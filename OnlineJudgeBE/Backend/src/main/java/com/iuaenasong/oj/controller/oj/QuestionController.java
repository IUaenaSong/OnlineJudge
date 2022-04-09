/**
 * @Author LengYun
 * @Since 2022/04/02 16:51
 * @Description
 */

package com.iuaenasong.oj.controller.oj;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.question.Question;
import com.iuaenasong.oj.service.oj.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @RequestMapping(value = "/get-question-detail", method = RequestMethod.GET)
    public CommonResult<Question> getQuestionInfo(@RequestParam(value = "questionId", required = true) String questionId) {
        return questionService.getQuestionInfo(questionId);
    }

}