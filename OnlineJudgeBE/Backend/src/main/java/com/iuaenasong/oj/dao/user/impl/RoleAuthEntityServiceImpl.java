/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.user.impl;

import com.iuaenasong.oj.pojo.entity.user.Auth;
import com.iuaenasong.oj.pojo.entity.user.RoleAuth;
import com.iuaenasong.oj.mapper.RoleAuthMapper;
import com.iuaenasong.oj.dao.user.RoleAuthEntityService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleAuthEntityServiceImpl extends ServiceImpl<RoleAuthMapper, RoleAuth> implements RoleAuthEntityService {

    @Autowired
    private RoleAuthMapper roleAuthMapper;

    @Override
    public IPage<Auth> getAuthList(int limit, int currentPage, long rid, String keyword) {

        IPage<Auth> iPage = new Page<>(currentPage, limit);
        List<Auth> authList = roleAuthMapper.getAuthList(iPage, rid, keyword);

        return iPage.setRecords(authList);
    }

}
