/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.admin;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;
import com.iuaenasong.oj.common.result.CommonResult;

import com.iuaenasong.oj.pojo.entity.training.TrainingCategory;
import com.iuaenasong.oj.service.admin.training.AdminTrainingCategoryService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/admin/training/category")
public class AdminTrainingCategoryController {

    @Resource
    private AdminTrainingCategoryService adminTrainingCategoryService;

    @PostMapping("")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "problem_admin"}, logical = Logical.OR)
    public CommonResult<TrainingCategory> addTrainingCategory(@RequestBody TrainingCategory trainingCategory) {
        return adminTrainingCategoryService.addTrainingCategory(trainingCategory);
    }

    @PutMapping("")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> updateTrainingCategory(@RequestBody TrainingCategory trainingCategory) {
        return adminTrainingCategoryService.updateTrainingCategory(trainingCategory);
    }

    @DeleteMapping("")
    @RequiresAuthentication
    @RequiresRoles(value = {"root", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> deleteTrainingCategory(@RequestParam("cid") Long cid) {
        return adminTrainingCategoryService.deleteTrainingCategory(cid);
    }
}