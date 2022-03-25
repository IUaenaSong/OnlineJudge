/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.pojo.entity.user.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;

public interface UserRoleEntityService extends IService<UserRole> {

    UserRolesVo getUserRoles(String uid,String username);

    IPage<UserRolesVo> getUserList(int limit, int currentPage, String keyword,Boolean onlyAdmin);

    void deleteCache(String uid, boolean isRemoveSession);

    String getAuthChangeContent(int oldType,int newType);
}
