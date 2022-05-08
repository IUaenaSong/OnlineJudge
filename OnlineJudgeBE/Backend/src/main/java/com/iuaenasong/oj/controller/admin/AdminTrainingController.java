/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.TrainingDto;
import com.iuaenasong.oj.pojo.dto.TrainingProblemDto;
import com.iuaenasong.oj.pojo.entity.training.Training;
import com.iuaenasong.oj.pojo.entity.training.TrainingProblem;
import com.iuaenasong.oj.service.admin.training.AdminTrainingProblemService;
import com.iuaenasong.oj.service.admin.training.AdminTrainingService;

import javax.annotation.Resource;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/training")
public class AdminTrainingController {

    @Resource
    private AdminTrainingService adminTrainingService;

    @Resource
    private AdminTrainingProblemService adminTrainingProblemService;

    @GetMapping("/get-training-list")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<IPage<Training>> getTrainingList(@RequestParam(value = "limit", required = false) Integer limit,
                                                         @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                         @RequestParam(value = "keyword", required = false) String keyword) {
        return adminTrainingService.getTrainingList(limit, currentPage, keyword);
    }

    @GetMapping("")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<TrainingDto> getTraining(@RequestParam("tid") Long tid) {
        return adminTrainingService.getTraining(tid);
    }

    @DeleteMapping("")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> deleteTraining(@RequestParam("tid") Long tid) {
        return adminTrainingService.deleteTraining(tid);
    }

    @PostMapping("")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> addTraining(@RequestBody TrainingDto trainingDto) {
        return adminTrainingService.addTraining(trainingDto);
    }

    @PutMapping("")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> updateTraining(@RequestBody TrainingDto trainingDto) {
        return adminTrainingService.updateTraining(trainingDto);
    }

    @PutMapping("/change-training-status")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> changeTrainingStatus(@RequestParam(value = "tid", required = true) Long tid,
                                                   @RequestParam(value = "author", required = true) String author,
                                                   @RequestParam(value = "status", required = true) Boolean status) {
        return adminTrainingService.changeTrainingStatus(tid, author, status);
    }

    @GetMapping("/get-problem-list")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<HashMap<String, Object>> getProblemList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                @RequestParam(value = "keyword", required = false) String keyword,
                                                                @RequestParam(value = "queryExisted", defaultValue = "false") Boolean queryExisted,
                                                                @RequestParam(value = "tid", required = true) Long tid) {
        return adminTrainingProblemService.getProblemList(limit, currentPage, keyword, queryExisted, tid);
    }

    @PutMapping("/problem")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> updateProblem(@RequestBody TrainingProblem trainingProblem) {
        return adminTrainingProblemService.updateProblem(trainingProblem);
    }

    @DeleteMapping("/problem")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> deleteProblem(@RequestParam("pid") Long pid,
                                            @RequestParam(value = "tid", required = false) Long tid) {

        return adminTrainingProblemService.deleteProblem(pid, tid);
    }

    @PostMapping("/add-problem-from-public")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> addProblemFromPublic(@RequestBody TrainingProblemDto trainingProblemDto) {
        return adminTrainingProblemService.addProblemFromPublic(trainingProblemDto);
    }

    @GetMapping("/import-remote-oj-problem")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> importTrainingRemoteOJProblem(@RequestParam("name") String name,
                                                            @RequestParam("problemId") String problemId,
                                                            @RequestParam("tid") Long tid) {
        return adminTrainingProblemService.importTrainingRemoteOJProblem(name, problemId, tid);
    }

}