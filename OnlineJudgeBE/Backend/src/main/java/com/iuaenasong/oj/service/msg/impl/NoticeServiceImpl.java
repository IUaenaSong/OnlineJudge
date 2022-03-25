/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.msg.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.manager.msg.NoticeManager;
import com.iuaenasong.oj.pojo.vo.SysMsgVo;
import com.iuaenasong.oj.service.msg.NoticeService;

import javax.annotation.Resource;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeManager noticeManager;

    @Override
    public CommonResult<IPage<SysMsgVo>> getSysNotice(Integer limit, Integer currentPage) {
        return CommonResult.successResponse(noticeManager.getSysNotice(limit, currentPage));
    }

    @Override
    public CommonResult<IPage<SysMsgVo>> getMineNotice(Integer limit, Integer currentPage) {
        return CommonResult.successResponse(noticeManager.getMineNotice(limit, currentPage));
    }
}