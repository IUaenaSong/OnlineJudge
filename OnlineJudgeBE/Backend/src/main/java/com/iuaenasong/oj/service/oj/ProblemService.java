/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.PidListDto;
import com.iuaenasong.oj.pojo.vo.ProblemInfoVo;
import com.iuaenasong.oj.pojo.vo.ProblemVo;
import com.iuaenasong.oj.pojo.vo.RandomProblemVo;

import java.util.HashMap;
import java.util.List;

public interface ProblemService {

    public CommonResult<Page<ProblemVo>> getProblemList(Integer limit, Integer currentPage,
                                                        String keyword, List<Long> tagId, Integer difficulty, String oj);

    public CommonResult<RandomProblemVo> getRandomProblem();

    public CommonResult<HashMap<Long, Object>> getUserProblemStatus(PidListDto pidListDto);

    public CommonResult<ProblemInfoVo> getProblemInfo(String problemId);

}