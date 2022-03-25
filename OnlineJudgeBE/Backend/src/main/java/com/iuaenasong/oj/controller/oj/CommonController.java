/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.oj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.problem.*;
import com.iuaenasong.oj.pojo.entity.training.TrainingCategory;
import com.iuaenasong.oj.pojo.vo.CaptchaVo;
import com.iuaenasong.oj.service.oj.CommonService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommonController {

    @Autowired
    private CommonService commonService;

    @GetMapping("/captcha")
    public CommonResult<CaptchaVo> getCaptcha() {
        return commonService.getCaptcha();
    }

    @GetMapping("/get-training-category")
    public CommonResult<List<TrainingCategory>> getTrainingCategory() {
        return commonService.getTrainingCategory();
    }

    @GetMapping("/get-all-problem-tags")
    public CommonResult<List<Tag>> getAllProblemTagsList(@RequestParam(value = "oj", defaultValue = "ME") String oj) {
        return commonService.getAllProblemTagsList(oj);
    }

    @GetMapping("/get-problem-tags")
    public CommonResult<Collection<Tag>> getProblemTags(Long pid) {
        return commonService.getProblemTags(pid);
    }

    @GetMapping("/languages")
    public CommonResult<List<Language>> getLanguages(@RequestParam(value = "pid", required = false) Long pid,
                                                     @RequestParam(value = "all", required = false) Boolean all) {
        return commonService.getLanguages(pid, all);
    }

    @GetMapping("/get-problem-languages")
    public CommonResult<Collection<Language>> getProblemLanguages(@RequestParam("pid") Long pid) {
        return commonService.getProblemLanguages(pid);
    }

    @GetMapping("/get-problem-code-template")
    public CommonResult<List<CodeTemplate>> getProblemCodeTemplate(@RequestParam("pid") Long pid) {
        return commonService.getProblemCodeTemplate(pid);
    }

}