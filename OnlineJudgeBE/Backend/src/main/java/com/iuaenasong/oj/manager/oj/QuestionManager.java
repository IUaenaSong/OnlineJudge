/**
 * @Author LengYun
 * @Since 2022/04/02 16:51
 * @Description
 */

package com.iuaenasong.oj.manager.oj;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.dao.question.*;
import com.iuaenasong.oj.pojo.entity.question.*;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.validator.GroupValidator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuestionManager {
    @Autowired
    private QuestionEntityService questionEntityService;

    @Autowired
    private GroupValidator groupValidator;

    public Question getQuestionInfo(String questionId) throws StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        QueryWrapper<Question> wrapper = new QueryWrapper<Question>().eq("question_id", questionId);

        Question question = questionEntityService.getOne(wrapper, false);
        if (question == null) {
           throw new StatusNotFoundException("该题号对应的问题不存在");
        }
        if (question.getAuth() != 1) {
            throw new StatusForbiddenException("该题号对应问题并非公开问题，不支持访问！");
        }

        if (!question.getIsPublic()) {
            if (!groupValidator.isGroupMember(userRolesVo.getUid(), question.getGid()) && !isRoot) {
                throw new StatusForbiddenException("该题号对应问题并非公开问题，不支持访问！");
            }
        }

        if ((!question.getShare() || question.getAuth() == 3)
                && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(),question.getGid())
                && !userRolesVo.getUsername().equals(question.getAuthor())) {
            question.setAnswer(null).setJudge(null).setRadio(null);
            question.setChoices(question.getChoices().replaceAll("<status>([\\s\\S]*?)</status>", "<status>false</status>"));
        }
        return question;
    }

}