/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.vo.ExamRegisterCountVo;
import com.iuaenasong.oj.pojo.vo.ExamVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ExamMapper extends BaseMapper<Exam> {

    List<ExamRegisterCountVo> getExamRegisterCount(@Param("eidList")List<Long> eidList);

    ExamVo getExamInfoById(@Param("eid")long eid);
}
