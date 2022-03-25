/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.entity.contest.ContestRecord;

public interface ContestRecordEntityService extends IService<ContestRecord> {
    void UpdateContestRecord(String uid, Integer score, Integer status, Long submitId, Long cid, Integer useTime);
}
