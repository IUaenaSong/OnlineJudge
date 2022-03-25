/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.contest.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.iuaenasong.oj.mapper.ContestPrintMapper;
import com.iuaenasong.oj.pojo.entity.contest.ContestPrint;
import com.iuaenasong.oj.dao.contest.ContestPrintEntityService;

@Service
public class ContestPrintEntityServiceImpl extends ServiceImpl<ContestPrintMapper, ContestPrint> implements ContestPrintEntityService {
}