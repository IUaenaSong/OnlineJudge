/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.RegisterTrainingDto;
import com.iuaenasong.oj.pojo.vo.AccessVo;
import com.iuaenasong.oj.pojo.vo.ProblemVo;
import com.iuaenasong.oj.pojo.vo.TrainingRankVo;
import com.iuaenasong.oj.pojo.vo.TrainingVo;

import java.util.List;

public interface TrainingService {

    public CommonResult<IPage<TrainingVo>> getTrainingList(Integer limit, Integer currentPage, String keyword, Long categoryId, String auth);

    public CommonResult<TrainingVo> getTraining(Long tid);

    public CommonResult<List<ProblemVo>> getTrainingProblemList(Long tid);

    public CommonResult<Void> toRegisterTraining(RegisterTrainingDto registerTrainingDto);

    public CommonResult<AccessVo> getTrainingAccess( Long tid);

    public CommonResult<IPage<TrainingRankVo>> getTrainingRank(Long tid, Integer limit, Integer currentPage);
}