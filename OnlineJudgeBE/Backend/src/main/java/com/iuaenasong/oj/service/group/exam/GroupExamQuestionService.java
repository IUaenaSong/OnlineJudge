/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.service.group.exam;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.ExamQuestionDto;
import com.iuaenasong.oj.pojo.entity.exam.ExamQuestion;
import com.iuaenasong.oj.pojo.entity.question.Question;

import java.util.HashMap;
import java.util.Map;

public interface GroupExamQuestionService {

    public CommonResult<HashMap<String, Object>> getExamQuestionList(Integer limit, Integer currentPage, String keyword, Long eid, Integer questionType, Boolean queryExisted);

    public CommonResult<Map<Object, Object>> addQuestion(Question question);

    public CommonResult<ExamQuestion> getExamQuestion(Long qid, Long eid);

    public CommonResult<Void> updateExamQuestion(ExamQuestion examQuestion);

    public CommonResult<Void> deleteExamQuestion(Long qid, Long eid);

    public CommonResult<Void> addQuestionFromGroup(ExamQuestionDto examQuestionDto);

}
