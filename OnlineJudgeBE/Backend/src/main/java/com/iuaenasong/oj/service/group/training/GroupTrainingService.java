/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.group.training;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.TrainingDto;
import com.iuaenasong.oj.pojo.entity.training.Training;
import com.iuaenasong.oj.pojo.vo.TrainingVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface GroupTrainingService {

    public CommonResult<IPage<TrainingVo>> getTrainingList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<IPage<Training>> getAdminTrainingList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<TrainingDto> getTraining(Long tid);

    public CommonResult<Void> addTraining(TrainingDto trainingDto);

    public CommonResult<Void> updateTraining(TrainingDto trainingDto);

    public CommonResult<Void> deleteTraining(Long tid);

    public CommonResult<Void> changeTrainingStatus(Long tid, Boolean status);

}
