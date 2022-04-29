/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.group;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.vo.AdminExamVo;
import com.iuaenasong.oj.pojo.vo.ExamVo;
import com.iuaenasong.oj.service.group.exam.GroupExamService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiresAuthentication
@RequestMapping("/api/group")
public class GroupExamController {

    @Autowired
    private GroupExamService groupExamService;

    @GetMapping("/get-exam-list")
    public CommonResult<IPage<ExamVo>> getExamList(@RequestParam(value = "limit", required = false) Integer limit,
                                                   @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                   @RequestParam(value = "gid", required = true) Long gid) {
        return groupExamService.getExamList(limit, currentPage, gid);
    }

    @GetMapping("/get-admin-exam-list")
    public CommonResult<IPage<Exam>> getAdminExamList(@RequestParam(value = "limit", required = false) Integer limit,
                                                      @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                      @RequestParam(value = "gid", required = true) Long gid) {
        return groupExamService.getAdminExamList(limit, currentPage, gid);
    }

    @GetMapping("/exam")
    public CommonResult<AdminExamVo> getExam(@RequestParam("eid") Long eid) {
        return groupExamService.getExam(eid);
    }

    @PostMapping("/exam")
    public CommonResult<Void> addExam(@RequestBody AdminExamVo adminExamVo) {
        return groupExamService.addExam(adminExamVo);
    }

    @PutMapping("/exam")
    public CommonResult<Void> updateExam(@RequestBody AdminExamVo adminExamVo) {
        return groupExamService.updateExam(adminExamVo);
    }

    @DeleteMapping("/exam")
    public CommonResult<Void> deleteExam(@RequestParam(value = "eid", required = true) Long eid) {
        return groupExamService.deleteExam(eid);
    }

    @PutMapping("/change-exam-visible")
    public CommonResult<Void> changeExamVisible(@RequestParam(value = "eid", required = true) Long eid,
                                                @RequestParam(value = "visible", required = true) Boolean visible) {
        return groupExamService.changeExamVisible(eid, visible);
    }

}
