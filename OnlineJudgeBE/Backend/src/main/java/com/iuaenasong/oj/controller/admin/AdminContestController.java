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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.AnnouncementDto;
import com.iuaenasong.oj.pojo.dto.ContestProblemDto;
import com.iuaenasong.oj.pojo.dto.ProblemDto;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.entity.contest.ContestProblem;

import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.vo.AdminContestVo;
import com.iuaenasong.oj.pojo.vo.AnnouncementVo;

import com.iuaenasong.oj.service.admin.contest.AdminContestAnnouncementService;
import com.iuaenasong.oj.service.admin.contest.AdminContestProblemService;
import com.iuaenasong.oj.service.admin.contest.AdminContestService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/admin/contest")
public class AdminContestController {

    @Autowired
    private AdminContestService adminContestService;

    @Autowired
    private AdminContestProblemService adminContestProblemService;

    @Autowired
    private AdminContestAnnouncementService adminContestAnnouncementService;

    @GetMapping("/get-contest-list")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<IPage<Contest>> getContestList(@RequestParam(value = "limit", required = false) Integer limit,
                                                       @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                       @RequestParam(value = "keyword", required = false) String keyword) {

        return adminContestService.getContestList(limit, currentPage, keyword);
    }

    @GetMapping("")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<AdminContestVo> getContest(@RequestParam("cid") Long cid) {

        return adminContestService.getContest(cid);
    }

    @DeleteMapping("")
    @RequiresAuthentication
    @RequiresRoles(value = "root") // 只有超级管理员能删除比赛
    public CommonResult<Void> deleteContest(@RequestParam("cid") Long cid) {

        return adminContestService.deleteContest(cid);
    }

    @PostMapping("")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> addContest(@RequestBody AdminContestVo adminContestVo) {

        return adminContestService.addContest(adminContestVo);
    }

    @PutMapping("")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> updateContest(@RequestBody AdminContestVo adminContestVo) {

        return adminContestService.updateContest(adminContestVo);
    }

    @PutMapping("/change-contest-visible")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> changeContestVisible(@RequestParam(value = "cid", required = true) Long cid,
                                                   @RequestParam(value = "uid", required = true) String uid,
                                                   @RequestParam(value = "visible", required = true) Boolean visible) {

        return adminContestService.changeContestVisible(cid, uid, visible);
    }

    @GetMapping("/get-problem-list")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<HashMap<String, Object>> getProblemList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                @RequestParam(value = "keyword", required = false) String keyword,
                                                                @RequestParam(value = "cid", required = true) Long cid,
                                                                @RequestParam(value = "problemType", required = false) Integer problemType,
                                                                @RequestParam(value = "oj", required = false) String oj) {

        return adminContestProblemService.getProblemList(limit, currentPage, keyword, cid, problemType, oj);
    }

    @GetMapping("/problem")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Problem> getProblem(@RequestParam("pid") Long pid, HttpServletRequest request) {
        return adminContestProblemService.getProblem(pid);
    }

    @DeleteMapping("/problem")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "problem_admin"}, logical = Logical.OR)
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> deleteProblem(@RequestParam("pid") Long pid,
                                            @RequestParam(value = "cid", required = false) Long cid) {
        return adminContestProblemService.deleteProblem(pid, cid);
    }

    @PostMapping("/problem")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Map<Object, Object>> addProblem(@RequestBody ProblemDto problemDto) {

        return adminContestProblemService.addProblem(problemDto);
    }

    @PutMapping("/problem")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> updateProblem(@RequestBody ProblemDto problemDto) {

        return adminContestProblemService.updateProblem(problemDto);
    }

    @GetMapping("/contest-problem")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<ContestProblem> getContestProblem(@RequestParam(value = "cid", required = true) Long cid,
                                                          @RequestParam(value = "pid", required = true) Long pid) {

        return adminContestProblemService.getContestProblem(cid, pid);
    }

    @PutMapping("/contest-problem")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<ContestProblem> setContestProblem(@RequestBody ContestProblem contestProblem) {

        return adminContestProblemService.setContestProblem(contestProblem);
    }

    @PostMapping("/add-problem-from-public")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> addProblemFromPublic(@RequestBody ContestProblemDto contestProblemDto) {

        return adminContestProblemService.addProblemFromPublic(contestProblemDto);
    }

    @GetMapping("/import-remote-oj-problem")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> importContestRemoteOJProblem(@RequestParam("name") String name,
                                                           @RequestParam("problemId") String problemId,
                                                           @RequestParam("cid") Long cid,
                                                           @RequestParam("displayId") String displayId) {

        return adminContestProblemService.importContestRemoteOJProblem(name, problemId, cid, displayId);
    }

    @GetMapping("/announcement")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<IPage<AnnouncementVo>> getAnnouncementList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                   @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                   @RequestParam(value = "cid", required = true) Long cid) {

        return adminContestAnnouncementService.getAnnouncementList(limit, currentPage, cid);
    }

    @DeleteMapping("/announcement")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> deleteAnnouncement(@RequestParam("aid") Long aid) {

        return adminContestAnnouncementService.deleteAnnouncement(aid);
    }

    @PostMapping("/announcement")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> addAnnouncement(@RequestBody AnnouncementDto announcementDto) {

        return adminContestAnnouncementService.addAnnouncement(announcementDto);
    }

    @PutMapping("/announcement")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> updateAnnouncement(@RequestBody AnnouncementDto announcementDto) {

        return adminContestAnnouncementService.updateAnnouncement(announcementDto);
    }
}