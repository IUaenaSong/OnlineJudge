/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.group.announcement;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.common.Announcement;
import com.iuaenasong.oj.pojo.vo.AnnouncementVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface GroupAnnouncementService {

    public CommonResult<IPage<AnnouncementVo>> getAnnouncementList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<IPage<AnnouncementVo>> getAdminAnnouncementList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<Void> addAnnouncement(Announcement announcement);

    public CommonResult<Void> updateAnnouncement(Announcement announcement);

    public CommonResult<Void> deleteAnnouncement(Long aid);

}
