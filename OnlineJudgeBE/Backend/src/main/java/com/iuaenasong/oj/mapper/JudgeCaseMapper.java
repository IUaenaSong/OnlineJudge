/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.iuaenasong.oj.pojo.entity.judge.JudgeCase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
@Repository
public interface JudgeCaseMapper extends BaseMapper<JudgeCase> {

}
