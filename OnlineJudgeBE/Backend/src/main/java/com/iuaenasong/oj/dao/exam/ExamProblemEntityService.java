/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.dao.exam;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.entity.exam.ExamProblem;
import com.iuaenasong.oj.pojo.vo.ExamProblemVo;

import java.util.Date;
import java.util.List;

public interface ExamProblemEntityService extends IService<ExamProblem> {
    List<ExamProblemVo> getExamProblemList(Long eid, Date startTime, Date endTime, Boolean isAdmin, String examAuthorUid);

    void syncExamRecord(Long pid, Long eid, String displayId, Integer score);
}
