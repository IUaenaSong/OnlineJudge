/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.group.problem;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.ProblemDto;
import com.iuaenasong.oj.pojo.entity.judge.CompileDTO;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.entity.problem.ProblemCase;
import com.iuaenasong.oj.pojo.entity.problem.Tag;
import com.iuaenasong.oj.pojo.vo.ProblemVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface GroupProblemService {

    public CommonResult<IPage<ProblemVo>> getProblemList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<IPage<Problem>> getAdminProblemList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<Problem> getProblem(Long pid);

    public CommonResult<Void> addProblem(ProblemDto problemDto);

    public CommonResult<Void> updateProblem(ProblemDto problemDto);

    public CommonResult<Void> deleteProblem(Long pid);

    public CommonResult<List<ProblemCase>> getProblemCases(Long pid, Boolean isUpload);

    public CommonResult<List<Tag>> getAllProblemTagsList(Long gid);

    public CommonResult<Void> compileSpj(CompileDTO compileDTO, Long gid);

    public CommonResult<Void> compileInteractive(CompileDTO compileDTO, Long gid);

    public CommonResult<Void> changeProblemAuth(Long pid, Integer auth);
}
