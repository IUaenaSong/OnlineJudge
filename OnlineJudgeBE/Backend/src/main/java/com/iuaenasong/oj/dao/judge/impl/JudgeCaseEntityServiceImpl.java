/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.judge.impl;

import com.iuaenasong.oj.pojo.entity.judge.JudgeCase;
import com.iuaenasong.oj.mapper.JudgeCaseMapper;
import com.iuaenasong.oj.dao.judge.JudgeCaseEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class JudgeCaseEntityServiceImpl extends ServiceImpl<JudgeCaseMapper, JudgeCase> implements JudgeCaseEntityService {

}
