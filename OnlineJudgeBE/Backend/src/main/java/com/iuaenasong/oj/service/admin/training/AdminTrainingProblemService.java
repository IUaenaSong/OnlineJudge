/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.training;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.TrainingProblemDto;
import com.iuaenasong.oj.pojo.entity.training.TrainingProblem;

import java.util.HashMap;

public interface AdminTrainingProblemService {

    public CommonResult<HashMap<String, Object>> getProblemList(Integer limit, Integer currentPage, String keyword, Boolean queryExisted, Long tid);

    public CommonResult<Void> updateProblem(TrainingProblem trainingProblem);

    public CommonResult<Void> deleteProblem(Long pid,Long tid);

    public CommonResult<Void> addProblemFromPublic(TrainingProblemDto trainingProblemDto);

    public CommonResult<Void> importTrainingRemoteOJProblem(String name, String problemId, Long tid);
}
