/**
 * @Author LengYun
 * @Since 2022/04/02 16:51
 * @Description
 */

package com.iuaenasong.oj.dao.group.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iuaenasong.oj.dao.group.GroupQuestionEntityService;
import com.iuaenasong.oj.mapper.GroupQuestionMapper;
import com.iuaenasong.oj.pojo.entity.question.Question;
import org.springframework.stereotype.Service;

@Service
public class GroupQuestionEntityServiceImpl extends ServiceImpl<GroupQuestionMapper, Question> implements GroupQuestionEntityService {

}
