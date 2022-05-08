/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iuaenasong.oj.pojo.entity.exam.ExamRecord;
import com.iuaenasong.oj.pojo.vo.ExamProblemKVo;
import com.iuaenasong.oj.pojo.vo.ExamRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {

    List<ExamRecordVo> getExamRecordByRecentSubmission(@Param("id") Long eid,
                                                     @Param("uid") String uid,
                                                     @Param("superAdminUidList") List<String> superAdminUidList,
                                                     @Param("startTime") Date startTime,
                                                     @Param("endTime") Date endTime);

    List<ExamRecordVo> getExamRecordByHighestSubmission(@Param("eid") Long eid,
                                                        @Param("uid") String uid,
                                                        @Param("superAdminUidList") List<String> superAdminUidList,
                                                        @Param("startTime") Date startTime,
                                                        @Param("endTime") Date endTime);

    List<ExamProblemKVo> getExamProblemK(@Param("eid") Long eid);
}
