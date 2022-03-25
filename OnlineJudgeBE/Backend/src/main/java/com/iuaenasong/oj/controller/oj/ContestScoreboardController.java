/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.oj;

import org.springframework.web.bind.annotation.*;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.ContestRankDto;
import com.iuaenasong.oj.pojo.vo.*;
import com.iuaenasong.oj.service.oj.ContestScoreboardService;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ContestScoreboardController {

    @Resource
    private ContestScoreboardService contestScoreboardService;

    
    @GetMapping("/get-contest-outsize-info")
    public CommonResult<ContestOutsideInfo> getContestOutsideInfo(@RequestParam(value = "cid", required = true) Long cid) {
        return contestScoreboardService.getContestOutsideInfo(cid);
    }

    
    @PostMapping("/get-contest-outside-scoreboard")
    public CommonResult<List> getContestOutsideScoreboard(@RequestBody ContestRankDto contestRankDto) {
        return contestScoreboardService.getContestOutsideScoreboard(contestRankDto);
    }
}