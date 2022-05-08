/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.oj;

import com.iuaenasong.oj.dao.exam.ExamEntityService;
import com.iuaenasong.oj.dao.exam.ExamRecordEntityService;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.entity.exam.ExamRecord;
import com.iuaenasong.oj.validator.ExamValidator;
import com.iuaenasong.oj.validator.GroupValidator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.iuaenasong.oj.common.exception.StatusAccessDeniedException;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.judge.remote.RemoteJudgeDispatcher;
import com.iuaenasong.oj.judge.self.JudgeDispatcher;
import com.iuaenasong.oj.pojo.dto.SubmitIdListDto;
import com.iuaenasong.oj.pojo.dto.ToJudgeDto;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.entity.contest.ContestRecord;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.pojo.entity.judge.JudgeCase;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.entity.user.UserAcproblem;
import com.iuaenasong.oj.pojo.vo.JudgeVo;
import com.iuaenasong.oj.pojo.vo.SubmissionInfoVo;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.dao.contest.ContestEntityService;
import com.iuaenasong.oj.dao.contest.ContestRecordEntityService;
import com.iuaenasong.oj.dao.judge.JudgeCaseEntityService;
import com.iuaenasong.oj.dao.judge.JudgeEntityService;
import com.iuaenasong.oj.dao.problem.ProblemEntityService;
import com.iuaenasong.oj.dao.user.UserAcproblemEntityService;
import com.iuaenasong.oj.utils.Constants;
import com.iuaenasong.oj.utils.IpUtils;
import com.iuaenasong.oj.utils.RedisUtils;
import com.iuaenasong.oj.validator.ContestValidator;
import com.iuaenasong.oj.validator.JudgeValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
public class JudgeManager {
    @Autowired
    private JudgeEntityService judgeEntityService;

    @Autowired
    private JudgeCaseEntityService judgeCaseEntityService;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private ContestEntityService contestEntityService;

    @Autowired
    private ContestRecordEntityService contestRecordEntityService;

    @Autowired
    private ExamEntityService examEntityService;

    @Autowired
    private ExamRecordEntityService examRecordEntityService;

    @Autowired
    private UserAcproblemEntityService userAcproblemEntityService;

    @Autowired
    private JudgeDispatcher judgeDispatcher;

    @Autowired
    private RemoteJudgeDispatcher remoteJudgeDispatcher;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private JudgeValidator judgeValidator;

    @Autowired
    private ContestValidator contestValidator;

    @Autowired
    private ExamValidator examValidator;

    @Autowired
    private BeforeDispatchInitManager beforeDispatchInitManager;

    @Autowired
    private GroupValidator groupValidator;

    
    @Transactional(rollbackFor = Exception.class)
    public Judge submitProblemJudge(ToJudgeDto judgeDto) throws StatusForbiddenException, StatusFailException, StatusNotFoundException, StatusAccessDeniedException {

        judgeValidator.validateSubmissionInfo(judgeDto);

        // 需要获取一下该token对应用户的数据
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isContestSubmission = judgeDto.getCid() != 0;

        boolean isExamSubmission = judgeDto.getEid() != 0;

        boolean isTrainingSubmission = judgeDto.getTid() != null && judgeDto.getTid() != 0;

        if (!isContestSubmission) { // 非比赛提交限制8秒提交一次
            String lockKey = Constants.Account.SUBMIT_NON_CONTEST_LOCK.getCode() + userRolesVo.getUid();
            long count = redisUtils.incr(lockKey, 1);
            if (count > 1) {
                throw new StatusForbiddenException("对不起，您的提交频率过快，请稍后再尝试！");
            }
            redisUtils.expire(lockKey, 8);
        }

        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        // 将提交先写入数据库，准备调用判题服务器
        Judge judge = new Judge();
        judge.setShare(false) // 默认设置代码为单独自己可见
                .setCode(judgeDto.getCode())
                .setCid(judgeDto.getCid())
                .setEid(judgeDto.getEid())
                .setLanguage(judgeDto.getLanguage())
                .setLength(judgeDto.getCode().length())
                .setUid(userRolesVo.getUid())
                .setUsername(userRolesVo.getUsername())
                .setStatus(Constants.Judge.STATUS_PENDING.getStatus()) // 开始进入判题队列
                .setSubmitTime(new Date())
                .setVersion(0)
                .setIp(IpUtils.getUserIpAddr(request));

        // 如果比赛id不等于0，则说明为比赛提交
        if (isContestSubmission) {
            beforeDispatchInitManager.initContestSubmission(judgeDto.getCid(), judgeDto.getPid(), userRolesVo, judge);
        } else if (isExamSubmission) {
            beforeDispatchInitManager.initExamSubmission(judgeDto.getEid(), judgeDto.getPid(), userRolesVo, judge);
        } else if (isTrainingSubmission) {
            beforeDispatchInitManager.initTrainingSubmission(judgeDto.getTid(), judgeDto.getPid(), userRolesVo, judge);
        } else { // 如果不是比赛提交和训练提交
            beforeDispatchInitManager.initCommonSubmission(judgeDto.getPid(), judge);
        }

        // 将提交加入任务队列
        if (judgeDto.getIsRemote()) { // 如果是远程oj判题
            remoteJudgeDispatcher.sendTask(judge, judge.getDisplayPid(), isContestSubmission, isExamSubmission, false);
        } else {
            judgeDispatcher.sendTask(judge, isContestSubmission, isExamSubmission);
        }

        return judge;
    }

    
    @Transactional(rollbackFor = Exception.class)
    public Judge resubmit(Long submitId) throws StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Judge judge = judgeEntityService.getById(submitId);
        if (judge == null) {
            throw new StatusNotFoundException("此提交数据不存在！");
        }

        Problem problem = problemEntityService.getById(judge.getPid());

        if (!problem.getIsPublic()) {
            if (!isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), problem.getGid())) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        }

        // 如果是非比赛题目
        if (judge.getCid() != 0) {
            if (problem.getIsRemote()) {
                // 将对应比赛记录设置成默认值
                UpdateWrapper<ContestRecord> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("submit_id", submitId).setSql("status=null,score=null");
                contestRecordEntityService.update(updateWrapper);
            } else {
                throw new StatusNotFoundException("错误！非vJudge题目在比赛过程无权限重新提交");
            }

        } else if (judge.getEid() != 0) {
            if (problem.getIsRemote()) {
                // 将对应考试记录设置成默认值
                UpdateWrapper<ExamRecord> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("submit_id", submitId).setSql("status=null,score=null");
                examRecordEntityService.update(updateWrapper);
            } else {
                throw new StatusNotFoundException("错误！非vJudge题目在比赛过程无权限重新提交");
            }

        } else {
            // 重判前，需要将该题目对应记录表一并更新
            // 如果该题已经是AC通过状态，更新该题目的用户ac做题表 user_acproblem
            if (judge.getStatus().intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus().intValue()) {
                QueryWrapper<UserAcproblem> userAcproblemQueryWrapper = new QueryWrapper<>();
                userAcproblemQueryWrapper.eq("submit_id", judge.getSubmitId());
                userAcproblemEntityService.remove(userAcproblemQueryWrapper);
            }
        }

        boolean isHasSubmitIdRemoteRejudge = false;
        if (Objects.nonNull(judge.getVjudgeSubmitId()) &&
                (judge.getStatus().intValue() == Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus()
                        || judge.getStatus().intValue() == Constants.Judge.STATUS_PENDING.getStatus()
                        || judge.getStatus().intValue() == Constants.Judge.STATUS_JUDGING.getStatus()
                        || judge.getStatus().intValue() == Constants.Judge.STATUS_COMPILING.getStatus()
                        || judge.getStatus().intValue() == Constants.Judge.STATUS_SYSTEM_ERROR.getStatus())) {
            isHasSubmitIdRemoteRejudge = true;
        }

        // 重新进入等待队列
        judge.setStatus(Constants.Judge.STATUS_PENDING.getStatus());
        judge.setVersion(judge.getVersion() + 1);
        judge.setErrorMessage(null)
                .setOiRankScore(null)
                .setScore(null)
                .setTime(null)
                .setJudger("")
                .setMemory(null);
        judgeEntityService.updateById(judge);

        // 将提交加入任务队列
        if (problem.getIsRemote()) { // 如果是远程oj判题

            remoteJudgeDispatcher.sendTask(judge, problem.getProblemId(),
                    judge.getCid() != 0, judge.getEid() != 0, isHasSubmitIdRemoteRejudge);
        } else {
            judgeDispatcher.sendTask(judge, judge.getCid() != 0, judge.getEid() != 0);
        }
        return judge;
    }

    
    public SubmissionInfoVo getSubmission(Long submitId) throws StatusNotFoundException, StatusAccessDeniedException, StatusForbiddenException {

        Judge judge = judgeEntityService.getById(submitId);
        if (judge == null) {
            throw new StatusNotFoundException("此提交数据不存在！");
        }

        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root"); // 是否为超级管理员

        // 清空vj信息
        judge.setVjudgeUsername(null);
        judge.setVjudgeSubmitId(null);
        judge.setVjudgePassword(null);

        // 超级管理员与题目管理员有权限查看代码
        // 如果不是本人或者并未分享代码，则不可查看
        // 当此次提交代码不共享
        // 比赛提交只有比赛创建者和root账号可看代码

        SubmissionInfoVo submissionInfoVo = new SubmissionInfoVo();

        if (judge.getCid() != 0) {
            if (userRolesVo == null) {
                throw new StatusAccessDeniedException("请先登录！");
            }
            Contest contest = contestEntityService.getById(judge.getCid());
            if (!isRoot && !userRolesVo.getUid().equals(contest.getUid()) && !groupValidator.isGroupRoot(userRolesVo.getUid(), contest.getGid())) {
                // 如果是比赛,那么还需要判断是否为封榜,比赛管理员和超级管理员可以有权限查看(ACM题目除外)
                if (contest.getType().intValue() == Constants.Contest.TYPE_OI.getCode()
                        && contestValidator.isSealRank(userRolesVo.getUid(), contest, true, false)) {
                    submissionInfoVo.setSubmission(new Judge().setStatus(Constants.Judge.STATUS_SUBMITTED_UNKNOWN_RESULT.getStatus()));
                    return submissionInfoVo;
                }
                // 不是本人的话不能查看代码、时间，空间，长度
                if (!userRolesVo.getUid().equals(judge.getUid())) {
                    judge.setCode(null);
                    // 如果还在比赛时间，不是本人不能查看时间，空间，长度，错误提示信息
                    if (contest.getStatus().intValue() == Constants.Contest.STATUS_RUNNING.getCode()) {
                        judge.setTime(null);
                        judge.setMemory(null);
                        judge.setLength(null);
                        judge.setErrorMessage("The contest is in progress. You are not allowed to view other people's error information.");
                    }
                }
            }
        } else if (judge.getEid() != 0) {
            if (userRolesVo == null) {
                throw new StatusAccessDeniedException("请先登录！");
            }
            Exam exam = examEntityService.getById(judge.getEid());
            if (!isRoot && !userRolesVo.getUid().equals(exam.getUid()) && !groupValidator.isGroupRoot(userRolesVo.getUid(), exam.getGid())) {
                // 如果是比赛,那么还需要判断是否为封榜,比赛管理员和超级管理员可以有权限查看(ACM题目除外)
                if (!examValidator.isRealScore(userRolesVo.getUid(), exam, false)) {
                    if (judge.getStatus().intValue() == Constants.Judge.STATUS_COMPILE_ERROR.getStatus()) {
                        judge.setErrorMessage(null);
                    }
                    judge.setStatus(Constants.Judge.STATUS_SUBMITTED_UNKNOWN_RESULT.getStatus());
                    judge.setTime(null);
                    judge.setMemory(null);
                }
                // 不是本人的话不能查看代码、时间，空间，长度
                if (!userRolesVo.getUid().equals(judge.getUid())) {
                    judge.setCode(null);
                    // 如果还在比赛时间，不是本人不能查看时间，空间，长度，错误提示信息
                    if (exam.getStatus().intValue() == Constants.Exam.STATUS_RUNNING.getCode()) {
                        judge.setTime(null);
                        judge.setMemory(null);
                        judge.setLength(null);
                        judge.setLanguage(null);
                        judge.setUsername(null);
                        judge.setUid(null);
                        judge.setErrorMessage("The exam is in progress. You are not allowed to view other people's error information.");
                    }
                }
            }
        } else {
            Problem problem = problemEntityService.getById(judge.getPid());
            if (!judge.getIsPublic()) {
                if (userRolesVo == null) {
                    throw new StatusAccessDeniedException("请先登录！");
                }
                if (!isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), problem.getGid())) {
                    throw new StatusForbiddenException("对不起，您无权限操作！");
                }
            }
            boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");// 是否为题目管理员
            if (!judge.getShare() && !isRoot && !isProblemAdmin) {
                if (userRolesVo != null) { // 当前是登陆状态
                    // 需要判断是否为当前登陆用户自己的提交代码
                    if (!judge.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupRoot(userRolesVo.getUid(), problem.getGid())) {
                        judge.setCode(null);
                    }
                } else { // 不是登陆状态，就直接无权限查看代码
                    judge.setCode(null);
                }
            }
        }

        Problem problem = problemEntityService.getById(judge.getPid());

        // 只允许用户查看ce错误,sf错误，se错误信息提示
        if (judge.getStatus().intValue() != Constants.Judge.STATUS_COMPILE_ERROR.getStatus() &&
                judge.getStatus().intValue() != Constants.Judge.STATUS_SYSTEM_ERROR.getStatus() &&
                judge.getStatus().intValue() != Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus()) {
            judge.setErrorMessage("The error message does not support viewing.");
        }
        submissionInfoVo.setSubmission(judge);
        submissionInfoVo.setCodeShare(problem.getCodeShare());

        return submissionInfoVo;

    }

    
    public void updateSubmission(Judge judge) throws StatusForbiddenException, StatusFailException {

        // 需要获取一下该token对应用户的数据
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Problem problem = problemEntityService.getById(judge.getPid());

        if (!userRolesVo.getUid().equals(judge.getUid()) && !isRoot && !groupValidator.isGroupRoot(userRolesVo.getUid(), problem.getGid())) { // 判断该提交是否为当前用户的
            throw new StatusForbiddenException("对不起，您不能修改他人的代码分享权限！");
        }
        Judge judgeInfo = judgeEntityService.getById(judge.getSubmitId());
        if (judgeInfo.getCid() != 0 || judgeInfo.getEid() != 0) { // 如果是比赛提交，不可分享！
            throw new StatusForbiddenException("对不起，您不能分享比赛题目的提交代码！");
        }
        judgeInfo.setShare(judge.getShare());
        boolean isOk = judgeEntityService.updateById(judgeInfo);
        if (!isOk) {
            throw new StatusFailException("修改代码权限失败！");
        }
    }

    
    public IPage<JudgeVo> getJudgeList(Integer limit,
                                       Integer currentPage,
                                       Boolean onlyMine,
                                       String searchPid,
                                       Integer searchStatus,
                                       String searchUsername,
                                       Long gid,
                                       Boolean completeProblemID) throws StatusAccessDeniedException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        // 页数，每页题数若为空，设置默认值
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 30;

        String uid = null;
        // 只查看当前用户的提交
        if (onlyMine) {
            // 需要获取一下该token对应用户的数据（有token便能获取到）
            if (userRolesVo == null) {
                throw new StatusAccessDeniedException("当前用户数据为空，请您重新登陆！");
            }
            uid = userRolesVo.getUid();
        }
        if (searchPid != null) {
            searchPid = searchPid.trim();
        }
        if (searchUsername != null) {
            searchUsername = searchUsername.trim();
        }

        if (gid != null) {
            if (!isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                gid = null;
            }
        }

        return judgeEntityService.getCommonJudgeList(limit,
                currentPage,
                searchPid,
                searchStatus,
                searchUsername,
                uid,
                gid,
                completeProblemID);
    }

    
    public HashMap<Long, Object> checkCommonJudgeResult(SubmitIdListDto submitIdListDto) {

        List<Long> submitIds = submitIdListDto.getSubmitIds();

        if (CollectionUtils.isEmpty(submitIds)) {
            return new HashMap<>();
        }

        QueryWrapper<Judge> queryWrapper = new QueryWrapper<>();
        // lambada表达式过滤掉code
        queryWrapper.select(Judge.class, info -> !info.getColumn().equals("code")).in("submit_id", submitIds)
                .eq("cid", 0).eq("eid", 0);
        List<Judge> judgeList = judgeEntityService.list(queryWrapper);
        HashMap<Long, Object> result = new HashMap<>();
        for (Judge judge : judgeList) {
            judge.setCode(null);
            judge.setErrorMessage(null);
            judge.setVjudgeUsername(null);
            judge.setVjudgeSubmitId(null);
            judge.setVjudgePassword(null);
            result.put(judge.getSubmitId(), judge);
        }
        return result;
    }

    
    public HashMap<Long, Object> checkContestJudgeResult(SubmitIdListDto submitIdListDto) throws StatusNotFoundException {

        if (submitIdListDto.getCid() == null) {
            throw new StatusNotFoundException("查询比赛id不能为空");
        }

        if (CollectionUtils.isEmpty(submitIdListDto.getSubmitIds())) {
            return new HashMap<>();
        }

        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");
        boolean isRoot = SecurityUtils.getSubject().hasRole("root"); // 是否为超级管理员

        Contest contest = contestEntityService.getById(submitIdListDto.getCid());

        boolean isContestAdmin = isRoot || userRolesVo.getUid().equals(contest.getUid()) || groupValidator.isGroupRoot(userRolesVo.getUid(), contest.getGid());
        // 如果是封榜时间且不是比赛管理员和超级管理员
        boolean isSealRank = contestValidator.isSealRank(userRolesVo.getUid(), contest, true, isRoot);

        QueryWrapper<Judge> queryWrapper = new QueryWrapper<>();
        // lambada表达式过滤掉code
        queryWrapper.select(Judge.class, info -> !info.getColumn().equals("code"))
                .in("submit_id", submitIdListDto.getSubmitIds())
                .eq("cid", submitIdListDto.getCid())
                .between(isSealRank, "submit_time", contest.getStartTime(), contest.getSealRankTime());
        List<Judge> judgeList = judgeEntityService.list(queryWrapper);
        HashMap<Long, Object> result = new HashMap<>();
        for (Judge judge : judgeList) {
            judge.setCode(null);
            judge.setDisplayPid(null);
            judge.setErrorMessage(null);
            judge.setVjudgeUsername(null);
            judge.setVjudgeSubmitId(null);
            judge.setVjudgePassword(null);
            if (!judge.getUid().equals(userRolesVo.getUid()) && !isContestAdmin) {
                judge.setTime(null);
                judge.setMemory(null);
                judge.setLength(null);
            }
            result.put(judge.getSubmitId(), judge);
        }
        return result;
    }

    public HashMap<Long, Object> checkExamJudgeResult(SubmitIdListDto submitIdListDto) throws StatusNotFoundException {

        if (submitIdListDto.getEid() == null) {
            throw new StatusNotFoundException("查询考试id不能为空");
        }

        if (CollectionUtils.isEmpty(submitIdListDto.getSubmitIds())) {
            return new HashMap<>();
        }

        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");
        boolean isRoot = SecurityUtils.getSubject().hasRole("root"); // 是否为超级管理员

        Exam exam = examEntityService.getById(submitIdListDto.getEid());

        boolean isExamAdmin = isRoot || userRolesVo.getUid().equals(exam.getUid()) || groupValidator.isGroupRoot(userRolesVo.getUid(), exam.getGid());
        // 如果是封榜时间且不是比赛管理员和超级管理员
        boolean realScore = examValidator.isRealScore(userRolesVo.getUid(), exam, isRoot);

        QueryWrapper<Judge> queryWrapper = new QueryWrapper<>();
        // lambada表达式过滤掉code
        queryWrapper.select(Judge.class, info -> !info.getColumn().equals("code"))
                .in("submit_id", submitIdListDto.getSubmitIds())
                .eq("eid", submitIdListDto.getEid());
        List<Judge> judgeList = judgeEntityService.list(queryWrapper);
        HashMap<Long, Object> result = new HashMap<>();
        for (Judge judge : judgeList) {
            judge.setCode(null);
            judge.setDisplayPid(null);
            judge.setErrorMessage(null);
            judge.setVjudgeUsername(null);
            judge.setVjudgeSubmitId(null);
            judge.setVjudgePassword(null);
            if (!judge.getUid().equals(userRolesVo.getUid()) && !isExamAdmin) {
                judge.setTime(null);
                judge.setMemory(null);
                judge.setLength(null);
                if (!realScore) {
                    judge.setStatus(Constants.Judge.STATUS_SUBMITTED_UNKNOWN_RESULT.getStatus());
                    judge.setUsername(null);
                    judge.setUid(null);
                    judge.setLanguage(null);
                }
            }

            result.put(judge.getSubmitId(), judge);
        }
        return result;
    }
    
    @GetMapping("/get-all-case-result")
    public List<JudgeCase> getALLCaseResult(Long submitId) throws StatusNotFoundException, StatusForbiddenException {

        Judge judge = judgeEntityService.getById(submitId);

        if (judge == null) {
            throw new StatusNotFoundException("此提交数据不存在！");
        }

        Problem problem = problemEntityService.getById(judge.getPid());

        // 如果该题不支持开放测试点结果查看
        if (!problem.getOpenCaseResult()) {
            return null;
        }

        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");
        boolean isRoot = SecurityUtils.getSubject().hasRole("root"); // 是否为超级管理员

        if (judge.getCid() != 0 && userRolesVo != null && !isRoot) {
            Contest contest = contestEntityService.getById(judge.getCid());
            // 如果不是比赛管理员 比赛封榜不能看
            if (!contest.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupRoot(userRolesVo.getUid(), contest.getGid())) {
                // 当前是比赛期间 同时处于封榜时间
                if (contest.getSealRank() && contest.getStatus().intValue() == Constants.Contest.STATUS_RUNNING.getCode()
                        && contest.getSealRankTime().before(new Date())) {
                    return null;
                }

                // 若是比赛题目，只支持OI查看测试点情况，ACM强制禁止查看,比赛管理员除外
                if (problem.getType().intValue() == Constants.Contest.TYPE_ACM.getCode()) {
                    return null;
                }
            }
        }

        if (judge.getEid() != 0 && userRolesVo != null && !isRoot) {
            Exam exam = examEntityService.getById(judge.getEid());
            // 如果不是比赛管理员 比赛封榜不能看
            if (!exam.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupRoot(userRolesVo.getUid(), exam.getGid())) {
                // 当前是比赛期间 同时处于封榜时间
                if (!exam.getRealScore() && exam.getStatus().intValue() == Constants.Exam.STATUS_RUNNING.getCode()) {
                    return null;
                }

                // 若是比赛题目，只支持OI查看测试点情况，ACM强制禁止查看,比赛管理员除外
                if (problem.getType().intValue() == Constants.Contest.TYPE_ACM.getCode()) {
                    return null;
                }
            }
        }

        QueryWrapper<JudgeCase> wrapper = new QueryWrapper<>();

        if (userRolesVo == null) {
            return null;
        } else if (!isRoot
                && !SecurityUtils.getSubject().hasRole("admin")
                && !SecurityUtils.getSubject().hasRole("problem_admin")) {
            wrapper.select("time", "memory", "score", "status", "user_output");
        }
        wrapper.eq("submit_id", submitId)
                .last("order by length(input_data) asc,input_data asc");

        // 当前所有测试点只支持 空间 时间 状态码 IO得分 和错误信息提示查看而已
        return judgeCaseEntityService.list(wrapper);
    }
}