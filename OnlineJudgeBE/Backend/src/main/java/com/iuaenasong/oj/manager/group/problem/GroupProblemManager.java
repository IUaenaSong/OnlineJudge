/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.group.problem;

import cn.hutool.core.io.FileUtil;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.dao.group.GroupEntityService;
import com.iuaenasong.oj.dao.group.GroupMemberEntityService;
import com.iuaenasong.oj.dao.group.GroupProblemEntityService;
import com.iuaenasong.oj.dao.judge.JudgeEntityService;
import com.iuaenasong.oj.dao.problem.ProblemCaseEntityService;
import com.iuaenasong.oj.dao.problem.ProblemEntityService;
import com.iuaenasong.oj.dao.problem.TagEntityService;
import com.iuaenasong.oj.judge.Dispatcher;
import com.iuaenasong.oj.pojo.dto.ProblemDto;
import com.iuaenasong.oj.pojo.entity.group.Group;
import com.iuaenasong.oj.pojo.entity.judge.CompileDTO;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.entity.problem.ProblemCase;
import com.iuaenasong.oj.pojo.entity.problem.Tag;
import com.iuaenasong.oj.pojo.vo.ProblemVo;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.utils.Constants;
import com.iuaenasong.oj.validator.GroupValidator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Component
public class GroupProblemManager {

    @Autowired
    private GroupProblemEntityService groupProblemEntityService;

    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private JudgeEntityService judgeEntityService;

    @Autowired
    private ProblemCaseEntityService problemCaseEntityService;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private TagEntityService tagEntityService;

    @Autowired
    private Dispatcher dispatcher;

    @Value("${oj.judge.token}")
    private String judgeToken;

    public IPage<ProblemVo> getProblemList(Integer limit, Integer currentPage, Long gid) throws StatusNotFoundException, StatusForbiddenException {
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

        return groupProblemEntityService.getProblemList(limit, currentPage, gid);
    }

    public IPage<Problem> getAdminProblemList(Integer limit, Integer currentPage, Long gid) throws StatusNotFoundException, StatusForbiddenException {

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

        return groupProblemEntityService.getAdminProblemList(limit, currentPage, gid);
    }

    public Problem getProblem(Long pid) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Problem problem = problemEntityService.getById(pid);

        if (problem == null) {
            throw new StatusNotFoundException("该题目不存在！");
        }

        Long gid = problem.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUsername().equals(problem.getAuthor()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        return problem;
    }

    public void addProblem(ProblemDto problemDto) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
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

        QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.eq("problem_id", problemDto.getProblem().getProblemId().toUpperCase());
        Problem problem = problemEntityService.getOne(problemQueryWrapper);

        if (problem != null) {
            throw new StatusFailException("该题目的Problem ID已存在，请更换！");
        }

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
        if (!isOk) {
            throw new StatusFailException("添加失败");
        }
    }

    public void updateProblem(ProblemDto problemDto) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long pid = problemDto.getProblem().getId();

        Problem problem = problemEntityService.getById(pid);

        if (problem == null) {
            throw new StatusNotFoundException("该题目不存在！");
        }

        Long gid = problem.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUsername().equals(problem.getAuthor()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        problemDto.getProblem().setProblemId(group.getShortName() + problemDto.getProblem().getProblemId());

        String problemId = problemDto.getProblem().getProblemId().toUpperCase();

        QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.eq("problem_id", problemId);

        Problem problem1 = problemEntityService.getOne(problemQueryWrapper);

        problemDto.getProblem().setModifiedUser(userRolesVo.getUsername());

        if (problem1 != null && problem1.getId().longValue() != pid) {
            throw new StatusFailException("当前的Problem ID 已被使用，请重新更换新的！");
        }

        problemDto.getProblem().setIsPublic(problem.getIsPublic());

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

        boolean isOk = problemEntityService.adminUpdateProblem(problemDto);
        if (isOk) {
            if (problem1 == null) {
                UpdateWrapper<Judge> judgeUpdateWrapper = new UpdateWrapper<>();
                judgeUpdateWrapper.eq("pid", problemDto.getProblem().getId())
                        .set("display_pid", problemId);
                judgeEntityService.update(judgeUpdateWrapper);
            }
        } else {
            throw new StatusFailException("修改失败");
        }
    }

    public void deleteProblem(Long pid) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Problem problem = problemEntityService.getById(pid);

        if (problem == null) {
            throw new StatusNotFoundException("该题目不存在！");
        }

        Long gid = problem.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUsername().equals(problem.getAuthor()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        boolean isOk = problemEntityService.removeById(pid);
        if (isOk) {
            FileUtil.del(Constants.File.TESTCASE_BASE_FOLDER.getPath() + File.separator + "problem_" + pid);
        } else {
            throw new StatusFailException("删除失败！");
        }
    }

    public List<ProblemCase> getProblemCases(Long pid, Boolean isUpload) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Problem problem = problemEntityService.getById(pid);

        if (problem == null) {
            throw new StatusNotFoundException("该题目不存在！");
        }

        Long gid = problem.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUsername().equals(problem.getAuthor()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        QueryWrapper<ProblemCase> problemCaseQueryWrapper = new QueryWrapper<>();
        problemCaseQueryWrapper.eq("pid", pid).eq("status", 0);
        if (isUpload) {
            problemCaseQueryWrapper.last("order by length(input) asc,input asc");
        }
        return problemCaseEntityService.list(problemCaseQueryWrapper);
    }

    public List<Tag> getAllProblemTagsList(Long gid) throws StatusNotFoundException, StatusForbiddenException{
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

        List<Tag> tagList;
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.isNull("gid").or().eq("gid", gid);
        tagList = tagEntityService.list(tagQueryWrapper);

        return tagList;
    }

    public void compileSpj(CompileDTO compileDTO, Long gid) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
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

        if (StringUtils.isEmpty(compileDTO.getCode()) ||
                StringUtils.isEmpty(compileDTO.getLanguage())) {
            throw new StatusFailException("参数不能为空！");
        }

        compileDTO.setToken(judgeToken);
        dispatcher.dispatcher("compile", "/compile-spj", compileDTO);
    }

    public void compileInteractive(CompileDTO compileDTO, Long gid) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
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

        if (StringUtils.isEmpty(compileDTO.getCode()) ||
                StringUtils.isEmpty(compileDTO.getLanguage())) {
            throw new StatusFailException("参数不能为空！");
        }

        compileDTO.setToken(judgeToken);
        dispatcher.dispatcher("compile", "/compile-interactive", compileDTO);
    }

    public void changeProblemAuth(Long pid, Integer auth) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Problem problem = problemEntityService.getById(pid);

        if (problem == null) {
            throw new StatusNotFoundException("该题目不存在！");
        }

        Long gid = problem.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUsername().equals(problem.getAuthor()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        UpdateWrapper<Problem> problemUpdateWrapper = new UpdateWrapper<>();
        problemUpdateWrapper.eq("id", pid)
                .set("auth", auth)
                .set("modified_user", userRolesVo.getUsername());

        boolean isOk = problemEntityService.update(problemUpdateWrapper);
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }
}
