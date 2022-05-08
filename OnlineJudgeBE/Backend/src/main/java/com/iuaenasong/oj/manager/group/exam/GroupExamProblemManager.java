/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.manager.group.exam;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.dao.exam.ExamEntityService;
import com.iuaenasong.oj.dao.exam.ExamProblemEntityService;
import com.iuaenasong.oj.dao.group.GroupEntityService;
import com.iuaenasong.oj.dao.judge.JudgeEntityService;
import com.iuaenasong.oj.dao.problem.ProblemEntityService;
import com.iuaenasong.oj.manager.admin.exam.AdminExamProblemManager;
import com.iuaenasong.oj.pojo.dto.ExamProblemDto;
import com.iuaenasong.oj.pojo.dto.ProblemDto;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.entity.exam.ExamProblem;
import com.iuaenasong.oj.pojo.entity.group.Group;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.entity.problem.Tag;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.validator.GroupValidator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class GroupExamProblemManager {

    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private ExamEntityService examEntityService;

    @Autowired
    private AdminExamProblemManager adminExamProblemManager;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private ExamProblemEntityService examProblemEntityService;

    @Autowired
    private JudgeEntityService judgeEntityService;

    @Autowired
    private GroupValidator groupValidator;

    public HashMap<String, Object> getExamProblemList(Integer limit, Integer currentPage, String keyword, Long eid, Integer problemType, String oj) throws StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Exam exam = examEntityService.getById(eid);

        if (exam == null) {
            throw new StatusNotFoundException("该考试不存在！");
        }

        Long gid = exam.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(exam.getUid()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        return adminExamProblemManager.getProblemList(limit, currentPage, keyword, eid, problemType, oj);
    }

    public Map<Object, Object> addProblem(ProblemDto problemDto) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long gid = problemDto.getProblem().getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupAdmin(userRolesVo.getUid(), gid) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        problemDto.getProblem().setProblemId(group.getShortName() + problemDto.getProblem().getProblemId());

        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("problem_id", problemDto.getProblem().getProblemId().toUpperCase());

        Problem problem = problemEntityService.getOne(queryWrapper);
        if (problem != null) {
            throw new StatusFailException("该题目的Problem ID已存在，请更换！");
        }

        problemDto.getProblem().setAuth(3);
        problemDto.getProblem().setIsPublic(false);

        List<Tag> tagList = new LinkedList<>();
        for (Tag tag : problemDto.getTags()) {
            if (tag.getGid() != null && tag.getGid().longValue() != gid) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }

            if (tag.getId() == null) {
                tag.setGid(gid);
            }

            tagList.add(tag);
        }

        problemDto.setTags(tagList);

        boolean isOk = problemEntityService.adminAddProblem(problemDto);
        if (isOk) {
            return MapUtil.builder().put("pid", problemDto.getProblem().getId()).map();
        } else {
            throw new StatusFailException("添加失败");
        }
    }

    public ExamProblem getExamProblem(Long pid, Long eid) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Exam exam = examEntityService.getById(eid);

        if (exam == null) {
            throw new StatusNotFoundException("该考试不存在！");
        }

        Long gid = exam.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(exam.getUid()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        QueryWrapper<ExamProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("eid", eid).eq("pid", pid);

        ExamProblem examProblem = examProblemEntityService.getOne(queryWrapper);
        if (examProblem == null) {
            throw new StatusFailException("该考试题目不存在！");
        }
        return examProblem;
    }

    public void updateExamProblem(ExamProblem examProblem) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long eid = examProblem.getEid();

        Exam exam = examEntityService.getById(eid);

        if (exam == null) {
            throw new StatusNotFoundException("该考试不存在！");
        }

        Long gid = exam.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(exam.getUid()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        Problem problem = problemEntityService.getById(examProblem.getPid());

        if (examProblem.getScore() == null) {
            examProblem.setScore(problem.getIoScore());
        }

        boolean isOk = examProblemEntityService.saveOrUpdate(examProblem);
        if (isOk) {
            examProblemEntityService.syncExamRecord(examProblem.getPid(), examProblem.getEid(), examProblem.getDisplayId(), examProblem.getScore());
        } else {
            throw new StatusFailException("更新失败！");
        }
    }

    public void deleteExamProblem(Long pid, Long eid) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Exam exam = examEntityService.getById(eid);

        if (exam == null) {
            throw new StatusNotFoundException("该考试不存在！");
        }

        Long gid = exam.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(exam.getUid()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        QueryWrapper<ExamProblem> examProblemQueryWrapper = new QueryWrapper<>();
        examProblemQueryWrapper.eq("eid", eid).eq("pid", pid);
        Boolean isOk = examProblemEntityService.remove(examProblemQueryWrapper);
        if (isOk) {
            UpdateWrapper<Judge> judgeUpdateWrapper = new UpdateWrapper<>();
            judgeUpdateWrapper.eq("eid", eid).eq("pid", pid);
            judgeEntityService.remove(judgeUpdateWrapper);
        } else {
            throw new StatusFailException("删除失败！");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addProblemFromPublic(ExamProblemDto examProblemDto) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long pid = examProblemDto.getPid();

        Problem problem = problemEntityService.getById(pid);

        if (problem == null || problem.getAuth() != 1 || !problem.getIsPublic()) {
            throw new StatusNotFoundException("该题目不存在或已被隐藏！");
        }

        Long eid = examProblemDto.getEid();

        Exam exam = examEntityService.getById(eid);

        if (exam == null) {
            throw new StatusNotFoundException("该考试不存在！");
        }

        if (problem.getType() != 1 && !problem.getIsRemote()) {
            throw new StatusFailException("题目类型不符！");
        }

        Long gid = exam.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(exam.getUid()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        String displayId = examProblemDto.getDisplayId();

        QueryWrapper<ExamProblem> examProblemQueryWrapper = new QueryWrapper<>();
        examProblemQueryWrapper.eq("eid", eid)
                .and(wrapper -> wrapper.eq("pid", pid)
                        .or()
                        .eq("display_id", displayId));
        ExamProblem examProblem = examProblemEntityService.getOne(examProblemQueryWrapper, false);
        if (examProblem != null) {
            throw new StatusFailException("添加失败，该题目已添加或者题目的考试展示ID已存在！");
        }

        String displayName = problem.getTitle();

        ExamProblem newCProblem = new ExamProblem();

        newCProblem.setEid(eid).setPid(pid).setDisplayTitle(displayName).setDisplayId(displayId).setScore(problem.getIoScore());

        boolean isOk = examProblemEntityService.saveOrUpdate(newCProblem);
        if (!isOk) {
            throw new StatusFailException("添加失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addProblemFromGroup(String problemId, Long eid, String displayId) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Exam exam = examEntityService.getById(eid);

        if (exam == null) {
            throw new StatusNotFoundException("该考试不存在！");
        }

        Long gid = exam.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(exam.getUid()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.eq("problem_id", problemId).eq("gid", gid);

        Problem problem = problemEntityService.getOne(problemQueryWrapper);

        if (problem == null) {
            throw new StatusNotFoundException("该题目不存在或不是团队题目！");
        }

        if (problem.getType() != 1 && !problem.getIsRemote()) {
            throw new StatusFailException("题目类型不符！");
        }

        QueryWrapper<ExamProblem> examProblemQueryWrapper = new QueryWrapper<>();
        examProblemQueryWrapper.eq("eid", eid)
                .and(wrapper -> wrapper.eq("pid", problem.getId())
                        .or()
                        .eq("display_id", displayId));

        ExamProblem examProblem = examProblemEntityService.getOne(examProblemQueryWrapper);
        if (examProblem != null) {
            throw new StatusFailException("添加失败，该题目已添加或者题目的考试展示ID已存在！");
        }

        ExamProblem newCProblem = new ExamProblem();
        String displayName = problem.getTitle();

        newCProblem.setEid(eid).setPid(problem.getId()).setDisplayTitle(displayName).setDisplayId(displayId).setScore(problem.getIoScore());

        boolean updateProblem = problemEntityService.saveOrUpdate(problem.setAuth(3));

        boolean isOk = examProblemEntityService.saveOrUpdate(newCProblem);
        if (!isOk || !updateProblem) {
            throw new StatusFailException("添加失败");
        }
    }
}
