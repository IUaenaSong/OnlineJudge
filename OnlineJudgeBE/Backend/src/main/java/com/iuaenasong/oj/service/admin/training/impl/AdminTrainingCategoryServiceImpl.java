/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.training.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.manager.admin.training.AdminTrainingCategoryManager;
import com.iuaenasong.oj.pojo.entity.training.TrainingCategory;
import com.iuaenasong.oj.service.admin.training.AdminTrainingCategoryService;

@Service
public class AdminTrainingCategoryServiceImpl implements AdminTrainingCategoryService {

    @Autowired
    private AdminTrainingCategoryManager adminTrainingCategoryManager;

    @Override
    public CommonResult<TrainingCategory> addTrainingCategory(TrainingCategory trainingCategory) {
        try {
            return CommonResult.successResponse(adminTrainingCategoryManager.addTrainingCategory(trainingCategory));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> updateTrainingCategory(TrainingCategory trainingCategory) {
        try {
            adminTrainingCategoryManager.updateTrainingCategory(trainingCategory);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> deleteTrainingCategory(Long cid) {
        try {
            adminTrainingCategoryManager.deleteTrainingCategory(cid);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}