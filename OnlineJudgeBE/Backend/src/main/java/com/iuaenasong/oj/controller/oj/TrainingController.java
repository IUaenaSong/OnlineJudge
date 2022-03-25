/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.RegisterTrainingDto;
import com.iuaenasong.oj.pojo.vo.*;
import com.iuaenasong.oj.service.oj.TrainingService;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TrainingController {

    @Resource
    private TrainingService trainingService;

    
    @GetMapping("/get-training-list")
    public CommonResult<IPage<TrainingVo>> getTrainingList(@RequestParam(value = "limit", required = false) Integer limit,
                                                           @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                           @RequestParam(value = "keyword", required = false) String keyword,
                                                           @RequestParam(value = "categoryId", required = false) Long categoryId,
                                                           @RequestParam(value = "auth", required = false) String auth) {

        return trainingService.getTrainingList(limit, currentPage, keyword, categoryId, auth);
    }

    
    @GetMapping("/get-training-detail")
    @RequiresAuthentication
    public CommonResult<TrainingVo> getTraining(@RequestParam(value = "tid") Long tid) {
        return trainingService.getTraining(tid);
    }

    
    @GetMapping("/get-training-problem-list")
    @RequiresAuthentication
    public CommonResult<List<ProblemVo>> getTrainingProblemList(@RequestParam(value = "tid") Long tid) {
        return trainingService.getTrainingProblemList(tid);
    }

    
    @PostMapping("/register-training")
    @RequiresAuthentication
    public CommonResult<Void> toRegisterTraining(@RequestBody RegisterTrainingDto registerTrainingDto) {
        return trainingService.toRegisterTraining(registerTrainingDto);
    }

    
    @RequiresAuthentication
    @GetMapping("/get-training-access")
    public CommonResult<AccessVo> getTrainingAccess(@RequestParam(value = "tid") Long tid) {
        return trainingService.getTrainingAccess(tid);
    }

    
    @GetMapping("/get-training-rank")
    @RequiresAuthentication
    public CommonResult<IPage<TrainingRankVo>> getTrainingRank(@RequestParam(value = "tid", required = true) Long tid,
                                                               @RequestParam(value = "limit", required = false) Integer limit,
                                                               @RequestParam(value = "currentPage", required = false) Integer currentPage) {
        return trainingService.getTrainingRank(tid, limit, currentPage);
    }

}