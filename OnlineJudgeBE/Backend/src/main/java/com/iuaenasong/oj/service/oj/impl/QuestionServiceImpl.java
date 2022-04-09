/**
 * @Author LengYun
 * @Since 2022/04/04 16:41
 * @Description
 */

package com.iuaenasong.oj.service.oj.impl;

import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.common.result.ResultStatus;
import com.iuaenasong.oj.manager.oj.QuestionManager;
import com.iuaenasong.oj.pojo.entity.question.Question;
import com.iuaenasong.oj.service.oj.QuestionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Resource
    private QuestionManager questionManager;

    @Override
    public CommonResult<Question> getQuestionInfo(String questionId) {
        try {
            return CommonResult.successResponse(questionManager.getQuestionInfo(questionId));
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }
}