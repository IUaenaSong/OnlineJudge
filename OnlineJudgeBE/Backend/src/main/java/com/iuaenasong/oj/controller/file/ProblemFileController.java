/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.file;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.service.file.ProblemFileService;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/api/file")
public class ProblemFileController {

    @Autowired
    private ProblemFileService problemFileService;

    
    @RequiresRoles("root")
    @RequiresAuthentication
    @ResponseBody
    @PostMapping("/import-problem")
    public CommonResult<Void> importProblem(@RequestParam("file") MultipartFile file) {
        return problemFileService.importProblem(file);
    }

    
    @GetMapping("/export-problem")
    @RequiresAuthentication
    @RequiresRoles("root")
    public void exportProblem(@RequestParam("pid") List<Long> pidList, HttpServletResponse response) {
        problemFileService.exportProblem(pidList, response);
    }

}