/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.admin.role;

import com.iuaenasong.oj.dao.user.RoleEntityService;
import com.iuaenasong.oj.pojo.entity.user.Auth;
import com.iuaenasong.oj.pojo.entity.user.Role;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminRoleManager {

    @Autowired
    private RoleEntityService roleEntityService;
    public IPage<Role> getRoleList(Integer limit, Integer currentPage, String keyword) {

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;

        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();

        IPage<Role> iPage = new Page<>(currentPage, limit);

        if (!StringUtils.isEmpty(keyword)) {
            final String key = keyword.trim();
            queryWrapper.and(wrapper -> wrapper.like("name", key).or()
                    .like("permission", key).or());
        }
        return roleEntityService.page(iPage, queryWrapper);
    }
}
