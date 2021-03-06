/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.admin.training;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.crawler.problem.ProblemStrategy;
import com.iuaenasong.oj.manager.admin.problem.RemoteProblemManager;
import com.iuaenasong.oj.pojo.dto.TrainingProblemDto;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.entity.training.Training;
import com.iuaenasong.oj.pojo.entity.training.TrainingProblem;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.dao.problem.ProblemEntityService;
import com.iuaenasong.oj.dao.training.TrainingProblemEntityService;
import com.iuaenasong.oj.dao.training.TrainingEntityService;
import com.iuaenasong.oj.utils.Constants;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdminTrainingProblemManager {

    @Resource
    private TrainingProblemEntityService trainingProblemEntityService;

    @Resource
    private TrainingEntityService trainingEntityService;

    @Resource
    private ProblemEntityService problemEntityService;

    @Resource
    private AdminTrainingRecordManager adminTrainingRecordManager;

    @Resource
    private RemoteProblemManager remoteProblemManager;

    public HashMap<String, Object> getProblemList(Integer limit, Integer currentPage, String keyword, Boolean queryExisted, Long tid) {
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;

        IPage<Problem> iPage = new Page<>(currentPage, limit);
        // ??????tid???TrainingProblem?????????????????????pid??????
        QueryWrapper<TrainingProblem> trainingProblemQueryWrapper = new QueryWrapper<>();
        trainingProblemQueryWrapper.eq("tid", tid).orderByAsc("display_id");
        List<Long> pidList = new LinkedList<>();
        List<TrainingProblem> trainingProblemList = trainingProblemEntityService.list(trainingProblemQueryWrapper);
        HashMap<Long, TrainingProblem> trainingProblemMap = new HashMap<>();
        trainingProblemList.forEach(trainingProblem -> {
            if (!trainingProblemMap.containsKey(trainingProblem.getPid())) {
                trainingProblemMap.put(trainingProblem.getPid(), trainingProblem);
            }
            pidList.add(trainingProblem.getPid());
        });

        HashMap<String, Object> trainingProblem = new HashMap<>();

        QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();

        // ???????????????????????????????????????????????????in??????????????????????????????????????????not in
        if (queryExisted) {
            problemQueryWrapper.in(pidList.size() > 0, "id", pidList);
        } else {
            // ??????????????????????????????????????????????????????????????????
            problemQueryWrapper.eq("auth", 1).eq("is_public", true);
            problemQueryWrapper.notIn(pidList.size() > 0, "id", pidList);
        }

        if (!StringUtils.isEmpty(keyword)) {
            problemQueryWrapper.and(wrapper -> wrapper.like("title", keyword).or()
                    .like("problem_id", keyword).or()
                    .like("author", keyword));
        }

        if (pidList.size() == 0 && queryExisted) {
            problemQueryWrapper = new QueryWrapper<>();
            problemQueryWrapper.eq("id", null);
        }

        IPage<Problem> problemListPage = problemEntityService.page(iPage, problemQueryWrapper);

        if (queryExisted && pidList.size() > 0) {
            List<Problem> problemListPageRecords = problemListPage.getRecords();
            List<Problem> sortProblemList = problemListPageRecords
                    .stream()
                    .sorted(Comparator.comparingInt(problem -> trainingProblemMap.get(problem.getId()).getRank()))
                    .collect(Collectors.toList());
            problemListPage.setRecords(sortProblemList);
        }

        trainingProblem.put("problemList", problemListPage);
        trainingProblem.put("trainingProblemMap", trainingProblemMap);

        return trainingProblem;
    }

    public void updateProblem(TrainingProblem trainingProblem) throws StatusFailException {
        boolean isOk = trainingProblemEntityService.saveOrUpdate(trainingProblem);

        if (!isOk) {
            throw new StatusFailException("???????????????");
        }
    }

    public void deleteProblem(Long pid,Long tid) throws StatusFailException {
        boolean isOk = false;
        //  ??????id??????null??????????????????????????????????????????
        if (tid != null) {
            QueryWrapper<TrainingProblem> trainingProblemQueryWrapper = new QueryWrapper<>();
            trainingProblemQueryWrapper.eq("tid", tid).eq("pid", pid);
            isOk = trainingProblemEntityService.remove(trainingProblemQueryWrapper);

        } else {
             
            isOk = problemEntityService.removeById(pid);
        }

        if (isOk) { // ????????????
            if (tid == null) {
                FileUtil.del(Constants.File.TESTCASE_BASE_FOLDER.getPath() + File.separator + "problem_" + pid);
            }

            // ??????????????????????????????
            UpdateWrapper<Training> trainingUpdateWrapper = new UpdateWrapper<>();
            trainingUpdateWrapper.set("gmt_modified", new Date())
                    .eq("id", tid);
            trainingEntityService.update(trainingUpdateWrapper);

        } else {
            throw new StatusFailException("???????????????");
        }
    }

    public void addProblemFromPublic(TrainingProblemDto trainingProblemDto) throws StatusFailException {

        Long pid = trainingProblemDto.getPid();
        Long tid = trainingProblemDto.getTid();
        String displayId = trainingProblemDto.getDisplayId();

        QueryWrapper<TrainingProblem> trainingProblemQueryWrapper = new QueryWrapper<>();
        trainingProblemQueryWrapper.eq("tid", tid)
                .and(wrapper -> wrapper.eq("pid", pid)
                        .or()
                        .eq("display_id", displayId));
        TrainingProblem trainingProblem = trainingProblemEntityService.getOne(trainingProblemQueryWrapper, false);
        if (trainingProblem != null) {
            throw new StatusFailException("????????????????????????????????????????????????????????????ID????????????");
        }

        TrainingProblem newTProblem = new TrainingProblem();
        boolean isOk = trainingProblemEntityService.saveOrUpdate(newTProblem
                .setTid(tid).setPid(pid).setDisplayId(displayId));
        if (isOk) { // ????????????

            // ??????????????????????????????
            UpdateWrapper<Training> trainingUpdateWrapper = new UpdateWrapper<>();
            trainingUpdateWrapper.set("gmt_modified", new Date())
                    .eq("id", tid);
            trainingEntityService.update(trainingUpdateWrapper);

            // ????????????????????????????????????????????????
            adminTrainingRecordManager.syncAlreadyRegisterUserRecord(tid, pid, newTProblem.getId());
        } else {
            throw new StatusFailException("???????????????");
        }
    }

    public void importTrainingRemoteOJProblem(String name, String problemId, Long tid) throws StatusFailException {
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

        QueryWrapper<TrainingProblem> trainingProblemQueryWrapper = new QueryWrapper<>();
        Problem finalProblem = problem;
        trainingProblemQueryWrapper.eq("tid", tid)
                .and(wrapper -> wrapper.eq("pid", finalProblem.getId())
                        .or()
                        .eq("display_id", finalProblem.getProblemId()));
        TrainingProblem trainingProblem = trainingProblemEntityService.getOne(trainingProblemQueryWrapper, false);
        if (trainingProblem != null) {
            throw new StatusFailException("????????????????????????????????????????????????????????????ID????????????");
        }

        TrainingProblem newTProblem = new TrainingProblem();
        boolean isOk = trainingProblemEntityService.saveOrUpdate(newTProblem
                .setTid(tid).setPid(problem.getId()).setDisplayId(problem.getProblemId()));
        if (isOk) { // ????????????

            // ??????????????????????????????
            UpdateWrapper<Training> trainingUpdateWrapper = new UpdateWrapper<>();
            trainingUpdateWrapper.set("gmt_modified", new Date())
                    .eq("id", tid);
            trainingEntityService.update(trainingUpdateWrapper);

            // ????????????????????????????????????????????????
            adminTrainingRecordManager.syncAlreadyRegisterUserRecord(tid, problem.getId(), newTProblem.getId());
        } else {
            throw new StatusFailException("???????????????");
        }
    }
}