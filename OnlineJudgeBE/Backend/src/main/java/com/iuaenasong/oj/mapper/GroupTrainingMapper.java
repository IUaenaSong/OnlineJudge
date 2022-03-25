/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.mapper;

import com.iuaenasong.oj.pojo.entity.training.Training;
import com.iuaenasong.oj.pojo.vo.TrainingVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GroupTrainingMapper extends BaseMapper<Training> {

    List<TrainingVo> getTrainingList(IPage iPage, @Param("gid") Long gid);

    List<Training> getAdminTrainingList(IPage iPage, @Param("gid") Long gid);

}
