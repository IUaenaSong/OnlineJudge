/**
 * @Author LengYun
 * @Since 2022/04/02 16:51
 * @Description
 */

package com.iuaenasong.oj.service.group.question;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.question.Question;

public interface GroupQuestionService {

    public CommonResult<IPage<Question>> getQuestionList(Integer limit, Integer currentPage, Integer type, Long gid);

    public CommonResult<IPage<Question>> getAdminQuestionList(Integer limit, Integer currentPage, Integer type, Long gid);

    public CommonResult<Question> getQuestion(Long qid);

    public CommonResult<Void> addQuestion(Question question);

    public CommonResult<Void> updateQuestion(Question question);

    public CommonResult<Void> deleteQuestion(Long qid);

    public CommonResult<Void> changeQuestionAuth(Long qid, Integer auth);
}
