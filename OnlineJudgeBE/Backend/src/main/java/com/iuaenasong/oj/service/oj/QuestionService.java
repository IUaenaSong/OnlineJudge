/**
 * @Author LengYun
 * @Since 2022/04/04 16:41
 * @Description
 */

package com.iuaenasong.oj.service.oj;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.question.Question;

public interface QuestionService {

    public CommonResult<Question> getQuestionInfo(String questionId);

}