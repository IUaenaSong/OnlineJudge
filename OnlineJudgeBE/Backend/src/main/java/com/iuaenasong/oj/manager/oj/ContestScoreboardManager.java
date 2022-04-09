/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.oj;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iuaenasong.oj.validator.GroupValidator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.pojo.dto.ContestRankDto;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.entity.contest.ContestProblem;
import com.iuaenasong.oj.pojo.vo.*;
import com.iuaenasong.oj.dao.contest.ContestEntityService;
import com.iuaenasong.oj.dao.contest.ContestProblemEntityService;
import com.iuaenasong.oj.utils.Constants;
import com.iuaenasong.oj.validator.ContestValidator;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
public class ContestScoreboardManager {

    @Resource
    private ContestEntityService contestEntityService;

    @Resource
    private ContestProblemEntityService contestProblemEntityService;

    @Resource
    private ContestValidator contestValidator;

    @Resource
    private ContestRankManager contestRankManager;

    @Autowired
    private GroupValidator groupValidator;

    public ContestOutsideInfo getContestOutsideInfo(Long cid) throws StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        Contest contest = contestEntityService.getById(cid);

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        ContestVo contestInfo = contestEntityService.getContestInfoById(cid);

        if (contestInfo == null) {
            throw new StatusNotFoundException("访问错误：该比赛不存在！");
        }

        if (!contestInfo.getOpenRank()) {
            throw new StatusForbiddenException("本场比赛未开启外榜，禁止访问外榜！");
        }

        // 获取本场比赛的状态
        if (contestInfo.getStatus().equals(Constants.Contest.STATUS_SCHEDULED.getCode())) {
            throw new StatusForbiddenException("本场比赛正在筹备中，禁止访问外榜！");
        }

        contestInfo.setNow(new Date());
        ContestOutsideInfo contestOutsideInfo = new ContestOutsideInfo();
        contestOutsideInfo.setContest(contestInfo);

        List<ContestProblemVo> contestProblemList;

        boolean isAdmin = isRoot || contest.getUid().equals(userRolesVo.getUid()) || groupValidator.isGroupRoot(userRolesVo.getUid(), contest.getGid());
        // 如果比赛开启封榜
        if (contestValidator.isSealRank(userRolesVo.getUid(), contest, true, isRoot)) {
            contestProblemList = contestProblemEntityService.getContestProblemList(cid, contest.getStartTime(), contest.getEndTime(),
                    contest.getSealRankTime(), isAdmin, contest.getUid());
        } else {
            contestProblemList = contestProblemEntityService.getContestProblemList(cid, contest.getStartTime(), contest.getEndTime(),
                    null, isAdmin, contest.getUid());
        }
        contestOutsideInfo.setProblemList(contestProblemList);

        return contestOutsideInfo;
    }

    public List getContestOutsideScoreboard(ContestRankDto contestRankDto) throws StatusFailException, StatusForbiddenException {

        Long cid = contestRankDto.getCid();
        List<String> concernedList = contestRankDto.getConcernedList();
        Boolean removeStar = contestRankDto.getRemoveStar();
        Boolean forceRefresh = contestRankDto.getForceRefresh();

        if (cid == null) {
            throw new StatusFailException("错误：比赛id不能为空");
        }
        if (removeStar == null) {
            removeStar = false;
        }
        if (forceRefresh == null) {
            forceRefresh = false;
        }

        // 获取本场比赛的状态
        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusFailException("访问错误：该比赛不存在！");
        }

        if (!contest.getOpenRank()) {
            throw new StatusForbiddenException("本场比赛未开启外榜，禁止访问外榜！");
        }

        if (contest.getStatus().equals(Constants.Contest.STATUS_SCHEDULED.getCode())) {
            throw new StatusForbiddenException("本场比赛正在筹备中，禁止访问外榜！");
        }

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        // 超级管理员或者该比赛的创建者，则为比赛管理者
        boolean isRoot = false;
        String currentUid = null;

        if (userRolesVo != null) {
            currentUid = userRolesVo.getUid();
            isRoot = SecurityUtils.getSubject().hasRole("root");
            // 不是比赛创建者或者超管无权限开启强制实时榜单
            if (!isRoot && !contest.getUid().equals(currentUid)) {
                forceRefresh = false;
            }
        }

        // 校验该比赛是否开启了封榜模式，超级管理员和比赛创建者可以直接看到实际榜单
        boolean isOpenSealRank = contestValidator.isSealRank(currentUid, contest, forceRefresh, isRoot);

        if (contest.getType().intValue() == Constants.Contest.TYPE_ACM.getCode()) {

            // 获取ACM比赛排行榜外榜
            return contestRankManager.getACMContestScoreboard(isOpenSealRank,
                    removeStar,
                    contest,
                    null,
                    concernedList,
                    !forceRefresh,
                    15L); // 默认15s缓存

        } else {
            // 获取OI比赛排行榜外榜
            return contestRankManager.getOIContestScoreboard(isOpenSealRank,
                    removeStar,
                    contest,
                    null,
                    concernedList,
                    !forceRefresh,
                    15L); // 默认15s缓存
        }
    }
}