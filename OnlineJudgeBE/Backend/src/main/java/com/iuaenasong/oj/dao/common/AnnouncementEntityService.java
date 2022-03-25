/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.pojo.entity.common.Announcement;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.vo.AnnouncementVo;

public interface AnnouncementEntityService extends IService<Announcement> {

    IPage<AnnouncementVo> getAnnouncementList(int limit, int currentPage, Boolean notAdmin);

    IPage<AnnouncementVo> getContestAnnouncement(Long cid,Boolean notAdmin,int limit, int currentPage);
}
