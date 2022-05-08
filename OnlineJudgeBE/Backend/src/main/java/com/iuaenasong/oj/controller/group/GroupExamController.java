/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.group;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.ExamProblemDto;
import com.iuaenasong.oj.pojo.dto.ExamQuestionDto;
import com.iuaenasong.oj.pojo.dto.ProblemDto;
import com.iuaenasong.oj.pojo.entity.exam.ExamProblem;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.entity.exam.ExamQuestion;
import com.iuaenasong.oj.pojo.entity.question.Question;
import com.iuaenasong.oj.pojo.vo.AdminExamVo;
import com.iuaenasong.oj.pojo.vo.ExamVo;
import com.iuaenasong.oj.service.group.exam.GroupExamProblemService;
import com.iuaenasong.oj.service.group.exam.GroupExamQuestionService;
import com.iuaenasong.oj.service.group.exam.GroupExamService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiresAuthentication
@RequestMapping("/api/group")
public class GroupExamController {

    @Autowired
    private GroupExamService groupExamService;

    @Autowired
    private GroupExamProblemService groupExamProblemService;

    @Autowired
    private GroupExamQuestionService groupExamQuestionService;

    @GetMapping("/get-exam-list")
    public CommonResult<IPage<ExamVo>> getExamList(@RequestParam(value = "limit", required = false) Integer limit,
                                                   @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                   @RequestParam(value = "gid", required = true) Long gid) {
        return groupExamService.getExamList(limit, currentPage, gid);
    }

    @GetMapping("/get-admin-exam-list")
    public CommonResult<IPage<Exam>> getAdminExamList(@RequestParam(value = "limit", required = false) Integer limit,
                                                      @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                      @RequestParam(value = "gid", required = true) Long gid) {
        return groupExamService.getAdminExamList(limit, currentPage, gid);
    }

    @GetMapping("/exam")
    public CommonResult<AdminExamVo> getExam(@RequestParam("eid") Long eid) {
        return groupExamService.getExam(eid);
    }

    @PostMapping("/exam")
    public CommonResult<Void> addExam(@RequestBody AdminExamVo adminExamVo) {
        return groupExamService.addExam(adminExamVo);
    }

    @PutMapping("/exam")
    public CommonResult<Void> updateExam(@RequestBody AdminExamVo adminExamVo) {
        return groupExamService.updateExam(adminExamVo);
    }

    @DeleteMapping("/exam")
    public CommonResult<Void> deleteExam(@RequestParam(value = "eid", required = true) Long eid) {
        return groupExamService.deleteExam(eid);
    }

    @PutMapping("/change-exam-visible")
    public CommonResult<Void> changeExamVisible(@RequestParam(value = "eid", required = true) Long eid,
                                                @RequestParam(value = "visible", required = true) Boolean visible) {
        return groupExamService.changeExamVisible(eid, visible);
    }

    @GetMapping("/get-exam-problem-list")
    public CommonResult<HashMap<String, Object>> getExamProblemList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                       @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                       @RequestParam(value = "keyword", required = false) String keyword,
                                                                       @RequestParam(value = "eid", required = true) Long eid,
                                                                       @RequestParam(value = "problemType", required = false) Integer problemType,
                                                                       @RequestParam(value = "oj", required = false) String oj) {
        return groupExamProblemService.getExamProblemList(limit, currentPage, keyword, eid, problemType, oj);
    }

    @PostMapping("/exam-problem")
    public CommonResult<Map<Object, Object>> addProblem(@RequestBody ProblemDto problemDto) {

        return groupExamProblemService.addProblem(problemDto);
    }

    @GetMapping("/exam-problem")
    public CommonResult<ExamProblem> getExamProblem(@RequestParam(value = "pid", required = true) Long pid,
                                                          @RequestParam(value = "eid", required = true) Long eid) {

        return groupExamProblemService.getExamProblem(pid, eid);
    }

    @PutMapping("/exam-problem")
    public CommonResult<Void> updateExamProblem(@RequestBody ExamProblem examProblem) {

        return groupExamProblemService.updateExamProblem(examProblem);
    }

    @DeleteMapping("/exam-problem")
    public CommonResult<Void> deleteExamProblem(@RequestParam(value = "pid", required = true) Long pid,
                                                   @RequestParam(value = "eid", required = true) Long eid) {
        return groupExamProblemService.deleteExamProblem(pid, eid);
    }

    @PostMapping("/add-exam-problem-from-public")
    public CommonResult<Void> addProblemFromPublic(@RequestBody ExamProblemDto examProblemDto) {
        return groupExamProblemService.addProblemFromPublic(examProblemDto);
    }

    @PostMapping("/add-exam-problem-from-group")
    public CommonResult<Void> addProblemFromGroup(@RequestParam(value = "problemId", required = true) String problemId,
                                                  @RequestParam(value = "eid", required = true) Long eid,
                                                  @RequestParam(value = "displayId", required = true) String displayId) {
        return groupExamProblemService.addProblemFromGroup(problemId, eid, displayId);
    }

    @GetMapping("/get-exam-question-list")
    public CommonResult<HashMap<String, Object>> getExamQuestionList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                     @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                     @RequestParam(value = "keyword", required = false) String keyword,
                                                                     @RequestParam(value = "eid", required = true) Long eid,
                                                                     @RequestParam(value = "questionType", required = false) Integer questionType,
                                                                     @RequestParam(value = "queryExisted", required = false) Boolean queryExisted) {
        return groupExamQuestionService.getExamQuestionList(limit, currentPage, keyword, eid, questionType, queryExisted);
    }

    @PostMapping("/exam-question")
    public CommonResult<Map<Object, Object>> addQuestion(@RequestBody Question question) {

        return groupExamQuestionService.addQuestion(question);
    }

    @GetMapping("/exam-question")
    public CommonResult<ExamQuestion> getExamQuestion(@RequestParam(value = "qid", required = true) Long qid,
                                                     @RequestParam(value = "eid", required = true) Long eid) {

        return groupExamQuestionService.getExamQuestion(qid, eid);
    }

    @PutMapping("/exam-question")
    public CommonResult<Void> updateExamQuestion(@RequestBody ExamQuestion examQuestion) {

        return groupExamQuestionService.updateExamQuestion(examQuestion);
    }

    @DeleteMapping("/exam-question")
    public CommonResult<Void> deleteExamQuestion(@RequestParam(value = "qid", required = true) Long qid,
                                                @RequestParam(value = "eid", required = true) Long eid) {
        return groupExamQuestionService.deleteExamQuestion(qid, eid);
    }


    @PostMapping("/add-exam-question-from-group")
    public CommonResult<Void> addQuestionFromGroup(@RequestBody ExamQuestionDto examQuestionDto) {
        return groupExamQuestionService.addQuestionFromGroup(examQuestionDto);
    }

}
