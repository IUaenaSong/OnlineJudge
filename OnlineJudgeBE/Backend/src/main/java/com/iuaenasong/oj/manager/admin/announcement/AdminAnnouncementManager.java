/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.admin.announcement;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.pojo.entity.common.Announcement;
import com.iuaenasong.oj.pojo.vo.AnnouncementVo;
import com.iuaenasong.oj.dao.common.AnnouncementEntityService;

@Component
public class AdminAnnouncementManager {

    @Autowired
    private AnnouncementEntityService announcementEntityService;

    public IPage<AnnouncementVo> getAnnouncementList(Integer limit, Integer currentPage) {

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        return announcementEntityService.getAnnouncementList(limit, currentPage, false);

    }

    public void deleteAnnouncement(long aid) throws StatusFailException {
        boolean isOk = announcementEntityService.removeById(aid);
        if (!isOk) {
            throw new StatusFailException("删除失败");
        }
    }

    public void addAnnouncement(Announcement announcement) throws StatusFailException {
        boolean isOk = announcementEntityService.save(announcement);
        if (!isOk) {
            throw new StatusFailException("添加失败");
        }
    }

    public void updateAnnouncement(Announcement announcement) throws StatusFailException {
        boolean isOk = announcementEntityService.updateById(announcement);
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }
}