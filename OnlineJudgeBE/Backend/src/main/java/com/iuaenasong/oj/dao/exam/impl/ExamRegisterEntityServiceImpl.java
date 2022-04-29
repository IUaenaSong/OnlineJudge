/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.dao.exam.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iuaenasong.oj.dao.exam.ExamRegisterEntityService;
import com.iuaenasong.oj.mapper.ExamRegisterMapper;
import com.iuaenasong.oj.pojo.entity.exam.ExamRegister;
import org.springframework.stereotype.Service;

@Service
public class ExamRegisterEntityServiceImpl extends ServiceImpl<ExamRegisterMapper, ExamRegister> implements ExamRegisterEntityService {

}
