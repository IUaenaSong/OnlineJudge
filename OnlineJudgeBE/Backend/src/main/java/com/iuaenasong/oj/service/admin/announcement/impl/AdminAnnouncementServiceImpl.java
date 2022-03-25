/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.announcement.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.manager.admin.announcement.AdminAnnouncementManager;
import com.iuaenasong.oj.pojo.entity.common.Announcement;
import com.iuaenasong.oj.pojo.vo.AnnouncementVo;
import com.iuaenasong.oj.service.admin.announcement.AdminAnnouncementService;

import javax.annotation.Resource;

@Service
public class AdminAnnouncementServiceImpl implements AdminAnnouncementService {

    @Resource
    private AdminAnnouncementManager adminAnnouncementManager;

    @Override
    public CommonResult<IPage<AnnouncementVo>> getAnnouncementList(Integer limit, Integer currentPage) {
        return CommonResult.successResponse(adminAnnouncementManager.getAnnouncementList(limit, currentPage));
    }

    @Override
    public CommonResult<Void> deleteAnnouncement(Long aid) {
        try {
            adminAnnouncementManager.deleteAnnouncement(aid);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> addAnnouncement(Announcement announcement) {
        try {
            adminAnnouncementManager.addAnnouncement(announcement);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> updateAnnouncement(Announcement announcement) {
        try {
            adminAnnouncementManager.updateAnnouncement(announcement);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}