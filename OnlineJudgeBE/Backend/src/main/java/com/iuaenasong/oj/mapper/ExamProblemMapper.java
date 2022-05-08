/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iuaenasong.oj.pojo.entity.exam.ExamProblem;
import com.iuaenasong.oj.pojo.vo.ExamProblemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface ExamProblemMapper extends BaseMapper<ExamProblem> {
    List<ExamProblemVo> getExamProblemList(@Param("eid") Long eid, @Param("startTime") Date startTime, @Param("endTime") Date endTime,
                                                 @Param("isAdmin") Boolean isAdmin, @Param("adminList") List<String> adminList);
}
