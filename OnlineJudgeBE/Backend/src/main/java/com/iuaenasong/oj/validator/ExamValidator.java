/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.validator;

import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.dao.exam.ExamRegisterEntityService;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.entity.exam.ExamRegister;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class ExamValidator {

    @Resource
    private ExamRegisterEntityService examRegisterEntityService;

    @Autowired
    private GroupValidator groupValidator;

    public boolean isRealScore(String uid, Exam exam, Boolean isRoot) {
        // 如果是管理员同时选择强制刷新榜单，则封榜无效
        Long gid = exam.getGid();
        if (isRoot || exam.getUid().equals(uid) || groupValidator.isGroupRoot(uid, gid)) {
            return true;
        } else if (!exam.getRealScore()) {
            Date now = new Date();
            // 如果现在时间处于封榜开始到比赛结束之间
            if (now.before(exam.getEndTime())) {
                return false;
            }
            // 或者没有开启赛后自动解除封榜，不可刷新榜单
            return exam.getAutoRealScore() && now.after(exam.getEndTime());
        }
        return true;
    }

    public void validateExamAuth(Exam exam, UserRolesVo userRolesVo, Boolean isRoot) throws StatusFailException, StatusForbiddenException {

        if (exam == null || !exam.getVisible()) {
            throw new StatusFailException("对不起，该考试不存在！");
        }

        Long gid = exam.getGid();
        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !isRoot && !exam.getUid().equals(userRolesVo.getUid())) { // 若不是比赛管理者

            // 判断一下比赛的状态，还未开始不能查看题目。
            if (exam.getStatus().intValue() != Constants.Exam.STATUS_RUNNING.getCode() &&
                    exam.getStatus().intValue() != Constants.Exam.STATUS_ENDED.getCode()) {
                throw new StatusForbiddenException("考试还未开始，您无权访问该比赛！");
            } else {
                if (!exam.getIsPublic() && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                    throw new StatusForbiddenException("对不起，您并非团队内的成员无法参加该团队内的考试！");
                }
                // 如果是处于比赛正在进行阶段，需要判断该场比赛是否为私有赛，私有赛需要判断该用户是否已注册
                if (exam.getAuth().intValue() == Constants.Exam.AUTH_PRIVATE.getCode()) {
                    QueryWrapper<ExamRegister> registerQueryWrapper = new QueryWrapper<>();
                    registerQueryWrapper.eq("eid", exam.getId()).eq("uid", userRolesVo.getUid());
                    ExamRegister register = examRegisterEntityService.getOne(registerQueryWrapper);
                    if (register == null) { // 如果数据为空，表示未注册私有赛，不可访问
                        throw new StatusForbiddenException("对不起，请先到考试首页输入考试密码进行注册！");
                    }

                    if (exam.getOpenAccountLimit()
                            && !validateAccountRule(exam.getAccountLimitRule(), userRolesVo.getUsername())) {
                        throw new StatusForbiddenException("对不起！本次考试只允许特定账号规则的用户参加！");
                    }
                }
            }
        }
    }

    public void validateJudgeAuth(Exam exam, String uid) throws StatusForbiddenException {

        if (exam.getAuth().intValue() == Constants.Exam.AUTH_PRIVATE.getCode() ||
                exam.getAuth().intValue() == Constants.Exam.AUTH_PROTECT.getCode()) {
            QueryWrapper<ExamRegister> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("eid", exam.getId()).eq("uid", uid);
            ExamRegister register = examRegisterEntityService.getOne(queryWrapper, false);
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