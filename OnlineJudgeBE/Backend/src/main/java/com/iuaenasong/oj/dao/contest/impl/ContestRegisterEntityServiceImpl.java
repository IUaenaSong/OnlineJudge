/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.contest.impl;

import com.iuaenasong.oj.pojo.entity.contest.ContestRegister;
import com.iuaenasong.oj.mapper.ContestRegisterMapper;
import com.iuaenasong.oj.dao.contest.ContestRegisterEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ContestRegisterEntityServiceImpl extends ServiceImpl<ContestRegisterMapper, ContestRegister> implements ContestRegisterEntityService {

}
