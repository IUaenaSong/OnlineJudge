/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.manager.group.exam;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.dao.exam.ExamEntityService;
import com.iuaenasong.oj.dao.exam.ExamQuestionEntityService;
import com.iuaenasong.oj.dao.group.GroupEntityService;
import com.iuaenasong.oj.dao.question.QuestionEntityService;
import com.iuaenasong.oj.manager.admin.exam.AdminExamQuestionManager;
import com.iuaenasong.oj.pojo.dto.ExamQuestionDto;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.entity.exam.ExamQuestion;
import com.iuaenasong.oj.pojo.entity.group.Group;
import com.iuaenasong.oj.pojo.entity.question.Question;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.validator.GroupValidator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
public class GroupExamQuestionManager {

    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private ExamEntityService examEntityService;

    @Autowired
    private AdminExamQuestionManager adminExamQuestionManager;

    @Autowired
    private QuestionEntityService questionEntityService;

    @Autowired
    private ExamQuestionEntityService examQuestionEntityService;

    @Autowired
    private GroupValidator groupValidator;

    public HashMap<String, Object> getExamQuestionList(Integer limit, Integer currentPage, String keyword, Long eid, Integer questionType, Boolean queryExisted) throws StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Exam exam = examEntityService.getById(eid);

        if (exam == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = exam.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(exam.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        return adminExamQuestionManager.getQuestionList(limit, currentPage, keyword, eid, questionType, queryExisted);
    }

    public Map<Object, Object> addQuestion(Question question) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long gid = question.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupAdmin(userRolesVo.getUid(), gid) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        question.setQuestionId(group.getShortName() + question.getQuestionId());

        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_id", question.getQuestionId().toUpperCase());

        Question question1 = questionEntityService.getOne(queryWrapper);
        if (question1 != null) {
            throw new StatusFailException("????????????Question ID????????????????????????");
        }

        question.setAuth(3);
        question.setIsPublic(false);

        boolean isOk = questionEntityService.save(question);
        if (isOk) {
            return MapUtil.builder().put("qid", question.getId()).map();
        } else {
            throw new StatusFailException("????????????");
        }
    }

    public ExamQuestion getExamQuestion(Long qid, Long eid) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Exam exam = examEntityService.getById(eid);

        if (exam == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = exam.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(exam.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        QueryWrapper<ExamQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("eid", eid).eq("qid", qid);

        ExamQuestion examQuestion = examQuestionEntityService.getOne(queryWrapper);
        if (examQuestion == null) {
            throw new StatusFailException("???????????????????????????");
        }
        return examQuestion;
    }

    public void updateExamQuestion(ExamQuestion examQuestion) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long eid = examQuestion.getEid();

        Exam exam = examEntityService.getById(eid);

        if (exam == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = exam.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(exam.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        Question question = questionEntityService.getById(examQuestion.getQid());

        if (examQuestion.getScore() == null) {
            examQuestion.setScore(question.getScore());
        }

        boolean isOk = examQuestionEntityService.saveOrUpdate(examQuestion);
        if (isOk) {
            examQuestionEntityService.syncExamQuestionRecord(examQuestion.getQid(), examQuestion.getEid(), examQuestion.getDisplayId());
        } else {
            throw new StatusFailException("???????????????");
        }
    }

    public void deleteExamQuestion(Long qid, Long eid) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Exam exam = examEntityService.getById(eid);

        if (exam == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = exam.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(exam.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        QueryWrapper<ExamQuestion> examQuestionQueryWrapper = new QueryWrapper<>();
        examQuestionQueryWrapper.eq("eid", eid).eq("qid", qid);
        Boolean isOk = examQuestionEntityService.remove(examQuestionQueryWrapper);
        if (!isOk) {
            throw new StatusFailException("???????????????");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addQuestionFromGroup(ExamQuestionDto examQuestionDto) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long qid = examQuestionDto.getQid();

        Question question = questionEntityService.getById(qid);

        if (question == null) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        Long eid = examQuestionDto.getEid();

        Exam exam = examEntityService.getById(eid);

        if (exam == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = exam.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(exam.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        String displayId = examQuestionDto.getDisplayId();

        QueryWrapper<ExamQuestion> examQuestionQueryWrapper = new QueryWrapper<>();
        examQuestionQueryWrapper.eq("eid", eid)
                .and(wrapper -> wrapper.eq("qid", qid)
                        .or()
                        .eq("display_id", displayId));
        ExamQuestion examQuestion = examQuestionEntityService.getOne(examQuestionQueryWrapper, false);
        if (examQuestion != null) {
            throw new StatusFailException("????????????????????????????????????????????????????????????ID????????????");
        }

        ExamQuestion newCQuestion = new ExamQuestion();

        newCQuestion.setEid(eid).setQid(qid).setDisplayId(displayId).setScore(question.getScore());

        boolean updateQuestion = questionEntityService.saveOrUpdate(question.setAuth(3));

        boolean isOk = examQuestionEntityService.saveOrUpdate(newCQuestion);
        if (!isOk || !updateQuestion) {
            throw new StatusFailException("????????????");
        }
    }

}
