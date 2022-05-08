/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.service.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.RegisterExamDto;
import com.iuaenasong.oj.pojo.dto.SubmitQuestionDto;
import com.iuaenasong.oj.pojo.entity.question.Question;
import com.iuaenasong.oj.pojo.vo.*;

import java.util.HashMap;
import java.util.List;

public interface ExamService {

    public CommonResult<ExamVo> getExamInfo(Long eid);

    public CommonResult<Void> toRegisterExam(RegisterExamDto registerExamDto);

    public CommonResult<AccessVo> getExamAccess(Long eid);

    public CommonResult<List<ExamProblemVo>> getExamProblemList(Long eid);

    public CommonResult<ProblemInfoVo> getExamProblemDetails(Long eid, String displayId);

    public CommonResult<List<ExamQuestionVo>> getExamQuestionList(Integer type, Long eid);

    public CommonResult<Question> getExamQuestionDetails(Long eid, String displayId);

    public CommonResult<List<String>> getExamQuestionStatus(Long eid);

    public CommonResult<Void> submitQuestion(SubmitQuestionDto submitQuestionDto);

    public CommonResult<HashMap<String, Double>> getExamProblemK(Long eid);

    public CommonResult<Void> submitScore(ExamPaperVo examPaperVo, Long eid);

    public CommonResult<Void> rejudgeExamQuestion(Long qid, Long eid);

    public CommonResult<String> getMyAnswer(String questionId, Long eid);

    public CommonResult<IPage<JudgeVo>> getExamSubmissionList(Integer limit,
                                                                 Integer currentPage,
                                                                 Boolean onlyMine,
                                                                 String displayId,
                                                                 Integer searchStatus,
                                                                 String searchUsername,
                                                                 Long searchEid,
                                                                 Boolean completeProblemID);

    public CommonResult<List<ExamPaperVo>> getExamPaperList(Long eid, String uid);

}
