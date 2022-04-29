/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.controller.oj;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.vo.*;
import com.iuaenasong.oj.service.oj.ExamService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExamController {

    @Autowired
    private ExamService examService;

    @GetMapping("/get-exam-info")
    @RequiresAuthentication
    public CommonResult<ExamVo> getExamInfo(@RequestParam(value = "eid", required = true) Long eid) {

        return examService.getExamInfo(eid);
    }

    @RequiresAuthentication
    @GetMapping("/get-exam-access")
    public CommonResult<AccessVo> getExamAccess(@RequestParam(value = "eid") Long eid) {

        return examService.getExamAccess(eid);
    }

}