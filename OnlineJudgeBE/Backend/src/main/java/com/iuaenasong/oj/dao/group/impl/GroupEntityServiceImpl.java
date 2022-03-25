/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.group.impl;

import com.iuaenasong.oj.dao.group.GroupEntityService;
import com.iuaenasong.oj.mapper.GroupMapper;
import com.iuaenasong.oj.pojo.entity.group.Group;
import com.iuaenasong.oj.pojo.vo.GroupVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupEntityServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupEntityService {

    @Autowired
    private GroupMapper groupMapper;

    @Override
    public IPage<GroupVo> getGroupList(int limit, int currentPage, String keyword, Integer auth, String uid, Boolean onlyMine) {
        IPage<GroupVo> iPage = new Page<>(currentPage, limit);
        List<GroupVo> groupList = groupMapper.getGroupList(iPage, keyword, auth, uid, onlyMine);

        return iPage.setRecords(groupList);
    }
}
