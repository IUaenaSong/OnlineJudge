/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.admin.auth;

import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.dao.user.AuthEntityService;
import com.iuaenasong.oj.pojo.entity.user.Auth;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminAuthManager {

    @Autowired
    private AuthEntityService authEntityService;

    public IPage<Auth> getAuthList(Integer limit, Integer currentPage, String keyword) {

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;

        QueryWrapper<Auth> queryWrapper = new QueryWrapper<>();

        IPage<Auth> iPage = new Page<>(currentPage, limit);

        if (!StringUtils.isEmpty(keyword)) {
            final String key = keyword.trim();
            queryWrapper.and(wrapper -> wrapper.like("role", key).or()
                    .like("description", key).or());
        }
        return authEntityService.page(iPage, queryWrapper);
    }

    public void deleteAuth(Long aid) throws StatusFailException {

        boolean isOk = authEntityService.removeById(aid);
        if (!isOk) { // 删除成功
            throw new StatusFailException("删除失败");
        }
    }

    public void addAuth(Auth auth) throws StatusFailException {

        boolean isOk = authEntityService.save(auth);
        if (!isOk) {
            throw new StatusFailException("添加失败");
        }
    }

    public void updateAuth(Auth auth) throws StatusFailException {

        boolean isOk = authEntityService.updateById(auth);
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

    public void changeAuthStatus(Long aid, Integer status) throws StatusFailException {

        UpdateWrapper<Auth> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", aid).set("status", status);
        boolean isOk = authEntityService.update(updateWrapper);
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }
}
