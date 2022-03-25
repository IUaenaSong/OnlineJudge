/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.group;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.TrainingDto;
import com.iuaenasong.oj.pojo.dto.TrainingProblemDto;
import com.iuaenasong.oj.pojo.entity.training.Training;
import com.iuaenasong.oj.pojo.entity.training.TrainingProblem;
import com.iuaenasong.oj.pojo.vo.TrainingVo;
import com.iuaenasong.oj.service.group.training.GroupTrainingProblemService;
import com.iuaenasong.oj.service.group.training.GroupTrainingService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiresAuthentication
@RequestMapping("/api/group")
public class GroupTrainingController {

    @Autowired
    private GroupTrainingService groupTrainingService;

    @Autowired
    private GroupTrainingProblemService groupTrainingProblemService;

    @GetMapping("/get-training-list")
    public CommonResult<IPage<TrainingVo>> getTrainingList(@RequestParam(value = "limit", required = false) Integer limit,
                                                          @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                          @RequestParam(value = "gid", required = true) Long gid) {
        return groupTrainingService.getTrainingList(limit, currentPage, gid);
    }

    @GetMapping("/get-admin-training-list")
    public CommonResult<IPage<Training>> getAdminTrainingList(@RequestParam(value = "limit", required = false) Integer limit,
                                                              @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                              @RequestParam(value = "gid", required = true) Long gid) {
        return groupTrainingService.getAdminTrainingList(limit, currentPage, gid);
    }

    @GetMapping("/training")
    public CommonResult<TrainingDto> getTraining(@RequestParam("tid") Long tid) {
        return groupTrainingService.getTraining(tid);
    }

    @PostMapping("/training")
    public CommonResult<Void> addTraining(@RequestBody TrainingDto trainingDto) {
        return groupTrainingService.addTraining(trainingDto);
    }

    @PutMapping("/training")
    public CommonResult<Void> updateTraining(@RequestBody TrainingDto trainingDto) {
        return groupTrainingService.updateTraining(trainingDto);
    }

    @DeleteMapping("/training")
    public CommonResult<Void> deleteTraining(@RequestParam(value = "tid", required = true) Long tid) {
        return groupTrainingService.deleteTraining(tid);
    }

    @PutMapping("/change-training-status")
    public CommonResult<Void> changeTrainingStatus(@RequestParam(value = "tid", required = true) Long tid,
                                                   @RequestParam(value = "status", required = true) Boolean status) {
        return groupTrainingService.changeTrainingStatus(tid, status);
    }

    @GetMapping("/get-training-problem-list")
    public CommonResult<HashMap<String, Object>> getTrainingProblemList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                        @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                        @RequestParam(value = "keyword", required = false) String keyword,
                                                                        @RequestParam(value = "queryExisted", required = false, defaultValue = "true") Boolean queryExisted,
                                                                        @RequestParam(value = "tid", required = true) Long tid) {
        return groupTrainingProblemService.getTrainingProblemList(limit, currentPage, keyword, queryExisted, tid);
    }

    @PutMapping("/training-problem")
    public CommonResult<Void> updateTrainingProblem(@RequestBody TrainingProblem trainingProblem) {
        return groupTrainingProblemService.updateTrainingProblem(trainingProblem);
    }

    @DeleteMapping("/training-problem")
    public CommonResult<Void> deleteTrainingProblem(@RequestParam(value = "pid", required = true) Long pid,
                                                    @RequestParam(value = "tid", required = true) Long tid) {
        return groupTrainingProblemService.deleteTrainingProblem(pid, tid);
    }

    @PostMapping("/add-training-problem-from-public")
    public CommonResult<Void> addProblemFromPublic(@RequestBody TrainingProblemDto trainingProblemDto) {
        return groupTrainingProblemService.addProblemFromPublic(trainingProblemDto);
    }

    @PostMapping("/add-training-problem-from-group")
    public CommonResult<Void> addProblemFromGroup(@RequestParam(value = "problemId", required = true) String problemId,
                                                  @RequestParam(value = "tid", required = true) Long tid) {
        return groupTrainingProblemService.addProblemFromGroup(problemId, tid);
    }
}
