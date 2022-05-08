/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.service.group.exam.impl;

import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.common.result.ResultStatus;
import com.iuaenasong.oj.manager.group.exam.GroupExamQuestionManager;
import com.iuaenasong.oj.pojo.dto.ExamQuestionDto;
import com.iuaenasong.oj.pojo.entity.exam.ExamQuestion;
import com.iuaenasong.oj.pojo.entity.question.Question;
import com.iuaenasong.oj.service.group.exam.GroupExamQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GroupExamQuestionServiceImpl implements GroupExamQuestionService {

    @Autowired
    private GroupExamQuestionManager groupExamQuestionManager;

    @Override
    public CommonResult<HashMap<String, Object>> getExamQuestionList(Integer limit, Integer currentPage, String keyword, Long eid, Integer questionType, Boolean queryExisted) {
        try {
            return CommonResult.successResponse(groupExamQuestionManager.getExamQuestionList(limit, currentPage, keyword, eid, questionType, queryExisted));
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }

    @Override
    public CommonResult<Map<Object, Object>> addQuestion(Question question) {
        try {
            return CommonResult.successResponse(groupExamQuestionManager.addQuestion(question));
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FAIL);
        }
    }

    @Override
    public CommonResult<ExamQuestion> getExamQuestion(Long qid, Long eid) {
        try {
            return CommonResult.successResponse(groupExamQuestionManager.getExamQuestion(qid, eid));
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FAIL);
        }
    }

    @Override
    public CommonResult<Void> updateExamQuestion(ExamQuestion examQuestion) {
        try {
            groupExamQuestionManager.updateExamQuestion(examQuestion);
            return CommonResult.successResponse();
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FAIL);
        }
    }

    @Override
    public CommonResult<Void> deleteExamQuestion(Long qid, Long eid) {
        try {
            groupExamQuestionManager.deleteExamQuestion(qid, eid);
            return CommonResult.successResponse();
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FAIL);
        }
    }

    @Override
    public CommonResult<Void> addQuestionFromGroup(ExamQuestionDto examQuestionDto) {
        try {
            groupExamQuestionManager.addQuestionFromGroup(examQuestionDto);
            return CommonResult.successResponse();
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FAIL);
        }
    }

}
