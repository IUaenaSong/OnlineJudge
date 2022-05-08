/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.contest.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import com.iuaenasong.oj.pojo.vo.ContestRegisterCountVo;
import com.iuaenasong.oj.pojo.vo.ContestVo;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.mapper.ContestMapper;
import com.iuaenasong.oj.dao.contest.ContestEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContestEntityServiceImpl extends ServiceImpl<ContestMapper, Contest> implements ContestEntityService {

    @Autowired
    private ContestMapper contestMapper;

    @Override
    public List<ContestVo> getWithinNext14DaysContests() {
        List<ContestVo> contestList = contestMapper.getWithinNext14DaysContests();
        setRegisterCount(contestList);

        return contestList;
    }

    @Override
    public IPage<ContestVo> getContestList(Integer limit, Integer currentPage, Integer type, Integer status, String keyword) {
        //新建分页
        IPage<ContestVo> page = new Page<>(currentPage, limit);

        List<ContestVo> contestList = contestMapper.getContestList(page, type, status, keyword);
        setRegisterCount(contestList);

        return page.setRecords(contestList);
    }

    @Override
    public ContestVo getContestInfoById(long cid) {
        List<Long> cidList = Collections.singletonList(cid);
        ContestVo contestVo = contestMapper.getContestInfoById(cid);
        if (contestVo != null) {
            List<ContestRegisterCountVo> contestRegisterCountVoList = contestMapper.getContestRegisterCount(cidList);
            if(!CollectionUtils.isEmpty(contestRegisterCountVoList)) {
                ContestRegisterCountVo contestRegisterCountVo = contestRegisterCountVoList.get(0);
                contestVo.setCount(contestRegisterCountVo.getCount());
            }
        }
        return contestVo;
    }

    @Override
    public Void setRegisterCount(List<ContestVo> contestList) {
        List<Long> cidList = contestList.stream().map(ContestVo::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(cidList)) {
            List<ContestRegisterCountVo> contestRegisterCountVoList = contestMapper.getContestRegisterCount(cidList);
            for (ContestRegisterCountVo contestRegisterCountVo : contestRegisterCountVoList) {
                for (ContestVo contestVo : contestList) {
                    if (contestRegisterCountVo.getCid().equals(contestVo.getId())) {
                        contestVo.setCount(contestRegisterCountVo.getCount());
                        break;
                    }
                }
            }
        }
        return null;
    }

}
