/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.judge.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iuaenasong.oj.dao.exam.ExamRecordEntityService;
import com.iuaenasong.oj.mapper.ProblemMapper;
import com.iuaenasong.oj.pojo.entity.exam.ExamRecord;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.pojo.entity.contest.ContestRecord;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.mapper.JudgeMapper;
import com.iuaenasong.oj.pojo.vo.JudgeVo;
import com.iuaenasong.oj.pojo.vo.ProblemCountVo;
import com.iuaenasong.oj.dao.contest.ContestRecordEntityService;
import com.iuaenasong.oj.dao.judge.JudgeEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iuaenasong.oj.utils.Constants;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "oj")
public class JudgeEntityServiceImpl extends ServiceImpl<JudgeMapper, Judge> implements JudgeEntityService {

    @Autowired
    private JudgeMapper judgeMapper;

    @Autowired
    private ContestRecordEntityService contestRecordEntityService;

    @Autowired
    private ExamRecordEntityService examRecordEntityService;

    @Autowired
    private ProblemMapper problemMapper;

    @Override
    public IPage<JudgeVo> getCommonJudgeList(Integer limit,
                                             Integer currentPage,
                                             String searchPid,
                                             Integer status,
                                             String username,
                                             String uid,
                                             Long gid,
                                             Boolean completeProblemID) {
        //新建分页
        Page<JudgeVo> page = new Page<>(currentPage, limit);

        IPage<JudgeVo> commonJudgeList = judgeMapper.getCommonJudgeList(page, searchPid, status, username, uid, gid, completeProblemID);
        List<JudgeVo> records = commonJudgeList.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            List<Long> pidList = records.stream().map(JudgeVo::getPid).collect(Collectors.toList());
            QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
            problemQueryWrapper.select("id", "title")
                    .in("id", pidList);
            List<Problem> problemList = problemMapper.selectList(problemQueryWrapper);
            HashMap<Long, String> storeMap = new HashMap<>(limit);
            for (JudgeVo judgeVo : records) {
                judgeVo.setTitle(getProblemTitleByPid(judgeVo.getPid(), problemList, storeMap));
            }
        }
        return commonJudgeList;
    }

    private String getProblemTitleByPid(Long pid, List<Problem> problemList, HashMap<Long, String> storeMap) {
        String title = storeMap.get(pid);
        if (title != null) {
            return title;
        }
        for (Problem problem : problemList) {
            if (problem.getId().equals(pid)) {
                storeMap.put(pid, problem.getTitle());
                return problem.getTitle();
            }
        }
        return "";
    }

    @Override
    public IPage<JudgeVo> getContestJudgeList(Integer limit, Integer currentPage, String displayId, Long cid, Integer status,
                                              String username, String uid, Boolean isAdmin, String rule,
                                              Date startTime, Date sealRankTime, String sealTimeUid, Boolean completeProblemID) {
        //新建分页
        Page<JudgeVo> page = new Page<>(currentPage, limit);

        return judgeMapper.getContestJudgeList(page, displayId, cid, status, username, uid, isAdmin,
                rule, startTime, sealRankTime, sealTimeUid, completeProblemID);
    }

    @Override
    public IPage<JudgeVo> getExamJudgeList(Integer limit, Integer currentPage, String displayId, Long eid, Integer status,
                                              String username, String uid, Boolean isAdmin,
                                              Date startTime, Date endTime, Boolean completeProblemID) {
        //新建分页
        Page<JudgeVo> page = new Page<>(currentPage, limit);

        return judgeMapper.getExamJudgeList(page, displayId, eid, status, username, uid, isAdmin,
                startTime, endTime, completeProblemID);
    }

    @Override
    public void failToUseRedisPublishJudge(Long submitId, Long pid, Boolean isContest, Boolean isExam) {
        UpdateWrapper<Judge> judgeUpdateWrapper = new UpdateWrapper<>();
        judgeUpdateWrapper.eq("submit_id", submitId)
                .set("error_message", "The something has gone wrong with the data Backup server. Please report this to administrator.")
                .set("status", Constants.Judge.STATUS_SYSTEM_ERROR.getStatus());
        judgeMapper.update(null, judgeUpdateWrapper);
        // 更新contest_record表
        if (isContest) {
            UpdateWrapper<ContestRecord> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("submit_id", submitId) // submit_id一定只有一个
                    .set("first_blood", false)
                    .set("status", Constants.Contest.RECORD_NOT_AC_NOT_PENALTY.getCode());
            contestRecordEntityService.update(updateWrapper);
        }
        if (isExam) {
            UpdateWrapper<ExamRecord> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("submit_id", submitId) // submit_id一定只有一个
                    .set("status", Constants.Exam.RECORD_NOT_AC_NOT_PENALTY.getCode());
            examRecordEntityService.update(updateWrapper);
        }
    }

    @Override
    public ProblemCountVo getContestProblemCount(Long pid, Long cpid, Long cid, Date startTime, Date sealRankTime, List<String> adminList) {
        return judgeMapper.getContestProblemCount(pid, cpid, cid, startTime, sealRankTime, adminList);
    }

    @Override
    public ProblemCountVo getExamProblemCount(Long pid, Long epid, Long eid, Date startTime, Date endTime, Boolean isAdmin, List<String> adminList) {
        return judgeMapper.getExamProblemCount(pid, epid, eid, startTime, endTime, isAdmin, adminList);
    }

    @Override
    public ProblemCountVo getProblemCount(Long pid) {
        return judgeMapper.getProblemCount(pid);
    }

    @Override
    public int getTodayJudgeNum(){
        return judgeMapper.getTodayJudgeNum();
    }

    @Override
    public List<ProblemCountVo> getProblemListCount(List<Long> pidList){
        return judgeMapper.getProblemListCount(pidList);
    }

}
