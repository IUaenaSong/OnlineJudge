/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.group.training;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.TrainingDto;
import com.iuaenasong.oj.pojo.dto.TrainingProblemDto;
import com.iuaenasong.oj.pojo.entity.training.Training;
import com.iuaenasong.oj.pojo.entity.training.TrainingProblem;
import com.iuaenasong.oj.pojo.vo.TrainingVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.HashMap;

public interface GroupTrainingProblemService {

    public CommonResult<HashMap<String, Object>> getTrainingProblemList(Integer limit, Integer currentPage, String keyword, Boolean queryExisted, Long tid);

    public CommonResult<Void> updateTrainingProblem(TrainingProblem trainingProblem);

    public CommonResult<Void> deleteTrainingProblem(Long pid, Long tid);

    public CommonResult<Void> addProblemFromPublic(TrainingProblemDto trainingProblemDto);

    public CommonResult<Void> addProblemFromGroup(String problemId, Long tid);

}
