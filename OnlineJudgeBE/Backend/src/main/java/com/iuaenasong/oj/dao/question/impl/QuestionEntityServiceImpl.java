/**
 * @Author LengYun
 * @Since 2022/04/02 16:51
 * @Description
 */

package com.iuaenasong.oj.dao.question.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iuaenasong.oj.dao.question.QuestionEntityService;
import com.iuaenasong.oj.mapper.QuestionMapper;
import com.iuaenasong.oj.pojo.entity.question.Question;
import org.springframework.stereotype.Service;

@Service
public class QuestionEntityServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionEntityService {

}
