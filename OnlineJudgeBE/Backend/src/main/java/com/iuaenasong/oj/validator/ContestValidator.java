/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.validator;

import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.entity.contest.ContestRegister;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.dao.contest.ContestRegisterEntityService;
import com.iuaenasong.oj.utils.Constants;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class ContestValidator {

    @Resource
    private ContestRegisterEntityService contestRegisterEntityService;

    @Autowired
    private GroupValidator groupValidator;

    public boolean isSealRank(String uid, Contest contest, Boolean forceRefresh, Boolean isRoot) {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");
        // 如果是管理员同时选择强制刷新榜单，则封榜无效
        Long gid = contest.getGid();
        if (forceRefresh && (isRoot || contest.getUid().equals(uid) || groupValidator.isGroupRoot(userRolesVo.getUid(), gid))) {
            return false;
        } else if (contest.getSealRank() && contest.getSealRankTime() != null) { // 该比赛开启封榜模式
            Date now = new Date();
            // 如果现在时间处于封榜开始到比赛结束之间
            if (now.after(contest.getSealRankTime()) && now.before(contest.getEndTime())) {
                return true;
            }
            // 或者没有开启赛后自动解除封榜，不可刷新榜单
            return !contest.getAutoRealRank() && now.after(contest.getEndTime());
        }
        return false;
    }

    
    public void validateContestAuth(Contest contest, UserRolesVo userRolesVo, Boolean isRoot) throws StatusFailException, StatusForbiddenException {

        if (contest == null || !contest.getVisible()) {
            throw new StatusFailException("对不起，该比赛不存在！");
        }

        Long gid = contest.getGid();
        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !isRoot && !contest.getUid().equals(userRolesVo.getUid())) { // 若不是比赛管理者

            // 判断一下比赛的状态，还未开始不能查看题目。
            if (contest.getStatus().intValue() != Constants.Contest.STATUS_RUNNING.getCode() &&
                    contest.getStatus().intValue() != Constants.Contest.STATUS_ENDED.getCode()) {
                throw new StatusForbiddenException("比赛还未开始，您无权访问该比赛！");
            } else { // 如果是处于比赛正在进行阶段，需要判断该场比赛是否为私有赛，私有赛需要判断该用户是否已注册
                if (contest.getAuth().intValue() == Constants.Contest.AUTH_PRIVATE.getCode()) {
                    QueryWrapper<ContestRegister> registerQueryWrapper = new QueryWrapper<>();
                    registerQueryWrapper.eq("cid", contest.getId()).eq("uid", userRolesVo.getUid());
                    ContestRegister register = contestRegisterEntityService.getOne(registerQueryWrapper);
                    if (register == null) { // 如果数据为空，表示未注册私有赛，不可访问
                        throw new StatusForbiddenException("对不起，请先到比赛首页输入比赛密码进行注册！");
                    }

                    if (contest.getOpenAccountLimit()
                            && !validateAccountRule(contest.getAccountLimitRule(), userRolesVo.getUsername())) {
                        throw new StatusForbiddenException("对不起！本次比赛只允许特定账号规则的用户参赛！");
                    }
                }
            }
        }
    }

    public void validateJudgeAuth(Contest contest, String uid) throws StatusForbiddenException {

        if (contest.getAuth().intValue() == Constants.Contest.AUTH_PRIVATE.getCode() ||
                contest.getAuth().intValue() == Constants.Contest.AUTH_PROTECT.getCode()) {
            QueryWrapper<ContestRegister> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("cid", contest.getId()).eq("uid", uid);
            ContestRegister register = contestRegisterEntityService.getOne(queryWrapper, false);
            // 如果还没注册
            if (register == null) {
                throw new StatusForbiddenException("对不起，请你先注册该比赛，提交代码失败！");
            }
        }
    }

    public boolean validateAccountRule(String accountRule, String username) {

        String prefix = ReUtil.get("<prefix>([\\s\\S]*?)</prefix>",
                accountRule, 1);
        String suffix = ReUtil.get("<suffix>([\\s\\S]*?)</suffix>",
                accountRule, 1);
        String start = ReUtil.get("<start>([\\s\\S]*?)</start>",
                accountRule, 1);
        String end = ReUtil.get("<end>([\\s\\S]*?)</end>",
                accountRule, 1);
        String extra = ReUtil.get("<extra>([\\s\\S]*?)</extra>",
                accountRule, 1);

        int startNum = Integer.parseInt(start);
        int endNum = Integer.parseInt(end);

        for (int i = startNum; i <= endNum; i++) {
            if (username.equals(prefix + i + suffix)) {
                return true;
            }
        }
        // 额外账号列表
        if (!StringUtils.isEmpty(extra)) {
            String[] accountList = extra.trim().split(" ");
            for (String account : accountList) {
                if (username.equals(account)) {
                    return true;
                }
            }
        }

        return false;
    }
}