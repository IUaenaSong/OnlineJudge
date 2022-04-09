/**
 * @Author LengYun
 * @Since 2022/04/02 16:51
 * @Description
 */

package com.iuaenasong.oj.manager.group.question;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.dao.group.GroupEntityService;
import com.iuaenasong.oj.dao.group.GroupQuestionEntityService;
import com.iuaenasong.oj.dao.question.QuestionEntityService;
import com.iuaenasong.oj.pojo.entity.group.Group;
import com.iuaenasong.oj.pojo.entity.question.Question;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.validator.GroupValidator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class GroupQuestionManager {

    @Autowired
    private GroupQuestionEntityService groupQuestionEntityService;

    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private QuestionEntityService questionEntityService;

    @Autowired
    private GroupValidator groupValidator;

    public IPage<Question> getQuestionList(Integer limit, Integer currentPage, Integer type, Long gid) throws StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupMember(userRolesVo.getUid(), gid) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        if (type == null) type = 0;

        IPage<Question> iPage = new Page<>(currentPage, limit);

        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("gid", gid).eq("auth", 1);

        if (type != 0) {
            questionQueryWrapper.eq("type", type);
        }

        return groupQuestionEntityService.page(iPage, questionQueryWrapper);
    }

    public IPage<Question> getAdminQuestionList(Integer limit, Integer currentPage, Integer type, Long gid) throws StatusNotFoundException, StatusForbiddenException {

        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupAdmin(userRolesVo.getUid(), gid) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        if (type == null) type = 0;

        IPage<Question> iPage = new Page<>(currentPage, limit);

        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("gid", gid);

        if (type != 0) {
            questionQueryWrapper.eq("type", type);
        }

        return groupQuestionEntityService.page(iPage, questionQueryWrapper);
    }

    public Question getQuestion(Long qid) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Question question = questionEntityService.getById(qid);

        if (question == null) {
            throw new StatusNotFoundException("该问题不存在！");
        }

        Long gid = question.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUsername().equals(question.getAuthor()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        return question;
    }

    public void addQuestion(Question question) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long gid = question.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupAdmin(userRolesVo.getUid(), gid) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        question.setQuestionId(group.getShortName() + question.getQuestionId());

        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("question_id", question.getQuestionId().toUpperCase());
        Question question1 = questionEntityService.getOne(questionQueryWrapper);

        if (question1 != null) {
            throw new StatusFailException("该题目的Question ID已存在，请更换！");
        }

        question.setIsPublic(false);

        boolean isOk = questionEntityService.save(question);
        if (!isOk) {
            throw new StatusFailException("添加失败");
        }
    }

    public void updateQuestion(Question question) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long qid = question.getId();

        Question question1 = questionEntityService.getById(qid);

        if (question1 == null) {
            throw new StatusNotFoundException("该问题不存在！");
        }

        Long gid = question.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUsername().equals(question.getAuthor()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        question.setQuestionId(group.getShortName() + question.getQuestionId());

        String questionId = question.getQuestionId().toUpperCase();

        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("question_id", questionId);

        Question question2 = questionEntityService.getOne(questionQueryWrapper);

        question.setModifiedUser(userRolesVo.getUsername());

        if (question2 != null && question2.getId().longValue() != qid) {
            throw new StatusFailException("当前的Question ID 已被使用，请重新更换新的！");
        }

        question.setIsPublic(question1.getIsPublic());

        boolean isOk = questionEntityService.saveOrUpdate(question);
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

    public void deleteQuestion(Long qid) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Question question = questionEntityService.getById(qid);

        if (question == null) {
            throw new StatusNotFoundException("该题目不存在！");
        }

        Long gid = question.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUsername().equals(question.getAuthor()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        boolean isOk = questionEntityService.removeById(qid);
        if (!isOk) {
            throw new StatusFailException("删除失败！");
        }
    }

    public void changeQuestionAuth(Long qid, Integer auth) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Question question = questionEntityService.getById(qid);

        if (question == null) {
            throw new StatusNotFoundException("该问题不存在！");
        }

        Long gid = question.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUsername().equals(question.getAuthor()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        UpdateWrapper<Question> questionUpdateWrapper = new UpdateWrapper<>();
        questionUpdateWrapper.eq("id", qid)
                .set("auth", auth)
                .set("modified_user", userRolesVo.getUsername());

        boolean isOk = questionEntityService.update(questionUpdateWrapper);
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }
}
