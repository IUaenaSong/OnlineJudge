/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.iuaenasong.oj.pojo.entity.training.TrainingRecord;
import com.iuaenasong.oj.pojo.vo.TrainingRecordVo;

import java.util.List;

@Mapper
@Repository
public interface TrainingRecordMapper extends BaseMapper<TrainingRecord> {

    public List<TrainingRecordVo> getTrainingRecord(@Param("tid") Long tid);
}