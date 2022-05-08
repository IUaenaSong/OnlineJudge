/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.rejudge;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.judge.Judge;

public interface RejudgeService {

    CommonResult<Judge> rejudge(Long submitId);

    CommonResult<Void> rejudgeContestProblem(Long cid,Long pid);

    CommonResult<Void> rejudgeExamProblem(Long eid,Long pid);
}
