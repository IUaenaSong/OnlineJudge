/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.dao.exam;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.vo.ExamVo;

import java.util.List;

public interface ExamEntityService extends IService<Exam> {

    ExamVo getExamInfoById(long eid);

    Void setRegisterCount(List<ExamVo> examList);
}
