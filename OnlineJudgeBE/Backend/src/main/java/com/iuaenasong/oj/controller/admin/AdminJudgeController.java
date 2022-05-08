/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.admin;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.service.admin.rejudge.RejudgeService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/admin/judge")
public class AdminJudgeController {

    @Resource
    private RejudgeService rejudgeService;

    @GetMapping("/rejudge")
    @RequiresAuthentication
    @RequiresRoles("root")  // 只有超级管理员能操作
    @RequiresPermissions("rejudge")
    public CommonResult<Judge> rejudge(@RequestParam("submitId") Long submitId) {
        return rejudgeService.rejudge(submitId);
    }

    @GetMapping("/rejudge-contest-problem")
    @RequiresAuthentication
    @RequiresRoles("root")  // 只有超级管理员能操作
    @RequiresPermissions("rejudge")
    public CommonResult<Void> rejudgeContestProblem(@RequestParam("cid") Long cid, @RequestParam("pid") Long pid) {
        return rejudgeService.rejudgeContestProblem(cid, pid);
    }

    @GetMapping("/rejudge-exam-problem")
    @RequiresAuthentication
    @RequiresRoles("root")  // 只有超级管理员能操作
    @RequiresPermissions("rejudge")
    public CommonResult<Void> rejudgeExamProblem(@RequestParam("eid") Long eid, @RequestParam("pid") Long pid) {
        return rejudgeService.rejudgeExamProblem(eid, pid);
    }
}
