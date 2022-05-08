/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.contest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.pojo.vo.ContestVo;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ContestEntityService extends IService<Contest> {

    List<ContestVo> getWithinNext14DaysContests();

    IPage<ContestVo> getContestList(Integer limit, Integer currentPage, Integer type, Integer status, String keyword);

    ContestVo getContestInfoById(long cid);

    Void setRegisterCount(List<ContestVo> contestList);
}
