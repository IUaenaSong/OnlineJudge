/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.training;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.entity.training.TrainingRecord;
import com.iuaenasong.oj.pojo.vo.TrainingRecordVo;

import java.util.List;

public interface TrainingRecordEntityService extends IService<TrainingRecord> {

    public List<TrainingRecordVo> getTrainingRecord(Long tid);

}