/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.dao.exam.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iuaenasong.oj.dao.exam.ExamEntityService;
import com.iuaenasong.oj.dao.exam.ExamProblemEntityService;
import com.iuaenasong.oj.dao.exam.ExamRecordEntityService;
import com.iuaenasong.oj.dao.group.GroupMemberEntityService;
import com.iuaenasong.oj.dao.user.UserInfoEntityService;
import com.iuaenasong.oj.mapper.ExamProblemMapper;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.entity.exam.ExamProblem;
import com.iuaenasong.oj.pojo.entity.exam.ExamRecord;
import com.iuaenasong.oj.pojo.vo.ExamProblemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
public class ExamProblemEntityServiceImpl extends ServiceImpl<ExamProblemMapper, ExamProblem> implements ExamProblemEntityService {

    @Autowired
    private ExamProblemMapper examProblemMapper;

    @Autowired
    private UserInfoEntityService userInfoEntityService;

    @Autowired
    private ExamRecordEntityService examRecordEntityService;

    @Autowired
    private GroupMemberEntityService groupMemberEntityService;

    @Autowired
    private ExamEntityService examEntityService;

    @Override
    public List<ExamProblemVo> getExamProblemList(Long eid, Date startTime, Date endTime, Boolean isAdmin, String examAuthorUid) {
        // 筛去 比赛管理员和超级管理员的提交
        List<String> superAdminUidList = userInfoEntityService.getSuperAdminUidList();
        superAdminUidList.add(examAuthorUid);

        Exam exam = examEntityService.getById(eid);
        List<String> groupRootUidList = groupMemberEntityService.getGroupRootUidList(exam.getGid());
        if (!CollectionUtils.isEmpty(groupRootUidList)) {
            superAdminUidList.addAll(groupRootUidList);
        }

        return examProblemMapper.getExamProblemList(eid, startTime, endTime, isAdmin, superAdminUidList);
    }

    @Async
    @Override
    public void syncExamRecord(Long pid, Long eid, String displayId, Integer score) {

        UpdateWrapper<ExamRecord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("pid", pid)
                .eq("eid", eid)
                .set("display_id", displayId)
                .set("score", score);
        examRecordEntityService.update(updateWrapper);
    }
}
