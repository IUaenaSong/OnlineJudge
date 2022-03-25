/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.judge.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.iuaenasong.oj.mapper.JudgeServerMapper;

import com.iuaenasong.oj.pojo.entity.judge.JudgeServer;
import com.iuaenasong.oj.dao.judge.JudgeServerEntityService;

@Service
public class JudgeServerEntityServiceImpl extends ServiceImpl<JudgeServerMapper, JudgeServer> implements JudgeServerEntityService {

}