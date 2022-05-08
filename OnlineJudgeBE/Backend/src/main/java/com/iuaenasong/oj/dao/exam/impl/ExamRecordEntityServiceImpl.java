/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.dao.exam.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iuaenasong.oj.dao.exam.ExamRecordEntityService;
import com.iuaenasong.oj.mapper.ExamRecordMapper;
import com.iuaenasong.oj.pojo.entity.exam.ExamRecord;
import org.springframework.stereotype.Service;

@Service
public class ExamRecordEntityServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordEntityService {

}
