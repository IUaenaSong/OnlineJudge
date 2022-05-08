/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.admin.rejudge;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.iuaenasong.oj.dao.exam.ExamRecordEntityService;
import com.iuaenasong.oj.pojo.entity.exam.ExamRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.dao.contest.ContestRecordEntityService;
import com.iuaenasong.oj.dao.judge.JudgeCaseEntityService;
import com.iuaenasong.oj.dao.judge.JudgeEntityService;
import com.iuaenasong.oj.dao.problem.ProblemEntityService;
import com.iuaenasong.oj.dao.user.UserAcproblemEntityService;
import com.iuaenasong.oj.judge.remote.RemoteJudgeDispatcher;
import com.iuaenasong.oj.judge.self.JudgeDispatcher;
import com.iuaenasong.oj.pojo.entity.contest.ContestRecord;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.pojo.entity.judge.JudgeCase;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.entity.user.UserAcproblem;
import com.iuaenasong.oj.utils.Constants;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Component
public class RejudgeManager {

    @Resource
    private JudgeEntityService judgeEntityService;

    @Resource
    private UserAcproblemEntityService userAcproblemEntityService;

    @Resource
    private ContestRecordEntityService contestRecordEntityService;

    @Resource
    private ExamRecordEntityService examRecordEntityService;

    @Resource
    private JudgeCaseEntityService judgeCaseEntityService;

    @Resource
    private ProblemEntityService problemEntityService;

    @Resource
    private JudgeDispatcher judgeDispatcher;

    @Resource
    private RemoteJudgeDispatcher remoteJudgeDispatcher;

    @Transactional(rollbackFor = Exception.class)
    public Judge rejudge(Long submitId) throws StatusFailException {
        Judge judge = judgeEntityService.getById(submitId);

        boolean isContestSubmission = judge.getCid() != 0;
        boolean isExamSubmission = judge.getEid() != 0;
        boolean resetContestRecordResult = true;
        boolean resetExamRecordResult = true;

        // 如果是比赛题目
        if (isContestSubmission) {
            // 将对应比赛记录设置成默认值
            UpdateWrapper<ContestRecord> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("submit_id", submitId).setSql("status=null,score=null");
            resetContestRecordResult = contestRecordEntityService.update(updateWrapper);

        } else if (isExamSubmission) {
            // 将对应比赛记录设置成默认值
            UpdateWrapper<ExamRecord> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("submit_id", submitId).setSql("status=null,score=null");
            resetExamRecordResult = examRecordEntityService.update(updateWrapper);

        } else {
            // 重判前，需要将该题目对应记录表一并更新
            // 如果该题已经是AC通过状态，更新该题目的用户ac做题表 user_acproblem
            if (judge.getStatus().intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus().intValue()) {
                QueryWrapper<UserAcproblem> userAcproblemQueryWrapper = new QueryWrapper<>();
                userAcproblemQueryWrapper.eq("submit_id", judge.getSubmitId());
                userAcproblemEntityService.remove(userAcproblemQueryWrapper);
            }
        }

        // 清除该提交对应的测试点结果
        QueryWrapper<JudgeCase> judgeCaseQueryWrapper = new QueryWrapper<>();
        judgeCaseQueryWrapper.eq("submit_id", submitId);
        judgeCaseEntityService.remove(judgeCaseQueryWrapper);

        boolean hasSubmitIdRemoteRejudge = isHasSubmitIdRemoteRejudge(judge.getVjudgeSubmitId(), judge.getStatus());

        // 设置默认值
        judge.setStatus(Constants.Judge.STATUS_PENDING.getStatus()); // 开始进入判题队列
        judge.setVersion(judge.getVersion() + 1);
        judge.setJudger("")
                .setTime(null)
                .setMemory(null)
                .setErrorMessage(null)
                .setOiRankScore(null)
                .setScore(null);

        boolean result = judgeEntityService.updateById(judge);

        if (result && resetContestRecordResult && resetExamRecordResult) {
            // 调用判题服务
            Problem problem = problemEntityService.getById(judge.getPid());
            if (problem.getIsRemote()) { // 如果是远程oj判题
                remoteJudgeDispatcher.sendTask(judge, problem.getProblemId(),
                        isContestSubmission, isExamSubmission, hasSubmitIdRemoteRejudge);
            } else {
                judgeDispatcher.sendTask(judge, isContestSubmission, isExamSubmission);
            }
            return judge;
        } else {
            throw new StatusFailException("重判失败！请重新尝试！");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void rejudgeContestProblem(Long cid, Long pid) throws StatusFailException {
        QueryWrapper<Judge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", cid).eq("pid", pid);
        List<Judge> rejudgeList = judgeEntityService.list(queryWrapper);

        if (rejudgeList.size() == 0) {
            throw new StatusFailException("当前该题目无提交，不可重判！");
        }

        List<Long> submitIdList = new LinkedList<>();
        HashMap<Long, Integer> idMapStatus = new HashMap<>();
        // 全部设置默认值
        for (Judge judge : rejudgeList) {
            idMapStatus.put(judge.getSubmitId(), judge.getStatus());
            judge.setStatus(Constants.Judge.STATUS_PENDING.getStatus()); // 开始进入判题队列
            judge.setVersion(judge.getVersion() + 1);
            judge.setJudger("")
                    .setTime(null)
                    .setMemory(null)
                    .setErrorMessage(null)
                    .setOiRankScore(null)
                    .setScore(null);
            submitIdList.add(judge.getSubmitId());
        }
        boolean resetJudgeResult = judgeEntityService.updateBatchById(rejudgeList);
        // 清除每个提交对应的测试点结果
        QueryWrapper<JudgeCase> judgeCaseQueryWrapper = new QueryWrapper<>();
        judgeCaseQueryWrapper.in("submit_id", submitIdList);
        judgeCaseEntityService.remove(judgeCaseQueryWrapper);
        // 将对应比赛记录设置成默认值
        UpdateWrapper<ContestRecord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("submit_id", submitIdList).setSql("status=null,score=null");
        boolean resetContestRecordResult = contestRecordEntityService.update(updateWrapper);

        if (resetContestRecordResult && resetJudgeResult) {
            // 调用重判服务
            Problem problem = problemEntityService.getById(pid);
            if (problem.getIsRemote()) { // 如果是远程oj判题
                for (Judge judge : rejudgeList) {
                    // 进入重判队列，等待调用判题服务
                    remoteJudgeDispatcher.sendTask(judge, problem.getProblemId(),
                            judge.getCid() != 0,
                            judge.getEid() != 0,
                            isHasSubmitIdRemoteRejudge(judge.getVjudgeSubmitId(), idMapStatus.get(judge.getSubmitId())));
                }
            } else {
                for (Judge judge : rejudgeList) {
                    // 进入重判队列，等待调用判题服务
                    judgeDispatcher.sendTask(judge, judge.getCid() != 0, judge.getEid() != 0);
                }
            }
        } else {
            throw new StatusFailException("重判失败！请重新尝试！");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void rejudgeExamProblem(Long eid, Long pid) throws StatusFailException {
        QueryWrapper<Judge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("eid", eid).eq("pid", pid);
        List<Judge> rejudgeList = judgeEntityService.list(queryWrapper);

        if (rejudgeList.size() == 0) {
            throw new StatusFailException("当前该题目无提交，不可重判！");
        }

        List<Long> submitIdList = new LinkedList<>();
        HashMap<Long, Integer> idMapStatus = new HashMap<>();
        // 全部设置默认值
        for (Judge judge : rejudgeList) {
            idMapStatus.put(judge.getSubmitId(), judge.getStatus());
            judge.setStatus(Constants.Judge.STATUS_PENDING.getStatus()); // 开始进入判题队列
            judge.setVersion(judge.getVersion() + 1);
            judge.setJudger("")
                    .setTime(null)
                    .setMemory(null)
                    .setErrorMessage(null)
                    .setOiRankScore(null)
                    .setScore(null);
            submitIdList.add(judge.getSubmitId());
        }
        boolean resetJudgeResult = judgeEntityService.updateBatchById(rejudgeList);
        // 清除每个提交对应的测试点结果
        QueryWrapper<JudgeCase> judgeCaseQueryWrapper = new QueryWrapper<>();
        judgeCaseQueryWrapper.in("submit_id", submitIdList);
        judgeCaseEntityService.remove(judgeCaseQueryWrapper);
        // 将对应比赛记录设置成默认值
        UpdateWrapper<ExamRecord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("submit_id", submitIdList).setSql("status=null,score=null");
        boolean resetExamRecordResult = examRecordEntityService.update(updateWrapper);

        if (resetExamRecordResult && resetJudgeResult) {
            // 调用重判服务
            Problem problem = problemEntityService.getById(pid);
            if (problem.getIsRemote()) { // 如果是远程oj判题
                for (Judge judge : rejudgeList) {
                    // 进入重判队列，等待调用判题服务
                    remoteJudgeDispatcher.sendTask(judge, problem.getProblemId(),
                            judge.getCid() != 0,
                            judge.getEid() != 0,
                            isHasSubmitIdRemoteRejudge(judge.getVjudgeSubmitId(), idMapStatus.get(judge.getSubmitId())));
                }
            } else {
                for (Judge judge : rejudgeList) {
                    // 进入重判队列，等待调用判题服务
                    judgeDispatcher.sendTask(judge, judge.getCid() != 0, judge.getEid() != 0);
                }
            }
        } else {
            throw new StatusFailException("重判失败！请重新尝试！");
        }
    }

    private boolean isHasSubmitIdRemoteRejudge(Long vjudgeSubmitId, int status) {
        boolean isHasSubmitIdRemoteRejudge = false;
        if (vjudgeSubmitId != null &&
                (status == Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus()
                        || status == Constants.Judge.STATUS_COMPILING.getStatus()
                        || status == Constants.Judge.STATUS_PENDING.getStatus()
                        || status == Constants.Judge.STATUS_JUDGING.getStatus()
                        || status == Constants.Judge.STATUS_SYSTEM_ERROR.getStatus())) {
            isHasSubmitIdRemoteRejudge = true;
        }
        return isHasSubmitIdRemoteRejudge;
    }
}