/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.admin.role;

import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.dao.user.RoleAuthEntityService;
import com.iuaenasong.oj.pojo.entity.user.Auth;
import com.iuaenasong.oj.pojo.entity.user.Role;
import com.iuaenasong.oj.pojo.entity.user.RoleAuth;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminRoleAuthManager {

    @Autowired
    private RoleAuthEntityService roleAuthEntityService;

    public IPage<Auth> getAuthList(Integer limit, Integer currentPage, Long rid, String keyword) {

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;

        if (!StringUtils.isEmpty(keyword)) {
            keyword = keyword.trim();
        }
        return roleAuthEntityService.getAuthList(limit, currentPage, rid, keyword);
    }

    public void addAuth(RoleAuth roleAuth) throws StatusFailException {

        boolean isOk = roleAuthEntityService.save(roleAuth);
        if (!isOk) {
            throw new StatusFailException("添加失败");
        }
    }

    public void deleteAuth(Long rid, Long aid) throws StatusFailException {

        QueryWrapper<RoleAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", rid).eq("auth_id", aid);
        boolean isOk = roleAuthEntityService.remove(queryWrapper);
        if (!isOk) {
            throw new StatusFailException("删除失败");
        }
    }
}
