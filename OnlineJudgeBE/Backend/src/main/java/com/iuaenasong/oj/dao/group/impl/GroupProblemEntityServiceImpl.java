/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.group.impl;

import com.iuaenasong.oj.dao.group.GroupProblemEntityService;
import com.iuaenasong.oj.mapper.GroupProblemMapper;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.vo.ProblemVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupProblemEntityServiceImpl extends ServiceImpl<GroupProblemMapper, Problem> implements GroupProblemEntityService {

    @Autowired
    private GroupProblemMapper groupProblemMapper;

    @Override
    public IPage<ProblemVo> getProblemList(int limit, int currentPage, Long gid) {
        IPage<ProblemVo> iPage = new Page<>(currentPage, limit);

        List<ProblemVo> problemList = groupProblemMapper.getProblemList(iPage, gid);

        return iPage.setRecords(problemList);
    }

    @Override
    public IPage<Problem> getAdminProblemList(int limit, int currentPage, Long gid) {
        IPage<Problem> iPage = new Page<>(currentPage, limit);

        List<Problem> problemList = groupProblemMapper.getAdminProblemList(iPage, gid);

        return iPage.setRecords(problemList);
    }

}
