/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.dao.exam;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.entity.exam.ExamQuestion;
import com.iuaenasong.oj.pojo.vo.ExamQuestionVo;

import java.util.Date;
import java.util.List;

public interface ExamQuestionEntityService extends IService<ExamQuestion> {
    List<ExamQuestionVo> getExamQuestionList(Integer type, Long eid, Date startTime, Date endTime, Boolean isAdmin, String examAuthorUid);

    void syncExamQuestionRecord(Long qid, Long eid, String displayId);
}
