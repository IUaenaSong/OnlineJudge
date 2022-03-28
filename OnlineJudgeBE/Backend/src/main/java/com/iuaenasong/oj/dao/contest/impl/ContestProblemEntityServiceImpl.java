/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.contest.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.iuaenasong.oj.dao.contest.ContestEntityService;
import com.iuaenasong.oj.manager.group.member.GroupMemberManager;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import com.iuaenasong.oj.pojo.entity.contest.ContestProblem;
import com.iuaenasong.oj.mapper.ContestProblemMapper;
import com.iuaenasong.oj.pojo.entity.contest.ContestRecord;
import com.iuaenasong.oj.pojo.vo.ContestProblemVo;
import com.iuaenasong.oj.dao.contest.ContestProblemEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.dao.contest.ContestRecordEntityService;
import com.iuaenasong.oj.dao.user.UserInfoEntityService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContestProblemEntityServiceImpl extends ServiceImpl<ContestProblemMapper, ContestProblem> implements ContestProblemEntityService {

    @Autowired
    private ContestProblemMapper contestProblemMapper;

    @Autowired
    private UserInfoEntityService userInfoEntityService;

    @Autowired
    private ContestRecordEntityService contestRecordEntityService;

    @Autowired
    private GroupMemberManager groupMemberManager;

    @Autowired
    private ContestEntityService contestEntityService;

    @Override
    public List<ContestProblemVo> getContestProblemList(Long cid, Date startTime, Date endTime, Date sealTime, Boolean isAdmin, String contestAuthorUid) {
        // 筛去 比赛管理员和超级管理员的提交
        List<String> superAdminUidList = userInfoEntityService.getSuperAdminUidList();
        superAdminUidList.add(contestAuthorUid);

        Contest contest = contestEntityService.getById(cid);
        List<String> groupRootUidList = groupMemberManager.getGroupRootUidList(contest.getGid());
        superAdminUidList.addAll(groupRootUidList);

        return contestProblemMapper.getContestProblemList(cid, startTime, endTime, sealTime, isAdmin, superAdminUidList);
    }

    @Async
    @Override
    public void syncContestRecord(Long pid, Long cid, String displayId) {

        UpdateWrapper<ContestRecord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("pid", pid)
                .eq("cid", cid)
                .set("display_id", displayId);
        contestRecordEntityService.update(updateWrapper);
    }
}
