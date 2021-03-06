/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.group.contest;

import cn.hutool.core.map.MapUtil;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.dao.contest.ContestEntityService;
import com.iuaenasong.oj.dao.contest.ContestProblemEntityService;
import com.iuaenasong.oj.dao.group.GroupEntityService;
import com.iuaenasong.oj.dao.judge.JudgeEntityService;
import com.iuaenasong.oj.dao.problem.ProblemEntityService;
import com.iuaenasong.oj.manager.admin.contest.AdminContestProblemManager;
import com.iuaenasong.oj.pojo.dto.ContestProblemDto;
import com.iuaenasong.oj.pojo.dto.ProblemDto;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.entity.contest.ContestProblem;
import com.iuaenasong.oj.pojo.entity.group.Group;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.entity.problem.Tag;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.validator.GroupValidator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class GroupContestProblemManager {

    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private ContestEntityService contestEntityService;

    @Autowired
    private AdminContestProblemManager adminContestProblemManager;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private ContestProblemEntityService contestProblemEntityService;

    @Autowired
    private JudgeEntityService judgeEntityService;

    @Autowired
    private GroupValidator groupValidator;

    public HashMap<String, Object> getContestProblemList(Integer limit, Integer currentPage, String keyword, Long cid, Integer problemType, String oj) throws StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = contest.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(contest.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        return adminContestProblemManager.getProblemList(limit, currentPage, keyword, cid, problemType, oj);
    }

    public Map<Object, Object> addProblem(ProblemDto problemDto) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long gid = problemDto.getProblem().getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupAdmin(userRolesVo.getUid(), gid) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        problemDto.getProblem().setProblemId(group.getShortName() + problemDto.getProblem().getProblemId());

        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("problem_id", problemDto.getProblem().getProblemId().toUpperCase());

        Problem problem = problemEntityService.getOne(queryWrapper);
        if (problem != null) {
            throw new StatusFailException("????????????Problem ID????????????????????????");
        }

        problemDto.getProblem().setAuth(3);
        problemDto.getProblem().setIsPublic(false);

        List<Tag> tagList = new LinkedList<>();
        for (Tag tag : problemDto.getTags()) {
            if (tag.getGid() != null && tag.getGid().longValue() != gid) {
                throw new StatusForbiddenException("?????????????????????????????????");
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
            throw new StatusFailException("????????????");
        }
    }

    public ContestProblem getContestProblem(Long pid, Long cid) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = contest.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(contest.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        QueryWrapper<ContestProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", cid).eq("pid", pid);

        ContestProblem contestProblem = contestProblemEntityService.getOne(queryWrapper);
        if (contestProblem == null) {
            throw new StatusFailException("???????????????????????????");
        }
        return contestProblem;
    }

    public void updateContestProblem(ContestProblem contestProblem) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long cid = contestProblem.getCid();

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = contest.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(contest.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        boolean isOk = contestProblemEntityService.saveOrUpdate(contestProblem);
        if (isOk) {
            contestProblemEntityService.syncContestRecord(contestProblem.getPid(), contestProblem.getCid(), contestProblem.getDisplayId());
        } else {
            throw new StatusFailException("???????????????");
        }
    }

    public void deleteContestProblem(Long pid, Long cid) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = contest.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(contest.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        QueryWrapper<ContestProblem> contestProblemQueryWrapper = new QueryWrapper<>();
        contestProblemQueryWrapper.eq("cid", cid).eq("pid", pid);
        Boolean isOk = contestProblemEntityService.remove(contestProblemQueryWrapper);
        if (isOk) {
            UpdateWrapper<Judge> judgeUpdateWrapper = new UpdateWrapper<>();
            judgeUpdateWrapper.eq("cid", cid).eq("pid", pid);
            judgeEntityService.remove(judgeUpdateWrapper);
        } else {
            throw new StatusFailException("???????????????");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addProblemFromPublic(ContestProblemDto contestProblemDto) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long pid = contestProblemDto.getPid();

        Problem problem = problemEntityService.getById(pid);

        if (problem == null || problem.getAuth() != 1 || !problem.getIsPublic()) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        Long cid = contestProblemDto.getCid();

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        if (problem.getType() != contest.getType() && !problem.getIsRemote()) {
            throw new StatusFailException("?????????????????????");
        }

        Long gid = contest.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(contest.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        String displayId = contestProblemDto.getDisplayId();

        QueryWrapper<ContestProblem> contestProblemQueryWrapper = new QueryWrapper<>();
        contestProblemQueryWrapper.eq("cid", cid)
                .and(wrapper -> wrapper.eq("pid", pid)
                        .or()
                        .eq("display_id", displayId));
        ContestProblem contestProblem = contestProblemEntityService.getOne(contestProblemQueryWrapper, false);
        if (contestProblem != null) {
            throw new StatusFailException("????????????????????????????????????????????????????????????ID????????????");
        }

        String displayName = problem.getTitle();

        ContestProblem newCProblem = new ContestProblem();

        boolean isOk = contestProblemEntityService.saveOrUpdate(newCProblem
                .setCid(cid).setPid(pid).setDisplayTitle(displayName).setDisplayId(displayId));
        if (!isOk) {
            throw new StatusFailException("????????????");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addProblemFromGroup(String problemId, Long cid, String displayId) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = contest.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(contest.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.eq("problem_id", problemId).eq("gid", gid);

        Problem problem = problemEntityService.getOne(problemQueryWrapper);

        if (problem == null) {
            throw new StatusNotFoundException("??????????????????????????????????????????");
        }

        if (problem.getType() != contest.getType() && !problem.getIsRemote()) {
            throw new StatusFailException("?????????????????????");
        }

        QueryWrapper<ContestProblem> contestProblemQueryWrapper = new QueryWrapper<>();
        contestProblemQueryWrapper.eq("cid", cid)
                .and(wrapper -> wrapper.eq("pid", problem.getId())
                        .or()
                        .eq("display_id", displayId));

        ContestProblem contestProblem = contestProblemEntityService.getOne(contestProblemQueryWrapper);
        if (contestProblem != null) {
            throw new StatusFailException("????????????????????????????????????????????????????????????ID????????????");
        }

        ContestProblem newCProblem = new ContestProblem();
        String displayName = problem.getTitle();

        boolean updateProblem = problemEntityService.saveOrUpdate(problem.setAuth(3));

        boolean isOk = contestProblemEntityService.saveOrUpdate(newCProblem
                .setCid(cid).setPid(problem.getId()).setDisplayTitle(displayName).setDisplayId(displayId));
        if (!isOk || !updateProblem) {
            throw new StatusFailException("????????????");
        }
    }
}
