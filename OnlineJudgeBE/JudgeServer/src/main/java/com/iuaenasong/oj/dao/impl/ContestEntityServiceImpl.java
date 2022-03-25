/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;;
import com.iuaenasong.oj.mapper.ContestMapper;

import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.dao.ContestEntityService;

@Service
public class ContestEntityServiceImpl extends ServiceImpl<ContestMapper, Contest> implements ContestEntityService {

}
