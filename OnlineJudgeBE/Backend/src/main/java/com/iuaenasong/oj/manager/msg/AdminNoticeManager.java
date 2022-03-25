/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.msg;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.pojo.entity.msg.AdminSysNotice;
import com.iuaenasong.oj.pojo.entity.msg.UserSysNotice;
import com.iuaenasong.oj.pojo.vo.AdminSysNoticeVo;
import com.iuaenasong.oj.dao.msg.AdminSysNoticeEntityService;
import com.iuaenasong.oj.dao.msg.UserSysNoticeEntityService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class AdminNoticeManager {

    @Resource
    private AdminSysNoticeEntityService adminSysNoticeEntityService;

    @Resource
    private UserSysNoticeEntityService userSysNoticeEntityService;

    public IPage<AdminSysNoticeVo> getSysNotice(Integer limit, Integer currentPage, String type) {

        // 页数，每页题数若为空，设置默认值
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 5;

        return adminSysNoticeEntityService.getSysNotice(limit, currentPage, type);
    }

    public void addSysNotice(AdminSysNotice adminSysNotice) throws StatusFailException {

        boolean isOk = adminSysNoticeEntityService.saveOrUpdate(adminSysNotice);
        if (!isOk) {
            throw new StatusFailException("发布失败");
        }
    }

    public void deleteSysNotice(Long id) throws StatusFailException {

        boolean isOk = adminSysNoticeEntityService.removeById(id);
        if (!isOk) {
            throw new StatusFailException("删除失败");
        }
    }

    public void updateSysNotice(AdminSysNotice adminSysNotice) throws StatusFailException {
        boolean isOk = adminSysNoticeEntityService.saveOrUpdate(adminSysNotice);
        if (!isOk) {
            throw new StatusFailException("更新失败");
        }
    }

    @Async
    public void syncNoticeToNewRegisterBatchUser(List<String> uidList) {
        QueryWrapper<AdminSysNotice> adminSysNoticeQueryWrapper = new QueryWrapper<>();
        adminSysNoticeQueryWrapper
                .eq("type", "All")
                .le("gmt_create", new Date())
                .eq("state", true);
        List<AdminSysNotice> adminSysNotices = adminSysNoticeEntityService.list(adminSysNoticeQueryWrapper);
        if (adminSysNotices.size() == 0) {
            return;
        }
        List<UserSysNotice> userSysNoticeList = new ArrayList<>();
        for (String uid : uidList) {
            for (AdminSysNotice adminSysNotice : adminSysNotices) {
                UserSysNotice userSysNotice = new UserSysNotice();
                userSysNotice.setType("Sys")
                        .setSysNoticeId(adminSysNotice.getId())
                        .setRecipientId(uid);
                userSysNoticeList.add(userSysNotice);
            }
        }
        userSysNoticeEntityService.saveOrUpdateBatch(userSysNoticeList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Async
    public void addSingleNoticeToUser(String adminId, String recipientId, String title, String content, String type) {
        AdminSysNotice adminSysNotice = new AdminSysNotice();
        adminSysNotice.setAdminId(adminId)
                .setType("Single")
                .setTitle(title)
                .setContent(content)
                .setState(true)
                .setRecipientId(recipientId);
        boolean isOk = adminSysNoticeEntityService.save(adminSysNotice);
        if (isOk) {
            UserSysNotice userSysNotice = new UserSysNotice();
            userSysNotice.setRecipientId(recipientId)
                    .setSysNoticeId(adminSysNotice.getId())
                    .setType(type);
            userSysNoticeEntityService.save(userSysNotice);
        }
    }
}