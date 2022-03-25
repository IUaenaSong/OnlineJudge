/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.user;

import com.iuaenasong.oj.pojo.dto.RegisterDto;
import com.iuaenasong.oj.pojo.entity.user.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserInfoEntityService extends IService<UserInfo> {

    public Boolean addUser(RegisterDto registerDto);

    public List<String> getSuperAdminUidList();
}
