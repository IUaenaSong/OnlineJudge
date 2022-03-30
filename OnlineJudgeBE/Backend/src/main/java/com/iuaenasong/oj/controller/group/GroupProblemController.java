/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.group;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.ProblemDto;
import com.iuaenasong.oj.pojo.entity.judge.CompileDTO;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.entity.problem.ProblemCase;
import com.iuaenasong.oj.pojo.entity.problem.Tag;
import com.iuaenasong.oj.pojo.vo.ProblemVo;
import com.iuaenasong.oj.service.group.problem.GroupProblemService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiresAuthentication
@RequestMapping("/api/group")
public class GroupProblemController {

    @Autowired
    private GroupProblemService groupProblemService;

    @GetMapping("/get-problem-list")
    public CommonResult<IPage<ProblemVo>> getProblemList(@RequestParam(value = "limit", required = false) Integer limit,
                                                         @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                         @RequestParam(value = "gid", required = true) Long gid) {
        return groupProblemService.getProblemList(limit, currentPage, gid);
    }

    @GetMapping("/get-admin-problem-list")
    public CommonResult<IPage<Problem>> getAdminProblemList(@RequestParam(value = "limit", required = false) Integer limit,
                                                            @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                            @RequestParam(value = "gid", required = true) Long gid) {
        return groupProblemService.getAdminProblemList(limit, currentPage, gid);
    }

    @GetMapping("/problem")
    public CommonResult<Problem> getProblem(@RequestParam("pid") Long pid) {
        return groupProblemService.getProblem(pid);
    }

    @PostMapping("/problem")
    public CommonResult<Void> addProblem(@RequestBody ProblemDto problemDto) {
        return groupProblemService.addProblem(problemDto);
    }

    @PutMapping("/problem")
    public CommonResult<Void> updateProblem(@RequestBody ProblemDto problemDto) {
        return groupProblemService.updateProblem(problemDto);
    }

    @DeleteMapping("/problem")
    public CommonResult<Void> deleteProblem(@RequestParam(value = "pid", required = true) Long pid) {
        return groupProblemService.deleteProblem(pid);
    }

    @GetMapping("/get-problem-cases")
    public CommonResult<List<ProblemCase>> getProblemCases(@RequestParam("pid") Long pid,
                                                           @RequestParam(value = "isUpload", defaultValue = "true") Boolean isUpload) {
        return groupProblemService.getProblemCases(pid, isUpload);
    }

    @GetMapping("/get-all-problem-tags")
    public CommonResult<List<Tag>> getAllProblemTagsList(@RequestParam("gid") Long gid) {
        return groupProblemService.getAllProblemTagsList(gid);
    }

    @PostMapping("/compile-spj")
    public CommonResult<Void> compileSpj(@RequestBody CompileDTO compileDTO,
                                         @RequestParam("gid") Long gid) {
        return groupProblemService.compileSpj(compileDTO, gid);
    }

    @PostMapping("/compile-interactive")
    public CommonResult<Void> compileInteractive(@RequestBody CompileDTO compileDTO,
                                                 @RequestParam("gid") Long gid) {
        return groupProblemService.compileInteractive(compileDTO, gid);
    }

    @PutMapping("/change-problem-auth")
    public CommonResult<Void> changeProblemAuth(@RequestParam(value = "pid", required = true) Long pid,
                                                @RequestParam(value = "auth", required = true) Integer auth) {
        return groupProblemService.changeProblemAuth(pid, auth);
    }
}
