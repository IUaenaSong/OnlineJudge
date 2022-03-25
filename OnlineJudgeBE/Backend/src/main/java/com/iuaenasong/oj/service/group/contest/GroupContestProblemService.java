/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.group.contest;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.ContestProblemDto;
import com.iuaenasong.oj.pojo.dto.ProblemDto;
import com.iuaenasong.oj.pojo.entity.contest.ContestProblem;

import java.util.HashMap;
import java.util.Map;

public interface GroupContestProblemService {

    public CommonResult<HashMap<String, Object>> getContestProblemList(Integer limit, Integer currentPage, String keyword, Long cid, Integer problemType, String oj);

    public CommonResult<Map<Object, Object>> addProblem(ProblemDto problemDto);

    public CommonResult<ContestProblem> getContestProblem(Long pid, Long cid);

    public CommonResult<Void> updateContestProblem(ContestProblem contestProblem);

    public CommonResult<Void> deleteContestProblem(Long pid, Long cid);

    public CommonResult<Void> addProblemFromPublic(ContestProblemDto contestProblemDto);

    public CommonResult<Void> addProblemFromGroup(String problemId, Long cid, String displayId);

}
