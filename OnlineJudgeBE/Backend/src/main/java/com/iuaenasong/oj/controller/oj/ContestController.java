/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.ContestPrintDto;
import com.iuaenasong.oj.pojo.dto.ContestRankDto;
import com.iuaenasong.oj.pojo.dto.RegisterContestDto;
import com.iuaenasong.oj.pojo.dto.UserReadContestAnnouncementDto;
import com.iuaenasong.oj.pojo.entity.common.Announcement;
import com.iuaenasong.oj.pojo.vo.*;
import com.iuaenasong.oj.service.oj.ContestService;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ContestController {

    @Autowired
    private ContestService contestService;

    
    @GetMapping("/get-contest-list")
    public CommonResult<IPage<ContestVo>> getContestList(@RequestParam(value = "limit", required = false) Integer limit,
                                                         @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                         @RequestParam(value = "status", required = false) Integer status,
                                                         @RequestParam(value = "type", required = false) Integer type,
                                                         @RequestParam(value = "keyword", required = false) String keyword) {
        return contestService.getContestList(limit, currentPage, status, type, keyword);
    }

    
    @GetMapping("/get-contest-info")
    @RequiresAuthentication
    public CommonResult<ContestVo> getContestInfo(@RequestParam(value = "cid", required = true) Long cid) {

        return contestService.getContestInfo(cid);
    }

    
    @PostMapping("/register-contest")
    @RequiresAuthentication
    public CommonResult<Void> toRegisterContest(@RequestBody RegisterContestDto registerContestDto) {
        return contestService.toRegisterContest(registerContestDto);
    }

    
    @RequiresAuthentication
    @GetMapping("/get-contest-access")
    public CommonResult<AccessVo> getContestAccess(@RequestParam(value = "cid") Long cid) {

        return contestService.getContestAccess(cid);
    }

    
    @GetMapping("/get-contest-problem")
    @RequiresAuthentication
    public CommonResult<List<ContestProblemVo>> getContestProblem(@RequestParam(value = "cid", required = true) Long cid) {

        return contestService.getContestProblem(cid);
    }

    @GetMapping("/get-contest-problem-details")
    @RequiresAuthentication
    public CommonResult<ProblemInfoVo> getContestProblemDetails(@RequestParam(value = "cid", required = true) Long cid,
                                                                @RequestParam(value = "displayId", required = true) String displayId) {

        return contestService.getContestProblemDetails(cid, displayId);
    }

    @GetMapping("/contest-submissions")
    @RequiresAuthentication
    public CommonResult<IPage<JudgeVo>> getContestSubmissionList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                 @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                 @RequestParam(value = "onlyMine", required = false) Boolean onlyMine,
                                                                 @RequestParam(value = "problemID", required = false) String displayId,
                                                                 @RequestParam(value = "status", required = false) Integer searchStatus,
                                                                 @RequestParam(value = "username", required = false) String searchUsername,
                                                                 @RequestParam(value = "contestID", required = true) Long searchCid,
                                                                 @RequestParam(value = "completeProblemID", defaultValue = "false") Boolean completeProblemID) {

        return contestService.getContestSubmissionList(limit,
                currentPage,
                onlyMine,
                displayId,
                searchStatus,
                searchUsername,
                searchCid,
                completeProblemID);
    }

    
    @PostMapping("/get-contest-rank")
    @RequiresAuthentication
    public CommonResult<IPage> getContestRank(@RequestBody ContestRankDto contestRankDto) {

        return contestService.getContestRank(contestRankDto);
    }

    
    @GetMapping("/get-contest-announcement")
    @RequiresAuthentication
    public CommonResult<IPage<AnnouncementVo>> getContestAnnouncement(@RequestParam(value = "cid", required = true) Long cid,
                                                                      @RequestParam(value = "limit", required = false) Integer limit,
                                                                      @RequestParam(value = "currentPage", required = false) Integer currentPage) {

        return contestService.getContestAnnouncement(cid, limit, currentPage);
    }

    
    @PostMapping("/get-contest-not-read-announcement")
    @RequiresAuthentication
    public CommonResult<List<Announcement>> getContestUserNotReadAnnouncement(@RequestBody UserReadContestAnnouncementDto userReadContestAnnouncementDto) {
        return contestService.getContestUserNotReadAnnouncement(userReadContestAnnouncementDto);
    }

    
    @PostMapping("/submit-print-text")
    @RequiresAuthentication
    public CommonResult<Void> submitPrintText(@RequestBody ContestPrintDto contestPrintDto) {

        return contestService.submitPrintText(contestPrintDto);
    }

}