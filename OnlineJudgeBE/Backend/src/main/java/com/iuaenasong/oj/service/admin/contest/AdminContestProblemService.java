/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.contest;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.ContestProblemDto;
import com.iuaenasong.oj.pojo.dto.ProblemDto;
import com.iuaenasong.oj.pojo.entity.contest.ContestProblem;
import com.iuaenasong.oj.pojo.entity.problem.Problem;

import java.util.HashMap;
import java.util.Map;

public interface AdminContestProblemService {

    public CommonResult<HashMap<String, Object>> getProblemList(Integer limit, Integer currentPage, String keyword,
                                                                Long cid, Integer problemType, String oj);

    public CommonResult<Problem> getProblem(Long pid);

    public CommonResult<Void> deleteProblem(Long pid, Long cid);

    public CommonResult<Map<Object, Object>> addProblem(ProblemDto problemDto);

    public CommonResult<Void> updateProblem(ProblemDto problemDto);

    public CommonResult<ContestProblem> getContestProblem(Long cid, Long pid);

    public CommonResult<ContestProblem> setContestProblem(ContestProblem contestProblem);

    public CommonResult<Void> addProblemFromPublic(ContestProblemDto contestProblemDto);

    public CommonResult<Void> importContestRemoteOJProblem(String name, String problemId, Long cid, String displayId);

}
