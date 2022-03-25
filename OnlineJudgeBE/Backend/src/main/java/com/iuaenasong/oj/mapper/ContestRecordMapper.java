/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.iuaenasong.oj.pojo.entity.contest.ContestRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iuaenasong.oj.pojo.vo.ContestRecordVo;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface ContestRecordMapper extends BaseMapper<ContestRecord> {
    List<ContestRecord> getACInfo(@Param("status") Integer status, @Param("cid") Long cid);

    List<ContestRecordVo> getOIContestRecordByRecentSubmission(@Param("cid") Long cid,
                                                                @Param("contestAuthor") String contestAuthor,
                                                                @Param("isOpenSealRank") Boolean isOpenSealRank,
                                                                @Param("sealTime") Date sealTime,
                                                                @Param("startTime") Date startTime,
                                                                @Param("endTime") Date endTime);

    List<ContestRecordVo> getOIContestRecordByHighestSubmission(@Param("cid") Long cid,
                                                                @Param("contestAuthor") String contestAuthor,
                                                                @Param("isOpenSealRank") Boolean isOpenSealRank,
                                                                @Param("sealTime") Date sealTime,
                                                                @Param("startTime") Date startTime,
                                                                @Param("endTime") Date endTime);

    List<ContestRecordVo> getACMContestRecord(@Param("username") String username, @Param("cid") Long cid);
}
