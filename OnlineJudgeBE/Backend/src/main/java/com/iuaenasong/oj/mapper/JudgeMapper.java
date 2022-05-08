/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iuaenasong.oj.pojo.vo.JudgeVo;
import com.iuaenasong.oj.pojo.vo.ProblemCountVo;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface JudgeMapper extends BaseMapper<Judge> {
    IPage<JudgeVo> getCommonJudgeList(Page<JudgeVo> page,
                                      @Param("searchPid") String searchPid,
                                      @Param("status") Integer status,
                                      @Param("username") String username,
                                      @Param("uid") String uid,
                                      @Param("gid") Long gid,
                                      @Param("completeProblemID") Boolean completeProblemID);

    IPage<JudgeVo> getContestJudgeList(Page<JudgeVo> page,
                                       @Param("displayId") String displayId,
                                       @Param("cid") Long cid,
                                       @Param("status") Integer status,
                                       @Param("username") String username,
                                       @Param("uid") String uid,
                                       @Param("isAdmin") Boolean isAdmin,
                                       @Param("rule") String rule,
                                       @Param("startTime") Date startTime,
                                       @Param("sealRankTime") Date sealRankTime,
                                       @Param("sealTimeUid") String sealTimeUid,
                                       @Param("completeProblemID")Boolean completeProblemID);

    IPage<JudgeVo> getExamJudgeList(Page<JudgeVo> page,
                                       @Param("displayId") String displayId,
                                       @Param("eid") Long eid,
                                       @Param("status") Integer status,
                                       @Param("username") String username,
                                       @Param("uid") String uid,
                                       @Param("isAdmin") Boolean isAdmin,
                                       @Param("startTime") Date startTime,
                                       @Param("endTime") Date endTime,
                                       @Param("completeProblemID") Boolean completeProblemID);

    int getTodayJudgeNum();

    ProblemCountVo getContestProblemCount(@Param("pid") Long pid,
                                          @Param("cpid") Long cpid,
                                          @Param("cid") Long cid,
                                          @Param("startTime") Date startTime,
                                          @Param("sealRankTime") Date sealRankTime,
                                          @Param("adminList") List<String> adminList);

    ProblemCountVo getExamProblemCount(@Param("pid") Long pid,
                                          @Param("epid") Long cpid,
                                          @Param("eid") Long cid,
                                          @Param("startTime") Date startTime,
                                          @Param("endTime") Date endTime,
                                          @Param("isAdmin") Boolean isAdmin,
                                          @Param("adminList") List<String> adminList);

    ProblemCountVo getProblemCount(@Param("pid") Long pid);

    List<ProblemCountVo> getProblemListCount(@Param("pidList") List<Long> pidList);
}
