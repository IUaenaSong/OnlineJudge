/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.controller.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.annotation.OJAccess;
import com.iuaenasong.oj.annotation.OJAccessEnum;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.RegisterExamDto;
import com.iuaenasong.oj.pojo.dto.SubmitQuestionDto;
import com.iuaenasong.oj.pojo.entity.question.Question;
import com.iuaenasong.oj.pojo.vo.*;
import com.iuaenasong.oj.service.oj.ExamService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ExamController {

    @Autowired
    private ExamService examService;

    @GetMapping("/get-exam-info")
    @RequiresAuthentication
    public CommonResult<ExamVo> getExamInfo(@RequestParam(value = "eid", required = true) Long eid) {

        return examService.getExamInfo(eid);
    }

    @PostMapping("/register-exam")
    @RequiresAuthentication
    public CommonResult<Void> toRegisterExam(@RequestBody RegisterExamDto registerExamDto) {
        return examService.toRegisterExam(registerExamDto);
    }

    @RequiresAuthentication
    @GetMapping("/get-exam-access")
    public CommonResult<AccessVo> getExamAccess(@RequestParam(value = "eid") Long eid) {

        return examService.getExamAccess(eid);
    }

    @GetMapping("/get-exam-problem-list")
    @RequiresAuthentication
    public CommonResult<List<ExamProblemVo>> getExamProblem(@RequestParam(value = "eid", required = true) Long eid) {

        return examService.getExamProblemList(eid);
    }

    @GetMapping("/get-exam-problem-details")
    @RequiresAuthentication
    public CommonResult<ProblemInfoVo> getExamProblemDetails(@RequestParam(value = "eid", required = true) Long eid,
                                                             @RequestParam(value = "displayId", required = true) String displayId) {

        return examService.getExamProblemDetails(eid, displayId);
    }

    @GetMapping("/get-exam-question-list")
    @RequiresAuthentication
    public CommonResult<List<ExamQuestionVo>> getExamQuestion(@RequestParam(value = "type", required = false) Integer type,
                                                             @RequestParam(value = "eid", required = true) Long eid) {

        return examService.getExamQuestionList(type, eid);
    }

    @GetMapping("/get-exam-question-details")
    @RequiresAuthentication
    public CommonResult<Question> getExamQuestionDetails(@RequestParam(value = "eid", required = true) Long eid,
                                                         @RequestParam(value = "displayId", required = true) String displayId) {

        return examService.getExamQuestionDetails(eid, displayId);
    }

    @GetMapping("/get-exam-question-status")
    @RequiresAuthentication
    public CommonResult<List<String>> getExamQuestionStatus(@RequestParam(value = "eid", required = true) Long eid) {

        return examService.getExamQuestionStatus(eid);
    }

    @PostMapping("/submit-question")
    @RequiresAuthentication
    public CommonResult<Void> submitQuestion(@RequestBody SubmitQuestionDto submitQuestionDto) {

        return examService.submitQuestion(submitQuestionDto);
    }

    @GetMapping("/get-exam-problem-k")
    public CommonResult<HashMap<String, Double>> getExamProblemK(@RequestParam("eid") Long eid) {

        return examService.getExamProblemK(eid);
    }

    @PostMapping("/submit-exam-paper-score")
    @RequiresAuthentication
    public CommonResult<Void> submitScore(@RequestBody ExamPaperVo examPaperVo,
                                          @RequestParam(value = "eid") Long eid) {

        return examService.submitScore(examPaperVo, eid);
    }

    @PostMapping("/rejudge-question")
    @RequiresAuthentication
    public CommonResult<Void> rejudgeExamQuestion(@RequestParam(value = "qid", required = true) Long qid,
                                             @RequestParam(value = "eid", required = true) Long eid) {

        return examService.rejudgeExamQuestion(qid, eid);
    }

    @GetMapping("/get-my-answer")
    @RequiresAuthentication
    public CommonResult<String> getMyAnswer(@RequestParam(value = "questionId", required = true) String questionId,
                                            @RequestParam(value = "eid", required = true) Long eid) {
        return examService.getMyAnswer(questionId, eid);
    }

    @GetMapping("/get-exam-submission-list")
    @RequiresAuthentication
    @OJAccess({OJAccessEnum.CONTEST_JUDGE})
    public CommonResult<IPage<JudgeVo>> getExamSubmissionList(@RequestParam(value = "limit", required = false) Integer limit,
                                                              @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                              @RequestParam(value = "onlyMine", required = false) Boolean onlyMine,
                                                              @RequestParam(value = "problemID", required = false) String displayId,
                                                              @RequestParam(value = "status", required = false) Integer searchStatus,
                                                              @RequestParam(value = "username", required = false) String searchUsername,
                                                              @RequestParam(value = "examID", required = true) Long searchEid,
                                                              @RequestParam(value = "completeProblemID", defaultValue = "false") Boolean completeProblemID) {

        return examService.getExamSubmissionList(limit,
                currentPage,
                onlyMine,
                displayId,
                searchStatus,
                searchUsername,
                searchEid,
                completeProblemID);
    }

    @GetMapping("/get-exam-paper-list")
    @RequiresAuthentication
    public CommonResult<List<ExamPaperVo>> getExamPaperList(@RequestParam(value = "eid", required = true) Long eid,
                                                            @RequestParam(value = "uid", required = false) String uid) {
        return examService.getExamPaperList(eid, uid);
    }
}