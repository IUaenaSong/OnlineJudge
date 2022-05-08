/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.dao.exam.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iuaenasong.oj.dao.exam.ExamEntityService;
import com.iuaenasong.oj.dao.exam.ExamQuestionEntityService;
import com.iuaenasong.oj.dao.exam.ExamQuestionRecordEntityService;
import com.iuaenasong.oj.dao.group.GroupMemberEntityService;
import com.iuaenasong.oj.dao.user.UserInfoEntityService;
import com.iuaenasong.oj.mapper.ExamQuestionMapper;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.entity.exam.ExamQuestion;
import com.iuaenasong.oj.pojo.entity.exam.ExamQuestionRecord;
import com.iuaenasong.oj.pojo.vo.ExamQuestionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
public class ExamQuestionEntityServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion> implements ExamQuestionEntityService {

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Autowired
    private UserInfoEntityService userInfoEntityService;

    @Autowired
    private ExamQuestionRecordEntityService examQuesitonRecordEntityService;

    @Autowired
    private GroupMemberEntityService groupMemberEntityService;

    @Autowired
    private ExamEntityService examEntityService;

    @Override
    public List<ExamQuestionVo> getExamQuestionList(Integer type, Long eid, Date startTime, Date endTime, Boolean isAdmin, String examAuthorUid) {
        if (type == null) type = 0;
        // 筛去 比赛管理员和超级管理员的提交
        List<String> superAdminUidList = userInfoEntityService.getSuperAdminUidList();
        superAdminUidList.add(examAuthorUid);

        Exam exam = examEntityService.getById(eid);
        List<String> groupRootUidList = groupMemberEntityService.getGroupRootUidList(exam.getGid());
        if (!CollectionUtils.isEmpty(groupRootUidList)) {
            superAdminUidList.addAll(groupRootUidList);
        }

        return examQuestionMapper.getExamQuestionList(type, eid, startTime, endTime, isAdmin, superAdminUidList);
    }

    @Async
    @Override
    public void syncExamQuestionRecord(Long qid, Long eid, String displayId) {

        UpdateWrapper<ExamQuestionRecord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("qid", qid)
                .eq("eid", eid)
                .set("display_id", displayId);
        examQuesitonRecordEntityService.update(updateWrapper);
    }
}
