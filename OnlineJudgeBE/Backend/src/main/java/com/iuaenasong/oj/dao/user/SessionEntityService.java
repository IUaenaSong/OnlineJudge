/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.entity.user.Session;

public interface SessionEntityService extends IService<Session> {

    public void checkRemoteLogin(String uid);

}
