/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.msg.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.manager.msg.AdminNoticeManager;
import com.iuaenasong.oj.pojo.entity.msg.AdminSysNotice;
import com.iuaenasong.oj.pojo.vo.AdminSysNoticeVo;
import com.iuaenasong.oj.service.msg.AdminNoticeService;

import javax.annotation.Resource;

@Service
public class AdminNoticeServiceImpl implements AdminNoticeService {

    @Resource
    private AdminNoticeManager adminNoticeManager;

    @Override
    public CommonResult<IPage<AdminSysNoticeVo>> getSysNotice(Integer limit, Integer currentPage, String type) {

        return CommonResult.successResponse(adminNoticeManager.getSysNotice(limit, currentPage, type));
    }

    @Override
    public CommonResult<Void> addSysNotice(AdminSysNotice adminSysNotice) {
        try {
            adminNoticeManager.addSysNotice(adminSysNotice);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> deleteSysNotice(Long id) {
        try {
            adminNoticeManager.deleteSysNotice(id);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> updateSysNotice(AdminSysNotice adminSysNotice) {
        try {
            adminNoticeManager.updateSysNotice(adminSysNotice);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}