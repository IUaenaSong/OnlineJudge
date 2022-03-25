/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.iuaenasong.oj.pojo.entity.common.Announcement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iuaenasong.oj.pojo.vo.AnnouncementVo;

@Mapper
@Repository
public interface AnnouncementMapper extends BaseMapper<Announcement> {
    IPage<AnnouncementVo> getAnnouncementList(Page<AnnouncementVo> page,@Param("notAdmin") Boolean notAdmin);
    IPage<AnnouncementVo> getContestAnnouncement(Page<AnnouncementVo> page,@Param("cid")Long cid,@Param("notAdmin") Boolean notAdmin);
}
