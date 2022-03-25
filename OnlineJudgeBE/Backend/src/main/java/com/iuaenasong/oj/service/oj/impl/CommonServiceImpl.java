/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj.impl;

import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.result.ResultStatus;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.manager.oj.CommonManager;
import com.iuaenasong.oj.pojo.entity.problem.CodeTemplate;
import com.iuaenasong.oj.pojo.entity.problem.Language;
import com.iuaenasong.oj.pojo.entity.problem.Tag;
import com.iuaenasong.oj.pojo.entity.training.TrainingCategory;
import com.iuaenasong.oj.pojo.vo.CaptchaVo;
import com.iuaenasong.oj.service.oj.CommonService;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

@Service
public class CommonServiceImpl implements CommonService {

    @Resource
    private CommonManager commonManager;

    @Override
    public CommonResult<CaptchaVo> getCaptcha() {
        return CommonResult.successResponse(commonManager.getCaptcha());
    }

    @Override
    public CommonResult<List<TrainingCategory>> getTrainingCategory() {
        return CommonResult.successResponse(commonManager.getTrainingCategory());
    }

    @Override
    public CommonResult<List<Tag>> getAllProblemTagsList(String oj) {
        return CommonResult.successResponse(commonManager.getAllProblemTagsList(oj));
    }

    @Override
    public CommonResult<Collection<Tag>> getProblemTags(Long pid) {
        try {
            return CommonResult.successResponse(commonManager.getProblemTags(pid));
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<List<Language>> getLanguages(Long pid, Boolean all) {
        return CommonResult.successResponse(commonManager.getLanguages(pid, all));
    }

    @Override
    public CommonResult<Collection<Language>> getProblemLanguages(Long pid) {
        try {
            return CommonResult.successResponse(commonManager.getProblemLanguages(pid));
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<List<CodeTemplate>> getProblemCodeTemplate(Long pid) {
        try {
            return CommonResult.successResponse(commonManager.getProblemCodeTemplate(pid));
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }
}