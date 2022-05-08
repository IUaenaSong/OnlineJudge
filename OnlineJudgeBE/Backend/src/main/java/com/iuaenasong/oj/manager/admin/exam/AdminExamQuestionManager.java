/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.manager.admin.exam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iuaenasong.oj.dao.exam.ExamEntityService;
import com.iuaenasong.oj.dao.exam.ExamQuestionEntityService;
import com.iuaenasong.oj.dao.question.QuestionEntityService;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.entity.exam.ExamQuestion;
import com.iuaenasong.oj.pojo.entity.question.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdminExamQuestionManager {

    @Autowired
    private ExamQuestionEntityService examQuestionEntityService;

    @Autowired
    private QuestionEntityService questionEntityService;

    @Autowired
    private ExamEntityService examEntityService;

    public HashMap<String, Object> getQuestionList(Integer limit, Integer currentPage, String keyword,
                                                  Long eid, Integer questionType, Boolean queryExisted) {
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        IPage<Question> iPage = new Page<>(currentPage, limit);
        // 根据eid在ExamQuestion表中查询到对应qid集合
        QueryWrapper<ExamQuestion> examQuestionQueryWrapper = new QueryWrapper<>();
        examQuestionQueryWrapper.eq("eid", eid);
        List<Long> qidList = new LinkedList<>();

        List<ExamQuestion> examQuestionList = examQuestionEntityService.list(examQuestionQueryWrapper);
        HashMap<Long, ExamQuestion> examQuestionMap = new HashMap<>();
        examQuestionList.forEach(examQuestion -> {
            examQuestionMap.put(examQuestion.getQid(), examQuestion);
            qidList.add(examQuestion.getQid());
        });

        HashMap<String, Object> examQuestion = new HashMap<>();

        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();

        if (questionType != 0) {
            questionQueryWrapper.eq("type", questionType);
        }

        if (!queryExisted) { // 必备条件 隐藏的不可取来做考试题目
            Exam exam = examEntityService.getById(eid);
            questionQueryWrapper.eq("gid", exam.getGid());
        }

        // 逻辑判断，如果是查询已有的就应该是in，如果是查询不要重复的，使用not in
        if (!queryExisted) {
            questionQueryWrapper.notIn(qidList.size() > 0, "id", qidList);
        } else {
            questionQueryWrapper.in(qidList.size() > 0, "id", qidList);
        }

        if (!StringUtils.isEmpty(keyword)) {
            questionQueryWrapper.and(wrapper -> wrapper.like("description", keyword).or()
                    .like("question_id", keyword).or()
                    .like("author", keyword));
        }

        if (qidList.size() == 0 && queryExisted) {
            questionQueryWrapper = new QueryWrapper<>();
            questionQueryWrapper.eq("id", null);
        }

        IPage<Question> questionListPage = questionEntityService.page(iPage, questionQueryWrapper);

        if (qidList.size() > 0 && queryExisted) {
            List<Question> questionList = questionListPage.getRecords();

            List<Question> sortedQuestionList = questionList.stream().sorted(Comparator.comparing(Question::getId, (a, b) -> {
                ExamQuestion x = examQuestionMap.get(a);
                ExamQuestion y = examQuestionMap.get(b);
                if (x == null && y != null) {
                    return 1;
                } else if (x != null && y == null) {
                    return -1;
                } else if (x == null) {
                    return -1;
                } else {
                    return x.getDisplayId().compareTo(y.getDisplayId());
                }
            })).collect(Collectors.toList());
            questionListPage.setRecords(sortedQuestionList);
        }

        examQuestion.put("questionList", questionListPage);
        examQuestion.put("examQuestionMap", examQuestionMap);

        return examQuestion;
    }

}