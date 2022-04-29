/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.manager.group.exam;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.dao.exam.ExamEntityService;
import com.iuaenasong.oj.dao.exam.ExamRegisterEntityService;
import com.iuaenasong.oj.dao.group.GroupExamEntityService;
import com.iuaenasong.oj.dao.group.GroupEntityService;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.entity.exam.ExamRegister;
import com.iuaenasong.oj.pojo.entity.group.Group;
import com.iuaenasong.oj.pojo.vo.AdminExamVo;
import com.iuaenasong.oj.pojo.vo.ExamVo;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.utils.Constants;
import com.iuaenasong.oj.validator.GroupValidator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GroupExamManager {
    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private GroupExamEntityService groupExamEntityService;

    @Autowired
    private ExamEntityService examEntityService;

    @Autowired
    private ExamRegisterEntityService examRegisterEntityService;

    @Autowired
    private GroupValidator groupValidator;

    public IPage<ExamVo> getExamList(Integer limit, Integer currentPage, Long gid) throws StatusNotFoundException, StatusForbiddenException {
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

        return groupExamEntityService.getExamList(limit, currentPage, gid);
    }

    public IPage<Exam> getAdminExamList(Integer limit, Integer currentPage, Long gid) throws StatusNotFoundException, StatusForbiddenException {
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

        return groupExamEntityService.getAdminExamList(limit, currentPage, gid);
    }

    public AdminExamVo getExam(Long cid) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Exam exam = examEntityService.getById(cid);

        if (exam == null) {
            throw new StatusNotFoundException("该比赛不存在！");
        }

        Long gid = exam.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(exam.getUid()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        AdminExamVo adminExamVo = BeanUtil.copyProperties(exam, AdminExamVo.class, "starAccount");
        return adminExamVo;
    }

    public void addExam(AdminExamVo adminExamVo) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long gid = adminExamVo.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupAdmin(userRolesVo.getUid(), gid) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        Exam exam = BeanUtil.copyProperties(adminExamVo, Exam.class, "starAccount");

        exam.setIsPublic(false);

        boolean isOk = examEntityService.save(exam);
        if (!isOk) {
            throw new StatusFailException("添加失败");
        }
    }

    public void updateExam(AdminExamVo adminExamVo) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long cid = adminExamVo.getId();

        Exam exam = examEntityService.getById(cid);

        if (exam == null) {
            throw new StatusNotFoundException("该比赛不存在！");
        }

        Long gid = exam.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(exam.getUid()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        Exam exam1 = BeanUtil.copyProperties(adminExamVo, Exam.class, "starAccount");
        Exam oldExam = examEntityService.getById(exam1.getId());
        boolean isOk = examEntityService.saveOrUpdate(exam1);
        if (isOk) {
            if (!exam1.getAuth().equals(Constants.Exam.AUTH_PUBLIC.getCode())) {
                if (!Objects.equals(oldExam.getPwd(), exam1.getPwd())) {
                    UpdateWrapper<ExamRegister> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq("cid", exam1.getId());
                    examRegisterEntityService.remove(updateWrapper);
                }
            }
        } else {
            throw new StatusFailException("修改失败");
        }
    }

    public void deleteExam(Long cid) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Exam exam = examEntityService.getById(cid);

        if (exam == null) {
            throw new StatusNotFoundException("该比赛不存在！");
        }

        Long gid = exam.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(exam.getUid()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        boolean isOk = examEntityService.removeById(cid);
        if (!isOk) {
            throw new StatusFailException("删除失败");
        }
    }

    public void changeExamVisible(Long cid, Boolean visible) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Exam exam = examEntityService.getById(cid);

        if (exam == null) {
            throw new StatusNotFoundException("该比赛不存在！");
        }

        Long gid = exam.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(exam.getUid()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        UpdateWrapper<Exam> examUpdateWrapper = new UpdateWrapper<>();
        examUpdateWrapper.eq("id", cid).set("visible", visible);

        boolean isOK = examEntityService.update(examUpdateWrapper);
        if (!isOK) {
            throw new StatusFailException("修改失败");
        }
    }
}
