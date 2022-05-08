/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.group.impl;

import com.iuaenasong.oj.dao.contest.ContestEntityService;
import com.iuaenasong.oj.dao.group.GroupContestEntityService;
import com.iuaenasong.oj.mapper.GroupContestMapper;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.vo.ContestVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupContestEntityServiceImpl extends ServiceImpl<GroupContestMapper, Contest> implements GroupContestEntityService {

    @Autowired
    private GroupContestMapper groupContestMapper;

    @Autowired
    private ContestEntityService contestEntityService;

    @Override
    public IPage<ContestVo> getContestList(int limit, int currentPage, Long gid) {
        IPage<ContestVo> iPage = new Page<>(currentPage, limit);

        List<ContestVo> contestList = groupContestMapper.getContestList(iPage, gid);

        contestEntityService.setRegisterCount(contestList);

        return iPage.setRecords(contestList);
    }

    @Override
    public IPage<Contest> getAdminContestList(int limit, int currentPage, Long gid) {
        IPage<Contest> iPage = new Page<>(currentPage, limit);

        List<Contest> contestList = groupContestMapper.getAdminContestList(iPage, gid);

        return iPage.setRecords(contestList);
    }
}
