/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.contest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.entity.contest.ContestRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.vo.ContestRecordVo;

import java.util.List;

public interface ContestRecordEntityService extends IService<ContestRecord> {

    IPage<ContestRecord> getACInfo(Integer currentPage,
                                   Integer limit,
                                   Integer status,
                                   Long cid,
                                   String contestCreatorId);

    List<ContestRecordVo> getOIContestRecord(Contest contest, Boolean isOpenSealRank);

    List<ContestRecordVo> getACMContestRecord(String username, Long cid);

}
