/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.contest.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iuaenasong.oj.dao.contest.ContestEntityService;
import com.iuaenasong.oj.dao.group.GroupMemberEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.entity.contest.ContestRecord;
import com.iuaenasong.oj.mapper.ContestRecordMapper;
import com.iuaenasong.oj.pojo.vo.ContestRecordVo;
import com.iuaenasong.oj.dao.contest.ContestRecordEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.dao.user.UserInfoEntityService;
import com.iuaenasong.oj.utils.Constants;
import com.iuaenasong.oj.utils.RedisUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class ContestRecordEntityServiceImpl extends ServiceImpl<ContestRecordMapper, ContestRecord> implements ContestRecordEntityService {

    @Autowired
    private ContestRecordMapper contestRecordMapper;

    @Autowired
    private UserInfoEntityService userInfoEntityService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ContestEntityService contestEntityService;

    @Autowired
    private GroupMemberEntityService groupMemberEntityService;

    @Override
    public IPage<ContestRecord> getACInfo(Integer currentPage, Integer limit, Integer status, Long cid, String contestCreatorId) {

        List<ContestRecord> acInfo = contestRecordMapper.getACInfo(status, cid);

        HashMap<Long, String> pidMapUidAndPid = new HashMap<>(12);
        HashMap<String, Long> UidAndPidMapTime = new HashMap<>(12);

        List<String> superAdminUidList = userInfoEntityService.getSuperAdminUidList();
        superAdminUidList.add(contestCreatorId);

        Contest contest = contestEntityService.getById(cid);
        List<String> groupRootUidList = groupMemberEntityService.getGroupRootUidList(contest.getGid());
        if (!CollectionUtils.isEmpty(groupRootUidList)) {
            superAdminUidList.addAll(groupRootUidList);
        }

        List<ContestRecord> userACInfo = new LinkedList<>();

        for (ContestRecord contestRecord : acInfo) {

            if (superAdminUidList.contains(contestRecord.getUid())) { // ????????????????????????????????????????????????
                continue;
            }

            contestRecord.setFirstBlood(false);
            String uidAndPid = pidMapUidAndPid.get(contestRecord.getPid());
            if (uidAndPid == null) {
                pidMapUidAndPid.put(contestRecord.getPid(), contestRecord.getUid() + contestRecord.getPid());
                UidAndPidMapTime.put(contestRecord.getUid() + contestRecord.getPid(), contestRecord.getTime());
            } else {
                Long firstTime = UidAndPidMapTime.get(uidAndPid);
                Long tmpTime = contestRecord.getTime();
                if (tmpTime < firstTime) {
                    pidMapUidAndPid.put(contestRecord.getPid(), contestRecord.getUid() + contestRecord.getPid());
                    UidAndPidMapTime.put(contestRecord.getUid() + contestRecord.getPid(), tmpTime);
                }
            }
            userACInfo.add(contestRecord);
        }

        List<ContestRecord> pageList = new ArrayList<>();

        int count = userACInfo.size();

        //???????????????????????????????????????
        int currId = currentPage > 1 ? (currentPage - 1) * limit : 0;
        for (int i = 0; i < limit && i < count - currId; i++) {
            ContestRecord contestRecord = userACInfo.get(currId + i);
            if (pidMapUidAndPid.get(contestRecord.getPid()).equals(contestRecord.getUid() + contestRecord.getPid())) {
                contestRecord.setFirstBlood(true);
            }
            pageList.add(contestRecord);
        }

        Page<ContestRecord> page = new Page<>(currentPage, limit);
        page.setSize(limit);
        page.setCurrent(currentPage);
        page.setTotal(count);
        page.setRecords(pageList);

        return page;
    }

    @Override
    public List<ContestRecordVo> getOIContestRecord(Contest contest, Boolean isOpenSealRank) {

        String oiRankScoreType = contest.getOiRankScoreType();
        Long cid = contest.getId();
        String contestAuthorUid = contest.getUid();
        Date sealTime = contest.getSealRankTime();
        Date startTime = contest.getStartTime();
        Date endTime = contest.getEndTime();

        if (!isOpenSealRank) {
            // ???????????? ??????????????????
            // ?????????????????????????????????????????????
            if (Objects.equals(Constants.Contest.OI_RANK_RECENT_SCORE.getName(), oiRankScoreType)) {
                return contestRecordMapper.getOIContestRecordByRecentSubmission(cid,
                        contestAuthorUid,
                        false,
                        sealTime,
                        startTime,
                        endTime);
            } else {
                return contestRecordMapper.getOIContestRecordByHighestSubmission(cid,
                        contestAuthorUid,
                        false,
                        sealTime,
                        startTime,
                        endTime);
            }

        } else {
            String key = Constants.Contest.OI_CONTEST_RANK_CACHE.getName() + "_" + oiRankScoreType + "_" + cid;
            List<ContestRecordVo> oiContestRecordList = (List<ContestRecordVo>) redisUtils.get(key);
            if (oiContestRecordList == null) {
                if (Objects.equals(Constants.Contest.OI_RANK_RECENT_SCORE.getName(), oiRankScoreType)) {
                    oiContestRecordList = contestRecordMapper.getOIContestRecordByRecentSubmission(cid,
                            contestAuthorUid,
                            true,
                            sealTime,
                            startTime,
                            endTime);
                } else {
                    oiContestRecordList = contestRecordMapper.getOIContestRecordByHighestSubmission(cid,
                            contestAuthorUid,
                            true,
                            sealTime,
                            startTime,
                            endTime);
                }
                redisUtils.set(key, oiContestRecordList, 2 * 3600);
            }
            return oiContestRecordList;
        }

    }

    @Override
    public List<ContestRecordVo> getACMContestRecord(String username, Long cid) {
        return contestRecordMapper.getACMContestRecord(username, cid);
    }

}
