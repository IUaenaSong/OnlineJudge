/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.AdminEditUserDto;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;

import java.util.List;
import java.util.Map;

public interface AdminUserService {

    public CommonResult<IPage<UserRolesVo>> getUserList(Integer limit, Integer currentPage, Boolean onlyAdmin, String keyword);

    public CommonResult<Void> editUser(AdminEditUserDto adminEditUserDto);

    public CommonResult<Void> deleteUser(List<String> deleteUserIdList);

    public CommonResult<Void> insertBatchUser(List<List<String>> users);

    public CommonResult<Map<Object,Object>> generateUser(Map<String, Object> params);

}