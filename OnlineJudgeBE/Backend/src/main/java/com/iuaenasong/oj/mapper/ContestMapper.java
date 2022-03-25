/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.iuaenasong.oj.pojo.vo.ContestRegisterCountVo;
import com.iuaenasong.oj.pojo.vo.ContestVo;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

@Mapper
@Repository
public interface ContestMapper extends BaseMapper<Contest> {
    List<ContestVo> getContestList(IPage page,@Param("type") Integer type,
                                   @Param("status")Integer status,@Param("keyword")String keyword);

    List<ContestRegisterCountVo> getContestRegisterCount(@Param("cidList")List<Long> cidList);

    ContestVo getContestInfoById(@Param("cid")long cid);

    List<ContestVo> getWithinNext14DaysContests();
}
