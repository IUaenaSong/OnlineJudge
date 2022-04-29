/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.manager.oj;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.dao.exam.*;
import com.iuaenasong.oj.pojo.entity.exam.*;
import com.iuaenasong.oj.pojo.vo.*;
import com.iuaenasong.oj.validator.ExamValidator;
import com.iuaenasong.oj.validator.GroupValidator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ExamManager {

    @Autowired
    private ExamEntityService examEntityService;

    @Autowired
    private ExamRegisterEntityService examRegisterEntityService;

    @Autowired
    private ExamValidator examValidator;

    @Autowired
    private GroupValidator groupValidator;

    public ExamVo getExamInfo(Long cid) throws StatusFailException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        ExamVo examInfo = examEntityService.getExamInfoById(cid);
        if (examInfo == null) {
            throw new StatusFailException("对不起，该比赛不存在!");
        }

        Exam exam = examEntityService.getById(cid);

        if (!exam.getIsPublic()) {
            if (!groupValidator.isGroupMember(userRolesVo.getUid(), exam.getGid()) && !isRoot) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        }

        // 设置当前服务器系统时间
        examInfo.setNow(new Date());

        return examInfo;
    }

    public AccessVo getExamAccess(Long cid) throws StatusFailException {
        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        QueryWrapper<ExamRegister> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", cid).eq("uid", userRolesVo.getUid());
        ExamRegister examRegister = examRegisterEntityService.getOne(queryWrapper, false);

        boolean access = false;
        if (examRegister != null) {
            access = true;
            Exam exam = examEntityService.getById(cid);
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

}