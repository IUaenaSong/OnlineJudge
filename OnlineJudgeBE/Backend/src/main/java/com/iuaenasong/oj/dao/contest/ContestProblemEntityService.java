/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.contest;

import com.iuaenasong.oj.pojo.entity.contest.ContestProblem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.vo.ContestProblemVo;

import java.util.Date;
import java.util.List;

public interface ContestProblemEntityService extends IService<ContestProblem> {
    List<ContestProblemVo> getContestProblemList(Long cid, Date startTime, Date endTime, Date sealTime,
                                                 Boolean isAdmin, String contestAuthorUid);

    void syncContestRecord(Long pid, Long cid, String displayId);
}
