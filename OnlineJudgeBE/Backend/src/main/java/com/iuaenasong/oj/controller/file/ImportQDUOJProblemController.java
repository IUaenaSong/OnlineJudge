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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.service.file.ImportQDUOJProblemService;

@Controller
@RequestMapping("/api/file")
public class ImportQDUOJProblemController {

    @Autowired
    private ImportQDUOJProblemService importQDUOJProblemService;

    
    @RequiresRoles("root")
    @RequiresAuthentication
    @ResponseBody
    @PostMapping("/import-qdoj-problem")
    public CommonResult<Void> importQDOJProblem(@RequestParam("file") MultipartFile file) {
        return importQDUOJProblemService.importQDOJProblem(file);
    }

}