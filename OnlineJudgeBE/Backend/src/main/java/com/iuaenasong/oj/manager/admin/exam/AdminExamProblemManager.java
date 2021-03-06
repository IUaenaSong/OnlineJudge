/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.manager.admin.exam;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.crawler.problem.ProblemStrategy;
import com.iuaenasong.oj.dao.exam.ExamEntityService;
import com.iuaenasong.oj.dao.exam.ExamProblemEntityService;
import com.iuaenasong.oj.dao.judge.JudgeEntityService;
import com.iuaenasong.oj.dao.problem.ProblemEntityService;
import com.iuaenasong.oj.manager.admin.problem.RemoteProblemManager;
import com.iuaenasong.oj.pojo.dto.ExamProblemDto;
import com.iuaenasong.oj.pojo.dto.ProblemDto;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.entity.exam.ExamProblem;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.utils.Constants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdminExamProblemManager {

    @Autowired
    private ExamProblemEntityService examProblemEntityService;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private JudgeEntityService judgeEntityService;

    @Autowired
    private RemoteProblemManager remoteProblemManager;

    @Autowired
    private ExamEntityService examEntityService;

    public HashMap<String, Object> getProblemList(Integer limit, Integer currentPage, String keyword,
                                                  Long eid, Integer problemType, String oj) {
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        IPage<Problem> iPage = new Page<>(currentPage, limit);
        // ??????eid???ExamProblem?????????????????????pid??????
        QueryWrapper<ExamProblem> examProblemQueryWrapper = new QueryWrapper<>();
        examProblemQueryWrapper.eq("eid", eid);
        List<Long> pidList = new LinkedList<>();

        List<ExamProblem> examProblemList = examProblemEntityService.list(examProblemQueryWrapper);
        HashMap<Long, ExamProblem> examProblemMap = new HashMap<>();
        examProblemList.forEach(examProblem -> {
            examProblemMap.put(examProblem.getPid(), examProblem);
            pidList.add(examProblem.getPid());
        });

        HashMap<String, Object> examProblem = new HashMap<>();

        QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();

        if (problemType != null) { // ???????????? ????????????????????????????????????
            problemQueryWrapper.eq("is_public", true)
                    // vj?????????????????????
                    .and(wrapper -> wrapper.eq("type", problemType)
                            .or().eq("is_remote", true))
                    .ne("auth", 2); // ???????????????????????????????????????????????????????????????????????????????????????????????????
            Exam exam = examEntityService.getById(eid);
            if (exam.getGid() != null) { //???????????????????????????????????????????????????
                problemQueryWrapper.ne("auth", 3);
            }
        }

        // ???????????????????????????????????????????????????in??????????????????????????????????????????not in
        if (problemType != null) {
            problemQueryWrapper.notIn(pidList.size() > 0, "id", pidList);
        } else {
            problemQueryWrapper.in(pidList.size() > 0, "id", pidList);
        }

        // ??????oj????????????
        if (oj != null && !"All".equals(oj)) {
            if (!Constants.RemoteOJ.isRemoteOJ(oj)) {
                problemQueryWrapper.eq("is_remote", false);
            } else {
                problemQueryWrapper.eq("is_remote", true).likeRight("problem_id", oj);
            }
        }

        if (!StringUtils.isEmpty(keyword)) {
            problemQueryWrapper.and(wrapper -> wrapper.like("title", keyword).or()
                    .like("problem_id", keyword).or()
                    .like("author", keyword));
        }

        if (pidList.size() == 0 && problemType == null) {
            problemQueryWrapper = new QueryWrapper<>();
            problemQueryWrapper.eq("id", null);
        }

        IPage<Problem> problemListPage = problemEntityService.page(iPage, problemQueryWrapper);

        if (pidList.size() > 0 && problemType == null) {
            List<Problem> problemList = problemListPage.getRecords();

            List<Problem> sortedProblemList = problemList.stream().sorted(Comparator.comparing(Problem::getId, (a, b) -> {
                ExamProblem x = examProblemMap.get(a);
                ExamProblem y = examProblemMap.get(b);
                if (x == null && y != null) {
                    return 1;
                } else if (x != null && y == null) {
                    return -1;
                } else if (x == null) {
                    return -1;
                } else {
                    return x.getDisplayId().compareTo(y.getDisplayId());
                }
            })).collect(Collectors.toList());
            problemListPage.setRecords(sortedProblemList);
        }

        examProblem.put("problemList", problemListPage);
        examProblem.put("examProblemMap", examProblemMap);

        return examProblem;
    }

    public Problem getProblem(Long pid) throws StatusFailException, StatusForbiddenException {

        Problem problem = problemEntityService.getById(pid);

        if (problem != null) { // ????????????
            // ???????????????????????????
            Session session = SecurityUtils.getSubject().getSession();
            UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

            boolean isRoot = SecurityUtils.getSubject().hasRole("root");
            boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
            // ?????????????????????????????????????????????????????????????????????
            if (!isRoot && !isProblemAdmin && !userRolesVo.getUsername().equals(problem.getAuthor())) {
                throw new StatusForbiddenException("???????????????????????????????????????");
            }

            return problem;
        } else {
            throw new StatusFailException("???????????????");
        }
    }

    public void deleteProblem(Long pid, Long eid) {
        //  ??????id??????null??????????????????????????????????????????
        if (eid != null) {
            QueryWrapper<ExamProblem> examProblemQueryWrapper = new QueryWrapper<>();
            examProblemQueryWrapper.eq("eid", eid).eq("pid", pid);
            examProblemEntityService.remove(examProblemQueryWrapper);
            // ??????????????????????????????????????????
            UpdateWrapper<Judge> judgeUpdateWrapper = new UpdateWrapper<>();
            judgeUpdateWrapper.eq("eid", eid).eq("pid", pid);
            judgeEntityService.remove(judgeUpdateWrapper);
        } else {

            problemEntityService.removeById(pid);
        }

        if (eid == null) {
            FileUtil.del(Constants.File.TESTCASE_BASE_FOLDER.getPath() + File.separator + "problem_" + pid);
        }
    }

    public Map<Object, Object> addProblem(ProblemDto problemDto) throws StatusFailException {

        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("problem_id", problemDto.getProblem().getProblemId().toUpperCase());
        Problem problem = problemEntityService.getOne(queryWrapper);
        if (problem != null) {
            throw new StatusFailException("????????????Problem ID????????????????????????");
        }
        // ?????????????????????
        problemDto.getProblem().setAuth(3);
        boolean isOk = problemEntityService.adminAddProblem(problemDto);
        if (isOk) { // ????????????
            // ????????????????????????id?????????????????????????????????
            return MapUtil.builder().put("pid", problemDto.getProblem().getId()).map();
        } else {
            throw new StatusFailException("????????????");
        }
    }

    public void updateProblem(ProblemDto problemDto) throws StatusForbiddenException, StatusFailException {
        // ???????????????????????????
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        // ?????????????????????????????????????????????????????????????????????
        if (!isRoot && !isProblemAdmin && !userRolesVo.getUsername().equals(problemDto.getProblem().getAuthor())) {
            throw new StatusForbiddenException("???????????????????????????????????????");
        }

        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("problem_id", problemDto.getProblem().getProblemId().toUpperCase());
        Problem problem = problemEntityService.getOne(queryWrapper);

        // ??????problem_id??????????????????????????????problem_id?????????????????????
        if (problem != null && problem.getId().longValue() != problemDto.getProblem().getId()) {
            throw new StatusFailException("?????????Problem ID ???????????????????????????????????????");
        }

        // ???????????????????????????
        problemDto.getProblem().setModifiedUser(userRolesVo.getUsername());

        boolean isOk = problemEntityService.adminUpdateProblem(problemDto);
        if (!isOk) {
            throw new StatusFailException("????????????");
        }
    }

    public ExamProblem getExamProblem(Long eid, Long pid) throws StatusFailException {
        QueryWrapper<ExamProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("eid", eid).eq("pid", pid);
        ExamProblem examProblem = examProblemEntityService.getOne(queryWrapper);
        if (examProblem == null) {
            throw new StatusFailException("????????????");
        }
        return examProblem;
    }

    public ExamProblem setExamProblem(ExamProblem examProblem) throws StatusFailException {
        boolean isOk = examProblemEntityService.saveOrUpdate(examProblem);
        if (isOk) {
            examProblemEntityService.syncExamRecord(examProblem.getPid(), examProblem.getEid(), examProblem.getDisplayId(), examProblem.getScore());
            return examProblem;
        } else {
            throw new StatusFailException("????????????");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addProblemFromPublic(ExamProblemDto examProblemDto) throws StatusFailException {

        Long pid = examProblemDto.getPid();
        Long eid = examProblemDto.getEid();
        String displayId = examProblemDto.getDisplayId();

        QueryWrapper<ExamProblem> examProblemQueryWrapper = new QueryWrapper<>();
        examProblemQueryWrapper.eq("eid", eid)
                .and(wrapper -> wrapper.eq("pid", pid)
                        .or()
                        .eq("display_id", displayId));
        ExamProblem examProblem = examProblemEntityService.getOne(examProblemQueryWrapper, false);
        if (examProblem != null) {
            throw new StatusFailException("????????????????????????????????????????????????????????????ID????????????");
        }

        // ???????????????????????????????????????
        Problem problem = problemEntityService.getById(pid);
        String displayName = problem.getTitle();

        // ?????????????????????
        boolean updateProblem = problemEntityService.saveOrUpdate(problem.setAuth(3));

        boolean isOk = examProblemEntityService.saveOrUpdate(new ExamProblem()
                .setEid(eid).setPid(pid).setDisplayTitle(displayName).setDisplayId(displayId));
        if (!isOk || !updateProblem) {
            throw new StatusFailException("????????????");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void importExamRemoteOJProblem(String name, String problemId, Long eid, String displayId) throws StatusFailException {
        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("problem_id", name.toUpperCase() + "-" + problemId);
        Problem problem = problemEntityService.getOne(queryWrapper, false);

        // ??????????????????????????????????????????
        if (problem == null) {
            Session session = SecurityUtils.getSubject().getSession();
            UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");
            try {
                ProblemStrategy.RemoteProblemInfo otherOJProblemInfo = remoteProblemManager.getOtherOJProblemInfo(name.toUpperCase(), problemId, userRolesVo.getUsername());
                if (otherOJProblemInfo != null) {
                    problem = remoteProblemManager.adminAddOtherOJProblem(otherOJProblemInfo, name);
                    if (problem == null) {
                        throw new StatusFailException("??????????????????????????????????????????");
                    }
                } else {
                    throw new StatusFailException("????????????????????????????????????????????????OJ????????????????????????????????????");
                }
            } catch (Exception e) {
                throw new StatusFailException(e.getMessage());
            }
        }

        QueryWrapper<ExamProblem> examProblemQueryWrapper = new QueryWrapper<>();
        Problem finalProblem = problem;
        examProblemQueryWrapper.eq("eid", eid)
                .and(wrapper -> wrapper.eq("pid", finalProblem.getId())
                        .or()
                        .eq("display_id", displayId));
        ExamProblem examProblem = examProblemEntityService.getOne(examProblemQueryWrapper, false);
        if (examProblem != null) {
            throw new StatusFailException("????????????????????????????????????????????????????????????ID????????????");
        }

        // ???????????????????????????????????????
        String displayName = problem.getTitle();

        // ?????????????????????
        boolean updateProblem = problemEntityService.saveOrUpdate(problem.setAuth(3));

        boolean isOk = examProblemEntityService.saveOrUpdate(new ExamProblem()
                .setEid(eid).setPid(problem.getId()).setDisplayTitle(displayName).setDisplayId(displayId));

        if (!isOk || !updateProblem) {
            throw new StatusFailException("????????????");
        }
    }

}