/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.user;

import com.iuaenasong.oj.pojo.entity.user.Auth;
import com.iuaenasong.oj.pojo.entity.user.RoleAuth;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RoleAuthEntityService extends IService<RoleAuth> {
    IPage<Auth> getAuthList(int limit, int currentPage, long rid, String keyword);
}
