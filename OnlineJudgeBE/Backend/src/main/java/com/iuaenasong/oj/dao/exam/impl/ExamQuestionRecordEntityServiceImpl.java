/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.dao.exam.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iuaenasong.oj.dao.exam.ExamQuestionRecordEntityService;
import com.iuaenasong.oj.mapper.ExamQuestionRecordMapper;
import com.iuaenasong.oj.pojo.entity.exam.ExamQuestionRecord;
import org.springframework.stereotype.Service;

@Service
public class ExamQuestionRecordEntityServiceImpl extends ServiceImpl<ExamQuestionRecordMapper, ExamQuestionRecord> implements ExamQuestionRecordEntityService {

}
