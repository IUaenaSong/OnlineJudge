/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.tag.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.manager.admin.tag.AdminTagManager;
import com.iuaenasong.oj.pojo.entity.problem.Tag;
import com.iuaenasong.oj.service.admin.tag.AdminTagService;

@Service
public class AdminTagServiceImpl implements AdminTagService {

    @Autowired
    private AdminTagManager adminTagManager;

    @Override
    public CommonResult<Tag> addProblem(Tag tag) {
        try {
            return CommonResult.successResponse(adminTagManager.addProblem(tag));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> updateTag(Tag tag) {
        try {
            adminTagManager.updateTag(tag);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> deleteTag(Long tid) {
        try {
            adminTagManager.deleteTag(tid);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}