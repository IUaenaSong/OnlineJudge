/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.contest.impl;

import com.iuaenasong.oj.pojo.entity.contest.ContestScore;
import com.iuaenasong.oj.mapper.ContestScoreMapper;
import com.iuaenasong.oj.dao.contest.ContestScoreEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ContestScoreEntityServiceImpl extends ServiceImpl<ContestScoreMapper, ContestScore> implements ContestScoreEntityService {

}
