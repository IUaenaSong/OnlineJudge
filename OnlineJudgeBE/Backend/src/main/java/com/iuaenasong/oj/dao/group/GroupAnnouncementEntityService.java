/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.group;

import com.iuaenasong.oj.pojo.entity.common.Announcement;
import com.iuaenasong.oj.pojo.vo.AnnouncementVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface GroupAnnouncementEntityService extends IService<Announcement> {

    IPage<AnnouncementVo> getAnnouncementList(int limit, int currentPage, Long gid);

    IPage<AnnouncementVo> getAdminAnnouncementList(int limit, int currentPage, Long gid);

}
