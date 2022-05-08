/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.manager.oj;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.dao.exam.*;
import com.iuaenasong.oj.dao.group.GroupMemberEntityService;
import com.iuaenasong.oj.dao.judge.JudgeEntityService;
import com.iuaenasong.oj.dao.problem.*;
import com.iuaenasong.oj.dao.question.QuestionEntityService;
import com.iuaenasong.oj.dao.user.UserInfoEntityService;
import com.iuaenasong.oj.mapper.ExamRecordMapper;
import com.iuaenasong.oj.pojo.dto.RegisterExamDto;
import com.iuaenasong.oj.pojo.dto.SubmitQuestionDto;
import com.iuaenasong.oj.pojo.entity.exam.*;
import com.iuaenasong.oj.pojo.entity.problem.*;
import com.iuaenasong.oj.pojo.entity.question.Question;
import com.iuaenasong.oj.pojo.vo.*;
import com.iuaenasong.oj.utils.Constants;
import com.iuaenasong.oj.validator.ExamValidator;
import com.iuaenasong.oj.validator.GroupValidator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExamManager {

    @Autowired
    private ExamEntityService examEntityService;

    @Autowired
    private ExamRegisterEntityService examRegisterEntityService;

    @Autowired
    private ExamProblemEntityService examProblemEntityService;

    @Autowired
    private ExamRecordEntityService examRecordEntityService;

    @Autowired
    private ExamQuestionEntityService examQuestionEntityService;

    @Autowired
    private ExamQuestionRecordEntityService examQuestionRecordEntityService;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private QuestionEntityService questionEntityService;

    @Autowired
    private ProblemTagEntityService problemTagEntityService;

    @Autowired
    private TagEntityService tagEntityService;

    @Autowired
    private ProblemLanguageEntityService problemLanguageEntityService;

    @Autowired
    private LanguageEntityService languageEntityService;

    @Autowired
    private CodeTemplateEntityService codeTemplateEntityService;

    @Autowired
    private JudgeEntityService judgeEntityService;

    @Autowired
    private UserInfoEntityService userInfoEntityService;

    @Autowired
    private GroupMemberEntityService groupMemberEntityService;

    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Autowired
    private ExamValidator examValidator;

    @Autowired
    private GroupValidator groupValidator;

    public ExamVo getExamInfo(Long eid) throws StatusFailException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        ExamVo examInfo = examEntityService.getExamInfoById(eid);
        if (examInfo == null) {
            throw new StatusFailException("对不起，该考试不存在!");
        }

        Exam exam = examEntityService.getById(eid);

        if (!exam.getIsPublic()) {
            if (!groupValidator.isGroupMember(userRolesVo.getUid(), exam.getGid()) && !isRoot) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        }

        // 设置当前服务器系统时间
        examInfo.setNow(new Date());

        return examInfo;
    }

    public void toRegisterExam(RegisterExamDto registerExamDto) throws StatusFailException, StatusForbiddenException {

        Long eid = registerExamDto.getEid();
        String password = registerExamDto.getPassword();
        if (eid == null || StringUtils.isEmpty(password)) {
            throw new StatusFailException("eid或者password不能为空！");
        }

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Exam exam = examEntityService.getById(eid);

        if (exam == null || !exam.getVisible()) {
            throw new StatusFailException("对不起，该考试不存在!");
        }

        if (!exam.getIsPublic()) {
            if (!groupValidator.isGroupMember(userRolesVo.getUid(), exam.getGid()) && !isRoot) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        }

        if (!exam.getPwd().equals(password)) { // 密码不对
            throw new StatusFailException("考试密码错误，请重新输入！");
        }

        // 需要校验当前考试是否开启账号规则限制，如果有，需要对当前用户的用户名进行验证
        if (exam.getOpenAccountLimit()
                && !examValidator.validateAccountRule(exam.getAccountLimitRule(), userRolesVo.getUsername())) {
            throw new StatusFailException("对不起！本次考试只允许特定账号规则的用户参赛！");
        }

        QueryWrapper<ExamRegister> wrapper = new QueryWrapper<ExamRegister>().eq("eid", eid)
                .eq("uid", userRolesVo.getUid());
        if (examRegisterEntityService.getOne(wrapper, false) != null) {
            throw new StatusFailException("您已注册过该考试，请勿重复注册！");
        }

        boolean isOk = examRegisterEntityService.saveOrUpdate(new ExamRegister()
                .setEid(eid)
                .setUid(userRolesVo.getUid()));

        if (!isOk) {
            throw new StatusFailException("校验考试密码失败，请稍后再试");
        }
    }

    public AccessVo getExamAccess(Long eid) throws StatusFailException {
        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        QueryWrapper<ExamRegister> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("eid", eid).eq("uid", userRolesVo.getUid());
        ExamRegister examRegister = examRegisterEntityService.getOne(queryWrapper, false);

        boolean access = false;
        if (examRegister != null) {
            access = true;
            Exam exam = examEntityService.getById(eid);
            if (exam == null || !exam.getVisible()) {
                throw new StatusFailException("对不起，该考试不存在!");
            }
            if (exam.getOpenAccountLimit()
                    && !examValidator.validateAccountRule(exam.getAccountLimitRule(), userRolesVo.getUsername())) {
                access = false;
            }
        }

        AccessVo accessVo = new AccessVo();
        accessVo.setAccess(access);
        return accessVo;
    }

    public List<ExamProblemVo> getExamProblemList(Long eid) throws StatusFailException, StatusForbiddenException {

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        // 获取本场考试的状态
        Exam exam = examEntityService.getById(eid);

        // 超级管理员或者该考试的创建者，则为考试管理者
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        // 需要对该考试做判断，是否处于开始或结束状态才可以获取题目列表，同时若是私有赛需要判断是否已注册（考试管理员包括超级管理员可以直接获取）
        examValidator.validateExamAuth(exam, userRolesVo, isRoot);

        List<ExamProblemVo> examProblemList;
        boolean isAdmin = isRoot || exam.getUid().equals(userRolesVo.getUid()) || groupValidator.isGroupRoot(userRolesVo.getUid(), exam.getGid());

        if (!exam.getIsPublic()) {
            if (!isRoot && !exam.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupMember(userRolesVo.getUid(), exam.getGid())) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        }

        examProblemList = examProblemEntityService.getExamProblemList(eid, exam.getStartTime(), exam.getEndTime(), isAdmin, exam.getUid());

        return examProblemList;
    }

    public ProblemInfoVo getExamProblemDetails(Long eid, String displayId) throws StatusFailException, StatusForbiddenException, StatusNotFoundException {

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        // 获取本场考试的状态
        Exam exam = examEntityService.getById(eid);

        // 是否为超级管理员或者该考试的创建者，则为考试管理者
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        // 需要对该考试做判断，是否处于开始或结束状态才可以获取题目，同时若是私有赛需要判断是否已注册（考试管理员包括超级管理员可以直接获取）
        examValidator.validateExamAuth(exam, userRolesVo, isRoot);

        // 根据eid和displayId获取pid
        QueryWrapper<ExamProblem> examProblemQueryWrapper = new QueryWrapper<>();
        examProblemQueryWrapper.eq("eid", eid).eq("display_id", displayId);
        ExamProblem examProblem = examProblemEntityService.getOne(examProblemQueryWrapper);

        if (examProblem == null) {
            throw new StatusNotFoundException("该考试题目不存在");
        }

        //查询题目详情，题目标签，题目语言，题目做题情况
        Problem problem = problemEntityService.getById(examProblem.getPid());

        if (problem.getAuth() == 2) {
            throw new StatusForbiddenException("该考试题目当前不可访问！");
        }

        // 设置考试题目的标题为设置展示标题
        problem.setTitle(examProblem.getDisplayTitle());
        problem.setIoScore(examProblem.getScore());

        List<Tag> tags = new LinkedList<>();

        // 考试结束后才开放标签和source、出题人、难度
        if (exam.getStatus().intValue() != Constants.Exam.STATUS_ENDED.getCode()) {
            problem.setSource(null);
            problem.setAuthor(null);
            problem.setDifficulty(null);
            QueryWrapper<ProblemTag> problemTagQueryWrapper = new QueryWrapper<>();
            problemTagQueryWrapper.eq("pid", examProblem.getPid());
            // 获取该题号对应的标签id
            List<Long> tidList = new LinkedList<>();
            problemTagEntityService.list(problemTagQueryWrapper).forEach(problemTag -> {
                tidList.add(problemTag.getTid());
            });
            if (tidList.size() != 0) {
                tags = (List<Tag>) tagEntityService.listByIds(tidList);
            }
        }
        // 记录 languageId对应的name
        HashMap<Long, String> tmpMap = new HashMap<>();

        // 获取题目提交的代码支持的语言
        List<String> languagesStr = new LinkedList<>();
        QueryWrapper<ProblemLanguage> problemLanguageQueryWrapper = new QueryWrapper<>();
        problemLanguageQueryWrapper.eq("pid", examProblem.getPid()).select("lid");
        List<Long> lidList = problemLanguageEntityService.list(problemLanguageQueryWrapper)
                .stream().map(ProblemLanguage::getLid).collect(Collectors.toList());
        languageEntityService.listByIds(lidList).forEach(language -> {
            languagesStr.add(language.getName());
            tmpMap.put(language.getId(), language.getName());
        });

        // 筛去 考试管理员和超级管理员的提交
        List<String> superAdminUidList = userInfoEntityService.getSuperAdminUidList();
        superAdminUidList.add(exam.getUid());

        List<String> groupRootUidList = groupMemberEntityService.getGroupRootUidList(exam.getGid());
        if (!CollectionUtils.isEmpty(groupRootUidList)) {
            superAdminUidList.addAll(groupRootUidList);
        }

        boolean isAdmin = isRoot || exam.getUid().equals(userRolesVo.getUid()) || groupValidator.isGroupRoot(userRolesVo.getUid(), exam.getGid());
        // 获取题目的提交记录
        ProblemCountVo problemCount = judgeEntityService.getExamProblemCount(examProblem.getPid(), examProblem.getId(),
                examProblem.getEid(), exam.getStartTime(), exam.getEndTime(), isAdmin, superAdminUidList);

        // 获取题目的代码模板
        QueryWrapper<CodeTemplate> codeTemplateQueryWrapper = new QueryWrapper<>();
        codeTemplateQueryWrapper.eq("pid", problem.getId()).eq("status", true);
        List<CodeTemplate> codeTemplates = codeTemplateEntityService.list(codeTemplateQueryWrapper);
        HashMap<String, String> LangNameAndCode = new HashMap<>();
        if (codeTemplates.size() > 0) {
            for (CodeTemplate codeTemplate : codeTemplates) {
                LangNameAndCode.put(tmpMap.get(codeTemplate.getLid()), codeTemplate.getCode());
            }
        }
        // 将数据统一写入到一个Vo返回数据实体类中
        return new ProblemInfoVo(problem, tags, languagesStr, problemCount, LangNameAndCode);
    }

    public List<ExamQuestionVo> getExamQuestionList(Integer type, Long eid) throws StatusFailException, StatusForbiddenException {

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        // 获取本场考试的状态
        Exam exam = examEntityService.getById(eid);

        // 超级管理员或者该考试的创建者，则为考试管理者
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        // 需要对该考试做判断，是否处于开始或结束状态才可以获取题目列表，同时若是私有赛需要判断是否已注册（考试管理员包括超级管理员可以直接获取）
        examValidator.validateExamAuth(exam, userRolesVo, isRoot);

        List<ExamQuestionVo> examQuestionList;
        boolean isAdmin = isRoot || exam.getUid().equals(userRolesVo.getUid()) || groupValidator.isGroupRoot(userRolesVo.getUid(), exam.getGid());

        if (!exam.getIsPublic()) {
            if (!isRoot && !exam.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupMember(userRolesVo.getUid(), exam.getGid())) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        }

        examQuestionList = examQuestionEntityService.getExamQuestionList(type, eid, exam.getStartTime(), exam.getEndTime(), isAdmin, exam.getUid());
        return examQuestionList;
    }

    public Question getExamQuestionDetails(Long eid, String displayId) throws StatusFailException, StatusForbiddenException, StatusNotFoundException {

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        // 获取本场考试的状态
        Exam exam = examEntityService.getById(eid);

        // 是否为超级管理员或者该考试的创建者，则为考试管理者
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        // 需要对该考试做判断，是否处于开始或结束状态才可以获取题目，同时若是私有赛需要判断是否已注册（考试管理员包括超级管理员可以直接获取）
        examValidator.validateExamAuth(exam, userRolesVo, isRoot);

        // 根据eid和displayId获取pid
        QueryWrapper<ExamQuestion> examQuestionQueryWrapper = new QueryWrapper<>();
        examQuestionQueryWrapper.eq("eid", eid).eq("display_id", displayId);
        ExamQuestion examQuestion = examQuestionEntityService.getOne(examQuestionQueryWrapper);

        if (examQuestion == null) {
            throw new StatusNotFoundException("该考试问题不存在");
        }

        //查询题目详情，题目标签，题目语言，题目做题情况
        Question question = questionEntityService.getById(examQuestion.getQid());

        if (question.getAuth() == 2) {
            throw new StatusForbiddenException("该考试问题当前不可访问！");
        }

        question.setQuestionId(examQuestion.getDisplayId());
        question.setScore(examQuestion.getScore());

        // 考试结束后才开放答案
        if (!isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(),question.getGid())
                && !userRolesVo.getUid().equals(exam.getUid())) {
            if (exam.getStatus().intValue() != Constants.Exam.STATUS_ENDED.getCode()) {
                question.setAuthor(null).setAnswer(null).setJudge(null).setRadio(null);
                question.setChoices(question.getChoices().replaceAll("<status>([\\s\\S]*?)</status>", "<status>false</status>"));

            } else if ((!question.getShare() || question.getAuth() == 3)) {
                question.setAnswer(null).setJudge(null).setRadio(null);
                question.setChoices(question.getChoices().replaceAll("<status>([\\s\\S]*?)</status>", "<status>false</status>"));
            }
        }

        return question;
    }

    public List<String> getExamQuestionStatus(Long eid) throws StatusFailException, StatusForbiddenException {

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        if (userRolesVo == null) {
            throw new StatusFailException("请先登录！");
        }

        QueryWrapper<ExamQuestionRecord> examQuestionRecordQueryWrapper = new QueryWrapper<>();
        examQuestionRecordQueryWrapper.select("display_id").eq("eid", eid).eq("uid", userRolesVo.getUid());

        List<ExamQuestionRecord> questionRecordList = examQuestionRecordEntityService.list(examQuestionRecordQueryWrapper);

        List<String> questionStatus = questionRecordList.stream().map(ExamQuestionRecord::getDisplayId).collect(Collectors.toList());
        return questionStatus;
    }

    public Void submitQuestion(SubmitQuestionDto submitQuestionDto) throws StatusFailException, StatusForbiddenException, StatusNotFoundException {

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        if (userRolesVo == null) {
            throw new StatusFailException("请先登录！");
        }

        Long eid = submitQuestionDto.getEid();

        QueryWrapper<ExamQuestion> examQuestionQueryWrapper = new QueryWrapper<>();
        examQuestionQueryWrapper.eq("eid", eid).eq("display_id", submitQuestionDto.getDisplayId());
        ExamQuestion examQuestion = examQuestionEntityService.getOne(examQuestionQueryWrapper);

        if (examQuestion == null) {
            throw new StatusNotFoundException("该题号对应的问题不存在！");
        }

        Question question = questionEntityService.getById(examQuestion.getQid());

        if (question.getAuth() == 2) {
            throw new StatusForbiddenException("该问题已被隐藏！");
        }

        Exam exam = examEntityService.getById(eid);

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        examValidator.validateExamAuth(exam, userRolesVo, isRoot);

        if (exam.getStatus().intValue() == Constants.Exam.STATUS_ENDED.getCode()) {
            throw new StatusForbiddenException("考试已结束！");
        }

        QueryWrapper<ExamQuestionRecord> examQuestionRecordQueryWrapper = new QueryWrapper<>();
        examQuestionRecordQueryWrapper.eq("eid", eid)
                .eq("qid", question.getId())
                .eq("uid", userRolesVo.getUid());

        ExamQuestionRecord examQuestionRecord = examQuestionRecordEntityService.getOne(examQuestionRecordQueryWrapper);

        ExamQuestionRecord examQuestionRecord1 = new ExamQuestionRecord();
        String answer = submitQuestionDto.getAnswer();
        Date now = new Date();
        examQuestionRecord1.setEid(eid)
                .setEqid(examQuestion.getId())
                .setQid(question.getId())
                .setUid(userRolesVo.getUid())
                .setUsername(userRolesVo.getUsername())
                .setRealname(userRolesVo.getRealname())
                .setDisplayId(submitQuestionDto.getDisplayId())
                .setSubmitTime(now)
                .setSubmitAnswer(answer);

        if (question.getType() == 1) {
            if (question.getSingle()) {
                if (question.getRadio() == null && answer.equals("") ||
                        question.getRadio() != null && question.getRadio().toString().equals(answer)) {
                    examQuestionRecord1.setScore(examQuestion.getScore()).setStatus(1);
                } else {
                    examQuestionRecord1.setScore(0).setStatus(-1);
                }
            } else {
                String choice = question.getChoices();
                String std = choice.replaceAll("<content>([\\s\\S]*?)</content>", "");
                std = std.replaceAll("<status>true</status>", "1");
                std = std.replaceAll("<status>false</status>", "0");

                int status = 0;
                if (std.length() < answer.length() || !answer.contains("1")) {
                    status = 2;
                } else {
                    for (int i = 0; i < answer.length(); i++) {
                        if (std.charAt(i) == '1' && answer.charAt(i) == '0') {
                            status = 1;
                        } else if (std.charAt(i) == '0' && answer.charAt(i) == '1') {
                            status = 2;
                            break;
                        }
                    }
                }
                if (status == 0) {
                    examQuestionRecord1.setScore(examQuestion.getScore()).setStatus(1);
                } else if (status == 1) {
                    examQuestionRecord1.setScore((examQuestion.getScore() + 1)  / 2).setStatus(0);
                } else {
                    examQuestionRecord1.setScore(0).setStatus(-1);
                }
            }
        } else if (question.getType() == 2) {
            if (question.getJudge() && answer.equals("true") || !question.getJudge() && answer.equals("false")) {
                examQuestionRecord1.setScore(examQuestion.getScore()).setStatus(1);
            } else {
                examQuestionRecord1.setScore(0).setStatus(-1);
            }
        } else {
            examQuestionRecord1.setScore(null).setStatus(null);
        }

        if (exam.getStatus().intValue() == Constants.Exam.STATUS_SCHEDULED.getCode()) {
            examQuestionRecord1.setTime(0L);
        } else {
            examQuestionRecord1.setTime(DateUtil.between(exam.getStartTime(), now, DateUnit.SECOND));
        }

        boolean isOk = false;
        if (examQuestionRecord == null) {
            isOk = examQuestionRecordEntityService.save(examQuestionRecord1);
        } else {
            examQuestionRecord1.setId(examQuestionRecord.getId());
            isOk = examQuestionRecordEntityService.updateById(examQuestionRecord1);
        }

        if (!isOk) {
            throw new StatusFailException("提交失败！");
        }
        return null;
    }

    public String getMyAnswer(String questionId, Long eid) throws StatusFailException, StatusForbiddenException, StatusNotFoundException {

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        if (userRolesVo == null) {
            return "";
        }

        QueryWrapper<ExamQuestion> examQuestionQueryWrapper = new QueryWrapper<>();
        examQuestionQueryWrapper.eq("eid", eid).eq("display_id", questionId);
        ExamQuestion examQuestion = examQuestionEntityService.getOne(examQuestionQueryWrapper);

        if (examQuestion == null) {
            throw new StatusNotFoundException("该题号对应的问题不存在！");
        }

        Question question = questionEntityService.getById(examQuestion.getQid());

        if (question.getAuth() == 2) {
            throw new StatusForbiddenException("该问题已被隐藏！");
        }

        Exam exam = examEntityService.getById(eid);

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        examValidator.validateExamAuth(exam, userRolesVo, isRoot);

        QueryWrapper<ExamQuestionRecord> examQuestionRecordQueryWrapper = new QueryWrapper<>();
        examQuestionRecordQueryWrapper.eq("eid", eid)
                .eq("qid", question.getId())
                .eq("uid", userRolesVo.getUid());

        ExamQuestionRecord examQuestionRecord = examQuestionRecordEntityService.getOne(examQuestionRecordQueryWrapper);

        if (examQuestionRecord == null) {
            return "";
        }
        return examQuestionRecord.getSubmitAnswer();
    }
    public IPage<JudgeVo> getExamSubmissionList(Integer limit,
                                                Integer currentPage,
                                                Boolean onlyMine,
                                                String displayId,
                                                Integer searchStatus,
                                                String searchUsername,
                                                Long searchEid,
                                                Boolean completeProblemID) throws StatusFailException, StatusForbiddenException {

        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");
        // 获取本场考试的状态
        Exam exam = examEntityService.getById(searchEid);

        // 是否为超级管理员或者该考试的创建者，则为考试管理者
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        // 需要对该考试做判断，是否处于开始或结束状态才可以获取题目，同时若是私有赛需要判断是否已注册（考试管理员包括超级管理员可以直接获取）
        examValidator.validateExamAuth(exam, userRolesVo, isRoot);

        // 页数，每页题数若为空，设置默认值
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 30;

        String uid = null;
        // 只查看当前用户的提交
        if (onlyMine) {
            // 需要获取一下该token对应用户的数据（有token便能获取到）
            uid = userRolesVo.getUid();
        }

        boolean isAdmin = exam.getUid().equals(userRolesVo.getUid()) || isRoot || groupValidator.isGroupRoot(userRolesVo.getUid(), exam.getGid());

        // OI考试封榜期间不更新，ACM考试封榜期间可看到自己的提交，但是其它人的不可见
        List<Integer> rightStatus = new ArrayList<>();
        rightStatus.add(-10);
        rightStatus.add(-5);
        rightStatus.add(4);
        rightStatus.add(5);
        rightStatus.add(6);
        rightStatus.add(7);
        rightStatus.add(9);
        rightStatus.add(10);
        if (!exam.getRealScore() && !isAdmin && !rightStatus.contains(searchStatus)) {
            searchStatus = null;
        }
        IPage<JudgeVo> examJudgeList = judgeEntityService.getExamJudgeList(limit,
                currentPage,
                displayId,
                searchEid,
                searchStatus,
                searchUsername,
                uid,
                isAdmin,
                exam.getStartTime(),
                exam.getEndTime(),
                completeProblemID);

        if (examJudgeList.getTotal() == 0) { // 未查询到一条数据
            return examJudgeList;
        } else {
            // 考试还是进行阶段，同时不是超级管理员与考试管理员，需要将除自己之外的提交的时间、空间、长度隐藏
            if (exam.getStatus().intValue() == Constants.Exam.STATUS_RUNNING.getCode()
                    && !isAdmin) {
                examJudgeList.getRecords().forEach(judgeVo -> {
                    if (!judgeVo.getUid().equals(userRolesVo.getUid())) {
                        judgeVo.setStatus(Constants.Judge.STATUS_SUBMITTED_UNKNOWN_RESULT.getStatus());
                        judgeVo.setScore(null);
                        judgeVo.setTime(null);
                        judgeVo.setMemory(null);
                        judgeVo.setLength(null);
                        judgeVo.setLanguage(null);
                        judgeVo.setUid(null);
                        judgeVo.setUsername(null);
                    }
                    if (!exam.getRealScore()) {
                        if (!rightStatus.contains(judgeVo.getStatus()))  {
                            judgeVo.setStatus(Constants.Judge.STATUS_SUBMITTED_UNKNOWN_RESULT.getStatus());
                        }
                        judgeVo.setStatus(Constants.Judge.STATUS_SUBMITTED_UNKNOWN_RESULT.getStatus());
                        judgeVo.setScore(null);
                        judgeVo.setTime(null);
                        judgeVo.setMemory(null);
                    }
                });
            }
            return examJudgeList;
        }
    }

    public Void rejudgeExamQuestion(Long qid, Long eid) throws StatusFailException, StatusForbiddenException, StatusNotFoundException {

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        Question question = questionEntityService.getById(qid);

        if (question.getType() != 1 &&question.getType() != 2) {
            throw new StatusFailException("该题目不支持重新评分！");
        }

        Exam exam = examEntityService.getById(eid);

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        boolean isAdmin = exam.getUid().equals(userRolesVo.getUid()) || isRoot || groupValidator.isGroupRoot(userRolesVo.getUid(), exam.getGid());

        if (!isAdmin) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        QueryWrapper<ExamQuestionRecord> examQuestionRecordQueryWrapper = new QueryWrapper<>();
        examQuestionRecordQueryWrapper.eq("eid", eid).eq("qid", qid);
        List<ExamQuestionRecord> rejudgeList = examQuestionRecordEntityService.list(examQuestionRecordQueryWrapper);

        if (rejudgeList.size() == 0) {
            throw new StatusFailException("当前问题无提交，不可重新评分！");
        }

        QueryWrapper<ExamQuestion> examQuestionQueryWrapper = new QueryWrapper<>();
        examQuestionQueryWrapper.eq("eid", eid).eq("qid", qid);
        ExamQuestion examQuestion = examQuestionEntityService.getOne(examQuestionQueryWrapper);

        for (ExamQuestionRecord examQuestionRecord : rejudgeList) {
            String answer = examQuestionRecord.getSubmitAnswer();
            if (question.getType() == 1) {
                if (question.getSingle()) {
                    if (question.getRadio() == null && answer.equals("") ||
                            question.getRadio() != null && question.getRadio().toString().equals(answer)) {
                        examQuestionRecord.setScore(examQuestion.getScore()).setStatus(1);
                    } else {
                        examQuestionRecord.setScore(0).setStatus(-1);
                    }
                } else {
                    String choice = question.getChoices();
                    String std = choice.replaceAll("<content>([\\s\\S]*?)</content>", "");
                    std = std.replaceAll("<status>true</status>", "1");
                    std = std.replaceAll("<status>false</status>", "0");

                    int status = 0;
                    if (std.length() < answer.length() || !answer.contains("1")) {
                        status = 2;
                    } else {
                        for (int i = 0; i < answer.length(); i++) {
                            if (std.charAt(i) == '1' && answer.charAt(i) == '0') {
                                status = 1;
                            } else if (std.charAt(i) == '0' && answer.charAt(i) == '1') {
                                status = 2;
                                break;
                            }
                        }
                    }
                    if (status == 0) {
                        examQuestionRecord.setScore(examQuestion.getScore()).setStatus(1);
                    } else if (status == 1) {
                        examQuestionRecord.setScore((examQuestion.getScore() + 1) / 2).setStatus(0);
                    } else {
                        examQuestionRecord.setScore(0).setStatus(-1);
                    }
                }
            } else if (question.getType() == 2) {
                if (question.getJudge() && answer.equals("true") || !question.getJudge() && answer.equals("false")) {
                    examQuestionRecord.setScore(examQuestion.getScore()).setStatus(1);
                } else {
                    examQuestionRecord.setScore(0).setStatus(-1);
                }
            }
            examQuestionRecordEntityService.updateById(examQuestionRecord);
        }

        return null;
    }

    public HashMap<String, Double> getExamProblemK(Long eid) throws StatusNotFoundException {
        Exam exam = examEntityService.getById(eid);

        if (exam == null) {
            throw new StatusNotFoundException("该考试不存在！");
        }

        List<ExamProblemKVo> examProblemKList = examRecordMapper.getExamProblemK(eid);

        HashMap<String, Double> kMap = new HashMap<String, Double>();

        for (ExamProblemKVo examProblemKVo : examProblemKList) {
            kMap.put(examProblemKVo.getDisplayId(), examProblemKVo.getK());
        }
        return kMap;
    }

    public List<ExamPaperVo> getExamPaperList(Long eid, String uid) throws StatusForbiddenException, StatusNotFoundException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        Exam exam = examEntityService.getById(eid);

        if (exam == null) {
            throw new StatusNotFoundException("该考试不存在！");
        }

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        boolean isAdmin = exam.getUid().equals(userRolesVo.getUid()) || isRoot || groupValidator.isGroupRoot(userRolesVo.getUid(), exam.getGid());

        if (!isAdmin && !(!StringUtils.isEmpty(uid) && uid.equals(userRolesVo.getUid())
                && exam.getAutoRealScore()
                && exam.getStatus().intValue() == Constants.Exam.STATUS_ENDED.getCode())) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        List<String> superAdminUidList = userInfoEntityService.getSuperAdminUidList();
        superAdminUidList.add(exam.getUid());

        List<String> groupRootUidList = groupMemberEntityService.getGroupRootUidList(exam.getGid());
        if (!CollectionUtils.isEmpty(groupRootUidList)) {
            superAdminUidList.addAll(groupRootUidList);
        }

        HashMap<String, ExamPaperVo> hashMap = new HashMap<String ,ExamPaperVo>();

        QueryWrapper<ExamQuestionRecord> examQuestionRecordQueryWrapper = new QueryWrapper<>();
        if (uid != null && !StringUtils.isEmpty(uid)) {
            examQuestionRecordQueryWrapper.eq("uid", uid);
        }
        examQuestionRecordQueryWrapper.eq("eid", eid)
                .notIn("uid", superAdminUidList)
                .between("submit_time", exam.getStartTime(), exam.getEndTime());

        List<ExamQuestionRecord> examQuestionRecordList = examQuestionRecordEntityService.list(examQuestionRecordQueryWrapper);

        for (ExamQuestionRecord examQuestionRecord : examQuestionRecordList) {
            ExamPaperVo examPaperVo = new ExamPaperVo();
            if (hashMap.containsKey(examQuestionRecord.getUid())) {
                examPaperVo = hashMap.get(examQuestionRecord.getUid());
            } else {
                examPaperVo.setUid(examQuestionRecord.getUid());
                examPaperVo.setUsername(examQuestionRecord.getUsername());
                examPaperVo.setRealname(examQuestionRecord.getRealname());
            }
            QuestionAnswerVo questionAnswerVo = new QuestionAnswerVo();
            questionAnswerVo.setScore(examQuestionRecord.getScore());
            questionAnswerVo.setAnswer(examQuestionRecord.getSubmitAnswer());
            examPaperVo.getExamQuestionRecordList()
                    .put(examQuestionRecord.getDisplayId(), questionAnswerVo);
            examPaperVo.setScore(examPaperVo.getScore() + (examQuestionRecord.getScore() != null ? examQuestionRecord.getScore() : 0));
            hashMap.put(examQuestionRecord.getUid(), examPaperVo);
        }

        HashMap<String, Double> kMap = getExamProblemK(eid);
        List<ExamRecordVo> examRecordList = new ArrayList<>();

        if (exam.getRankScoreType().equals(Constants.Exam.RANK_HIGHEST_SCORE.getName())) {
            examRecordList = examRecordMapper.getExamRecordByHighestSubmission(eid, uid, superAdminUidList, exam.getStartTime(), exam.getEndTime());
        } else {
            examRecordList = examRecordMapper.getExamRecordByRecentSubmission(eid, uid, superAdminUidList, exam.getStartTime(), exam.getEndTime());
        }

        for (ExamRecordVo examRecord : examRecordList) {
            ExamPaperVo examPaperVo = new ExamPaperVo();
            if (hashMap.containsKey(examRecord.getUid())) {
                examPaperVo = hashMap.get(examRecord.getUid());
            } else {
                examPaperVo.setUid(examRecord.getUid());
                examPaperVo.setUsername(examRecord.getUsername());
                examPaperVo.setRealname(examRecord.getRealname());
            }
            ProblemAnswerVo problemAnswerVo = new ProblemAnswerVo();
            problemAnswerVo.setScore((examRecord.getScore() != null ? examRecord.getScore() * kMap.get(examRecord.getDisplayId()) : null));
            problemAnswerVo.setCode(examRecord.getCode());
            problemAnswerVo.setTime(examRecord.getUseTime());
            examPaperVo.getExamRecordList()
                    .put(examRecord.getDisplayId(), problemAnswerVo);
            examPaperVo.setScore(examPaperVo.getScore()
                    + (examRecord.getScore() != null ? examRecord.getScore() : 0) * kMap.get(examRecord.getDisplayId()));
            examPaperVo.setTime(examPaperVo.getTime() + (examRecord.getUseTime() != null ? examRecord.getUseTime() : 0));
            hashMap.put(examRecord.getUid(), examPaperVo);
        }

        List<ExamPaperVo> examPaperList = new ArrayList<>();

        hashMap.forEach((key, value) -> {
            examPaperList.add(value);
        });
        examPaperList.stream().sorted(Comparator.comparing(ExamPaperVo::getScore, Comparator.reverseOrder())
                .thenComparing(ExamPaperVo::getScore));

        return examPaperList;
    }

    public Void submitScore(ExamPaperVo examPaperVo, Long eid) throws StatusFailException, StatusForbiddenException, StatusNotFoundException {

        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        Exam exam = examEntityService.getById(eid);

        if (exam == null) {
            throw new StatusNotFoundException("该考试不存在！");
        }

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        boolean isAdmin = exam.getUid().equals(userRolesVo.getUid()) || isRoot || groupValidator.isGroupRoot(userRolesVo.getUid(), exam.getGid());

        if (!isAdmin) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        String uid = examPaperVo.getUid();

        HashMap<String, QuestionAnswerVo> questionAnswerHashMap = examPaperVo.getExamQuestionRecordList();

        questionAnswerHashMap.forEach((key, value) -> {
            UpdateWrapper<ExamQuestionRecord> examQuestionRecordUpdateWrapper = new UpdateWrapper<>();
            examQuestionRecordUpdateWrapper.set("score", value.getScore())
                    .eq("eid", eid).eq("uid", uid).eq("display_id", key);

            examQuestionRecordEntityService.update(examQuestionRecordUpdateWrapper);

        });

        HashMap<String, ProblemAnswerVo> problemAnswerHashMap = examPaperVo.getExamRecordList();
        HashMap<String, Double> kMap = getExamProblemK(eid);

        List<ExamRecordVo> examRecordList = new ArrayList<>();

        if (exam.getRankScoreType().equals(Constants.Exam.RANK_HIGHEST_SCORE.getName())) {
            examRecordList = examRecordMapper.getExamRecordByHighestSubmission(eid, uid, null, exam.getStartTime(), exam.getEndTime());
        } else {
            examRecordList = examRecordMapper.getExamRecordByRecentSubmission(eid, uid, null, exam.getStartTime(), exam.getEndTime());
        }

        HashMap<String, Long> submitId = new HashMap<String, Long>();

        for (ExamRecordVo examRecordVo : examRecordList) {
            submitId.put(examRecordVo.getDisplayId(), examRecordVo.getSubmitId());
        }

        problemAnswerHashMap.forEach((key, value) -> {
            UpdateWrapper<ExamRecord> examRecordUpdateWrapper = new UpdateWrapper<>();
            examRecordUpdateWrapper.set("score", value.getScore() / kMap.get(key)).eq("submit_id", submitId.get(key));

            examRecordEntityService.update(examRecordUpdateWrapper);

        });

        return null;
    }
}