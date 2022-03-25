/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.training.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.mapper.TrainingRegisterMapper;
import com.iuaenasong.oj.pojo.entity.training.TrainingRegister;
import com.iuaenasong.oj.dao.training.TrainingRegisterEntityService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingRegisterEntityServiceImpl extends ServiceImpl<TrainingRegisterMapper, TrainingRegister> implements TrainingRegisterEntityService {

    @Resource
    private TrainingRegisterMapper trainingRegisterMapper;

    @Override
    public List<String> getAlreadyRegisterUidList(Long tid){
        QueryWrapper<TrainingRegister> trainingRegisterQueryWrapper = new QueryWrapper<>();
        trainingRegisterQueryWrapper.eq("tid", tid);
        return trainingRegisterMapper.selectList(trainingRegisterQueryWrapper).stream().map(TrainingRegister::getUid).collect(Collectors.toList());
    }

}