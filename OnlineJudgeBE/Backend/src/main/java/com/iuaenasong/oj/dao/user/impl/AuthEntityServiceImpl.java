/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.user.impl;

import com.iuaenasong.oj.pojo.entity.user.Auth;
import com.iuaenasong.oj.mapper.AuthMapper;
import com.iuaenasong.oj.dao.user.AuthEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AuthEntityServiceImpl extends ServiceImpl<AuthMapper, Auth> implements AuthEntityService {

}
