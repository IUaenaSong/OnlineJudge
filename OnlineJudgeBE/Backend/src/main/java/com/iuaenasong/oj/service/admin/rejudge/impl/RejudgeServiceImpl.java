/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.rejudge.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.manager.admin.rejudge.RejudgeManager;
import com.iuaenasong.oj.pojo.entity.judge.Judge;

import com.iuaenasong.oj.service.admin.rejudge.RejudgeService;

@Service
public class RejudgeServiceImpl implements RejudgeService {

    @Autowired
    private RejudgeManager rejudgeManager;

    @Override
    public CommonResult<Judge> rejudge(Long submitId) {
        try {
            Judge judge = rejudgeManager.rejudge(submitId);
            return CommonResult.successResponse(judge, "重判成功！该提交已进入判题队列！");
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> rejudgeContestProblem(Long cid, Long pid) {
        try {
            rejudgeManager.rejudgeContestProblem(cid, pid);
            return CommonResult.successResponse("重判成功！该题目对应的全部提交已进入判题队列！");
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}