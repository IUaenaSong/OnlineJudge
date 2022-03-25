/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.training.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.mapper.TrainingRecordMapper;
import com.iuaenasong.oj.pojo.entity.training.TrainingRecord;
import com.iuaenasong.oj.pojo.vo.TrainingRecordVo;
import com.iuaenasong.oj.dao.training.TrainingRecordEntityService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TrainingRecordEntityServiceImpl extends ServiceImpl<TrainingRecordMapper, TrainingRecord> implements TrainingRecordEntityService {

    @Resource
    private TrainingRecordMapper trainingRecordMapper;

    @Override
    public List<TrainingRecordVo> getTrainingRecord(Long tid){
        return trainingRecordMapper.getTrainingRecord(tid);
    }

}