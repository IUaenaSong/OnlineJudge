/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.service.group.exam;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.ExamProblemDto;
import com.iuaenasong.oj.pojo.dto.ProblemDto;
import com.iuaenasong.oj.pojo.entity.exam.ExamProblem;

import java.util.HashMap;
import java.util.Map;

public interface GroupExamProblemService {

    public CommonResult<HashMap<String, Object>> getExamProblemList(Integer limit, Integer currentPage, String keyword, Long eid, Integer problemType, String oj);

    public CommonResult<Map<Object, Object>> addProblem(ProblemDto problemDto);

    public CommonResult<ExamProblem> getExamProblem(Long pid, Long eid);

    public CommonResult<Void> updateExamProblem(ExamProblem examProblem);

    public CommonResult<Void> deleteExamProblem(Long pid, Long eid);

    public CommonResult<Void> addProblemFromPublic(ExamProblemDto examProblemDto);

    public CommonResult<Void> addProblemFromGroup(String problemId, Long eid, String displayId);

}
