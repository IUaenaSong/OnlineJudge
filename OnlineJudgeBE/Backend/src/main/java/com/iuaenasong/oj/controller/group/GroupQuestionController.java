/**
 * @Author LengYun
 * @Since 2022/04/02 16:51
 * @Description
 */

package com.iuaenasong.oj.controller.group;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.question.Question;
import com.iuaenasong.oj.service.group.question.GroupQuestionService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiresAuthentication
@RequestMapping("/api/group")
public class GroupQuestionController {

    @Autowired
    private GroupQuestionService groupQuestionService;

    @GetMapping("/get-question-list")
    public CommonResult<IPage<Question>> getQuestionList(@RequestParam(value = "limit", required = false) Integer limit,
                                                         @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                         @RequestParam(value = "type", required = false) Integer type,
                                                         @RequestParam(value = "gid", required = true) Long gid) {
        return groupQuestionService.getQuestionList(limit, currentPage, type, gid);
    }

    @GetMapping("/get-admin-question-list")
    public CommonResult<IPage<Question>> getAdminQuestionList(@RequestParam(value = "limit", required = false) Integer limit,
                                                              @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                              @RequestParam(value = "type", required = false) Integer type,
                                                              @RequestParam(value = "gid", required = true) Long gid) {
        return groupQuestionService.getAdminQuestionList(limit, currentPage, type, gid);
    }

    @GetMapping("/question")
    public CommonResult<Question> getQuestion(@RequestParam("qid") Long qid) {
        return groupQuestionService.getQuestion(qid);
    }

    @PostMapping("/question")
    public CommonResult<Void> addQuestion(@RequestBody Question question) {
        return groupQuestionService.addQuestion(question);
    }

    @PutMapping("/question")
    public CommonResult<Void> updateQuestion(@RequestBody Question question) {
        return groupQuestionService.updateQuestion(question);
    }

    @DeleteMapping("/question")
    public CommonResult<Void> deleteQuestion(@RequestParam(value = "qid", required = true) Long qid) {
        return groupQuestionService.deleteQuestion(qid);
    }

    @PutMapping("/change-question-auth")
    public CommonResult<Void> changeQuestionAuth(@RequestParam(value = "qid", required = true) Long qid,
                                                @RequestParam(value = "auth", required = true) Integer auth) {
        return groupQuestionService.changeQuestionAuth(qid, auth);
    }
}
