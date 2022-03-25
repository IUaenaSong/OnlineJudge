/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.common.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import com.iuaenasong.oj.pojo.entity.common.Announcement;
import com.iuaenasong.oj.mapper.AnnouncementMapper;
import com.iuaenasong.oj.pojo.vo.AnnouncementVo;
import com.iuaenasong.oj.dao.common.AnnouncementEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementEntityServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementEntityService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public IPage<AnnouncementVo> getAnnouncementList(int limit, int currentPage,Boolean notAdmin) {
        //新建分页
        Page<AnnouncementVo> page = new Page<>(currentPage, limit);
        return announcementMapper.getAnnouncementList(page,notAdmin);
    }

    @Override
    public IPage<AnnouncementVo> getContestAnnouncement(Long cid,Boolean notAdmin,int limit, int currentPage) {
        Page<AnnouncementVo> page = new Page<>(currentPage, limit);
        return announcementMapper.getContestAnnouncement(page,cid,notAdmin);
    }
}
