/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.dao.group.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iuaenasong.oj.dao.group.GroupExamEntityService;
import com.iuaenasong.oj.mapper.GroupExamMapper;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.vo.ExamVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupExamEntityServiceImpl extends ServiceImpl<GroupExamMapper, Exam> implements GroupExamEntityService {

    @Autowired
    private GroupExamMapper groupExamMapper;

    @Override
    public IPage<ExamVo> getExamList(int limit, int currentPage, Long gid) {
        IPage<ExamVo> iPage = new Page<>(currentPage, limit);

        List<ExamVo> examList = groupExamMapper.getExamList(iPage, gid);

        return iPage.setRecords(examList);
    }

    @Override
    public IPage<Exam> getAdminExamList(int limit, int currentPage, Long gid) {
        IPage<Exam> iPage = new Page<>(currentPage, limit);

        List<Exam> examList = groupExamMapper.getAdminExamList(iPage, gid);

        return iPage.setRecords(examList);
    }
}
