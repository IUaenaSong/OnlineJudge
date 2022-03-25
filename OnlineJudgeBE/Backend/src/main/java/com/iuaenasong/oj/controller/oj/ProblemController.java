/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.oj;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.PidListDto;
import com.iuaenasong.oj.pojo.vo.*;
import com.iuaenasong.oj.service.oj.ProblemService;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    
    @RequestMapping(value = "/get-problem-list", method = RequestMethod.GET)
    public CommonResult<Page<ProblemVo>> getProblemList(@RequestParam(value = "limit", required = false) Integer limit,
                                                        @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                        @RequestParam(value = "keyword", required = false) String keyword,
                                                        @RequestParam(value = "tagId", required = false) List<Long> tagId,
                                                        @RequestParam(value = "difficulty", required = false) Integer difficulty,
                                                        @RequestParam(value = "oj", required = false) String oj) {

        return problemService.getProblemList(limit, currentPage, keyword, tagId, difficulty, oj);
    }

    
    @GetMapping("/get-random-problem")
    public CommonResult<RandomProblemVo> getRandomProblem() {
        return problemService.getRandomProblem();
    }

    
    @RequiresAuthentication
    @PostMapping("/get-user-problem-status")
    public CommonResult<HashMap<Long, Object>> getUserProblemStatus(@Validated @RequestBody PidListDto pidListDto) {
        return problemService.getUserProblemStatus(pidListDto);
    }

    
    @RequestMapping(value = "/get-problem-detail", method = RequestMethod.GET)
    public CommonResult<ProblemInfoVo> getProblemInfo(@RequestParam(value = "problemId", required = true) String problemId) {
        return problemService.getProblemInfo(problemId);
    }

}