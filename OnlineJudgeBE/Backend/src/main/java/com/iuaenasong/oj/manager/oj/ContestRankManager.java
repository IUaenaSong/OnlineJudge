/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.oj;

import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.validator.GroupValidator;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.vo.ACMContestRankVo;
import com.iuaenasong.oj.pojo.vo.OIContestRankVo;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class ContestRankManager {

    @Resource
    private ContestCalculateRankManager contestCalculateRankManager;

    @Autowired
    private GroupValidator groupValidator;

    
    public IPage<ACMContestRankVo> getContestACMRankPage(Boolean isOpenSealRank,
                                                         Boolean removeStar,
                                                         String currentUserId,
                                                         List<String> concernedList,
                                                         Contest contest,
                                                         int currentPage,
                                                         int limit) {
        // 进行排序计算
        List<ACMContestRankVo> orderResultList = contestCalculateRankManager.calcACMRank(isOpenSealRank,
                removeStar,
                contest,
                currentUserId,
                concernedList);

        // 计算好排行榜，然后进行分页
        Page<ACMContestRankVo> page = new Page<>(currentPage, limit);
        int count = orderResultList.size();
        List<ACMContestRankVo> pageList = new ArrayList<>();
        //计算当前页第一条数据的下标
        int currId = currentPage > 1 ? (currentPage - 1) * limit : 0;
        for (int i = 0; i < limit && i < count - currId; i++) {
            pageList.add(orderResultList.get(currId + i));
        }
        page.setSize(limit);
        page.setCurrent(currentPage);
        page.setTotal(count);
        page.setRecords(pageList);

        return page;
    }

    public IPage<OIContestRankVo> getContestOIRankPage(Boolean isOpenSealRank,
                                                       Boolean removeStar,
                                                       String currentUserId,
                                                       List<String> concernedList,
                                                       Contest contest,
                                                       int currentPage,
                                                       int limit) {

        List<OIContestRankVo> orderResultList = contestCalculateRankManager.calcOIRank(isOpenSealRank,
                removeStar,
                contest,
                currentUserId,
                concernedList);

        // 计算好排行榜，然后进行分页
        Page<OIContestRankVo> page = new Page<>(currentPage, limit);
        int count = orderResultList.size();
        List<OIContestRankVo> pageList = new ArrayList<>();
        //计算当前页第一条数据的下标
        int currId = currentPage > 1 ? (currentPage - 1) * limit : 0;
        for (int i = 0; i < limit && i < count - currId; i++) {
            pageList.add(orderResultList.get(currId + i));
        }
        page.setSize(limit);
        page.setCurrent(currentPage);
        page.setTotal(count);
        page.setRecords(pageList);
        return page;
    }

    
    public List<ACMContestRankVo> getACMContestScoreboard(Boolean isOpenSealRank,
                                                          Boolean removeStar,
                                                          Contest contest,
                                                          String currentUserId,
                                                          List<String> concernedList,
                                                          Boolean useCache,
                                                          Long cacheTime) {

        return contestCalculateRankManager.calcACMRank(isOpenSealRank,
                removeStar,
                contest,
                currentUserId,
                concernedList,
                useCache,
                cacheTime);
    }

    
    public List<OIContestRankVo> getOIContestScoreboard(Boolean isOpenSealRank,
                                                         Boolean removeStar,
                                                         Contest contest,
                                                         String currentUserId,
                                                         List<String> concernedList,
                                                         Boolean useCache,
                                                         Long cacheTime) {

        return contestCalculateRankManager.calcOIRank(isOpenSealRank,
                removeStar,
                contest,
                currentUserId,
                concernedList,
                useCache,
                cacheTime);
    }

}