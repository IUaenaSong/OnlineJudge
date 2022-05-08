/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iuaenasong.oj.dao.ExamEntityService;
import com.iuaenasong.oj.mapper.ExamMapper;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import org.springframework.stereotype.Service;

@Service
public class ExamEntityServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamEntityService {

}
