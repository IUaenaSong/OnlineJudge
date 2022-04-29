/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.service.oj.impl;

import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.common.result.ResultStatus;
import com.iuaenasong.oj.manager.oj.ExamManager;
import com.iuaenasong.oj.pojo.vo.*;
import com.iuaenasong.oj.service.oj.ExamService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ExamServiceImpl implements ExamService {

    @Resource
    private ExamManager examManager;

    @Override
    public CommonResult<ExamVo> getExamInfo(Long cid) {
        try {
            return CommonResult.successResponse(examManager.getExamInfo(cid));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<AccessVo> getExamAccess(Long eid) {
        try {
            return CommonResult.successResponse(examManager.getExamAccess(eid));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

}