/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.user.impl;

import com.iuaenasong.oj.pojo.entity.user.Role;
import com.iuaenasong.oj.mapper.RoleMapper;
import com.iuaenasong.oj.dao.user.RoleEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RoleEntityServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleEntityService {

}
