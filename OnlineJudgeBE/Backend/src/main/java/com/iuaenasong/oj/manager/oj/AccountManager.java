/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.oj;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.crypto.SecureUtil;
import com.iuaenasong.oj.pojo.dto.ChangeMobileDto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusSystemErrorException;
import com.iuaenasong.oj.pojo.dto.ChangeEmailDto;
import com.iuaenasong.oj.pojo.dto.ChangePasswordDto;
import com.iuaenasong.oj.pojo.dto.CheckUsernameOrEmailOrMobileDto;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.entity.user.*;
import com.iuaenasong.oj.pojo.vo.*;
import com.iuaenasong.oj.dao.problem.ProblemEntityService;
import com.iuaenasong.oj.dao.user.*;
import com.iuaenasong.oj.utils.Constants;
import com.iuaenasong.oj.utils.RedisUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AccountManager {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserInfoEntityService userInfoEntityService;

    @Autowired
    private UserRoleEntityService userRoleEntityService;

    @Autowired
    private UserRecordEntityService userRecordEntityService;

    @Autowired
    private UserAcproblemEntityService userAcproblemEntityService;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private SessionEntityService sessionEntityService;

    
    public CheckUsernameOrEmailOrMobileVo checkUsernameOrEmailOrMobile(CheckUsernameOrEmailOrMobileDto checkUsernameOrEmailOrMobileDto) {

        String email = checkUsernameOrEmailOrMobileDto.getEmail();

        String mobile = checkUsernameOrEmailOrMobileDto.getMobile();

        String username = checkUsernameOrEmailOrMobileDto.getUsername();

        boolean rightEmail = false;

        boolean rightMobile = false;

        boolean rightUsername = false;

        if (!StringUtils.isEmpty(email)) {
            email = email.trim();
            boolean isEmail = Validator.isEmail(email);
            if (!isEmail) {
                rightEmail = false;
            } else {
                QueryWrapper<UserInfo> wrapper = new QueryWrapper<UserInfo>().eq("email", email);
                UserInfo user = userInfoEntityService.getOne(wrapper, false);
                if (user != null) {
                    rightEmail = true;
                } else {
                    rightEmail = false;
                }
            }
        }

        if (!StringUtils.isEmpty(mobile)) {
            mobile = mobile.trim();
            boolean isMobile = Validator.isMobile(mobile);
            if (!isMobile) {
                rightMobile = false;
            } else {
                QueryWrapper<UserInfo> wrapper = new QueryWrapper<UserInfo>().eq("mobile", mobile);
                UserInfo user = userInfoEntityService.getOne(wrapper, false);
                if (user != null) {
                    rightMobile = true;
                } else {
                    rightMobile = false;
                }
            }
        }

        if (!StringUtils.isEmpty(username)) {
            username = username.trim();
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<UserInfo>().eq("username", username);
            UserInfo user = userInfoEntityService.getOne(wrapper, false);
            if (user != null) {
                rightUsername = true;
            } else {
                rightUsername = false;
            }
        }

        CheckUsernameOrEmailOrMobileVo checkUsernameOrEmailOrMobileVo = new CheckUsernameOrEmailOrMobileVo();
        checkUsernameOrEmailOrMobileVo.setEmail(rightEmail);
        checkUsernameOrEmailOrMobileVo.setMobile(rightMobile);
        checkUsernameOrEmailOrMobileVo.setUsername(rightUsername);
        return checkUsernameOrEmailOrMobileVo;
    }

    
    public UserHomeVo getUserHomeInfo(String uid, String username) throws StatusFailException {

        org.apache.shiro.session.Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");
        // 如果没有uid和username，默认查询当前登录用户的
        if (StringUtils.isEmpty(uid) && StringUtils.isEmpty(username)) {
            if (userRolesVo != null) {
                uid = userRolesVo.getUid();
            } else {
                throw new StatusFailException("错误：uid和username不能都为空！");
            }
        }

        UserHomeVo userHomeInfo = userRecordEntityService.getUserHomeInfo(uid, username);
        if (userHomeInfo == null) {
            throw new StatusFailException("用户不存在");
        }
        QueryWrapper<UserAcproblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", userHomeInfo.getUid()).select("distinct pid");
        List<Long> pidList = new LinkedList<>();
        List<UserAcproblem> acProblemList = userAcproblemEntityService.list(queryWrapper);
        acProblemList.forEach(acProblem -> {
            pidList.add(acProblem.getPid());
        });

        List<String> disPlayIdList = new LinkedList<>();

        if (pidList.size() > 0) {
            QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
            problemQueryWrapper.in("id", pidList);
            List<Problem> problems = problemEntityService.list(problemQueryWrapper);
            problems.forEach(problem -> {
                disPlayIdList.add(problem.getProblemId());
            });
        }

        userHomeInfo.setSolvedList(disPlayIdList);
        QueryWrapper<Session> sessionQueryWrapper = new QueryWrapper<>();
        sessionQueryWrapper.eq("uid", userHomeInfo.getUid())
                .orderByDesc("gmt_create")
                .last("limit 1");

        Session recentSession = sessionEntityService.getOne(sessionQueryWrapper, false);
        if (recentSession != null) {
            userHomeInfo.setRecentLoginTime(recentSession.getGmtCreate());
        }
        return userHomeInfo;
    }

    
    public ChangeAccountVo changePassword(ChangePasswordDto changePasswordDto) throws StatusSystemErrorException, StatusFailException {
        String oldPassword = changePasswordDto.getOldPassword();
        String newPassword = changePasswordDto.getNewPassword();

        // 数据可用性判断
        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword)) {
            throw new StatusFailException("错误：原始密码或新密码不能为空！");
        }
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            throw new StatusFailException("新密码长度应该为6~20位！");
        }
        // 获取当前登录的用户
        org.apache.shiro.session.Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        // 如果已经被锁定半小时不能修改
        String lockKey = Constants.Account.CODE_CHANGE_PASSWORD_LOCK + userRolesVo.getUid();
        // 统计失败的key
        String countKey = Constants.Account.CODE_CHANGE_PASSWORD_FAIL + userRolesVo.getUid();

        ChangeAccountVo resp = new ChangeAccountVo();
        if (redisUtils.hasKey(lockKey)) {
            long expire = redisUtils.getExpire(lockKey);
            Date now = new Date();
            long minute = expire / 60;
            long second = expire % 60;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            resp.setCode(403);
            Date afterDate = new Date(now.getTime() + expire * 1000);
            String msg = "由于您多次修改密码失败，修改密码功能已锁定，请在" + minute + "分" + second + "秒后(" + formatter.format(afterDate) + ")再进行尝试！";
            resp.setMsg(msg);
            return resp;
        }
        // 与当前登录用户的密码进行比较判断
        if (userRolesVo.getPassword().equals(SecureUtil.md5(oldPassword))) { // 如果相同，则进行修改密码操作
            UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("password", SecureUtil.md5(newPassword))// 数据库用户密码全部用md5加密
                    .eq("uuid", userRolesVo.getUid());
            boolean isOk = userInfoEntityService.update(updateWrapper);
            if (isOk) {
                resp.setCode(200);
                resp.setMsg("修改密码成功！您将于5秒钟后退出进行重新登录操作！");
                // 清空记录
                redisUtils.del(countKey);
                // 更新session
                userRolesVo.setPassword(SecureUtil.md5(newPassword));
                session.setAttribute("userInfo", userRolesVo);
                return resp;
            } else {
                throw new StatusSystemErrorException("系统错误：修改密码失败！");
            }
        } else { // 如果不同，则进行记录，当失败次数达到5次，半个小时后才可重试
            Integer count = (Integer) redisUtils.get(countKey);
            if (count == null) {
                redisUtils.set(countKey, 1, 60 * 30); // 三十分钟不尝试，该限制会自动清空消失
                count = 0;
            } else if (count < 5) {
                redisUtils.incr(countKey, 1);
            }
            count++;
            if (count == 5) {
                redisUtils.del(countKey); // 清空统计
                redisUtils.set(lockKey, "lock", 60 * 30); // 设置锁定更改
            }
            resp.setCode(400);
            resp.setMsg("原始密码错误！您已累计修改密码失败" + count + "次...");
            return resp;
        }
    }

    public ChangeAccountVo changeEmail(ChangeEmailDto changeEmailDto) throws StatusSystemErrorException, StatusFailException {

        String password = changeEmailDto.getPassword();
        String newEmail = changeEmailDto.getNewEmail();
        String code = changeEmailDto.getCode();
        // 数据可用性判断
        String codeKey = Constants.Email.EMAIL_KEY_PREFIX.getValue() + newEmail;
        if (!redisUtils.hasKey(codeKey)) {
            throw new StatusFailException("验证码不存在或已过期");
        }
        if (!redisUtils.get(codeKey).equals(code)) {
            throw new StatusFailException("验证码不正确");
        }
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(newEmail)) {
            throw new StatusFailException("密码或新邮箱不能为空！");
        }
        if (!Validator.isEmail(newEmail)) {
            throw new StatusFailException("邮箱格式错误！");
        }
        // 获取当前登录的用户
        org.apache.shiro.session.Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        // 如果已经被锁定半小时不能修改
        String lockKey = Constants.Account.CODE_CHANGE_EMAIL_LOCK + userRolesVo.getUid();
        // 统计失败的key
        String countKey = Constants.Account.CODE_CHANGE_EMAIL_FAIL + userRolesVo.getUid();

        ChangeAccountVo resp = new ChangeAccountVo();
        if (redisUtils.hasKey(lockKey)) {
            long expire = redisUtils.getExpire(lockKey);
            Date now = new Date();
            long minute = expire / 60;
            long second = expire % 60;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            resp.setCode(403);
            Date afterDate = new Date(now.getTime() + expire * 1000);
            String msg = "由于您多次修改邮箱失败，修改邮箱功能已锁定，请在" + minute + "分" + second + "秒后(" + formatter.format(afterDate) + ")再进行尝试！";
            resp.setMsg(msg);
            return resp;
        }
        // 与当前登录用户的密码进行比较判断
        if (userRolesVo.getPassword().equals(SecureUtil.md5(password))) { // 如果相同，则进行修改操作
            UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("email", newEmail)
                    .eq("uuid", userRolesVo.getUid());

            boolean isOk = userInfoEntityService.update(updateWrapper);
            if (isOk) {

                UserInfoVo userInfoVo = new UserInfoVo();
                BeanUtil.copyProperties(userRolesVo, userInfoVo, "roles");
                userInfoVo.setRoleList(userRolesVo.getRoles().stream().map(Role::getRole).collect(Collectors.toList()));

                resp.setCode(200);
                resp.setMsg("修改邮箱成功！");
                resp.setUserInfo(userInfoVo);
                // 清空记录
                redisUtils.del(countKey);
                // 更新session
                userRolesVo.setEmail(newEmail);
                session.setAttribute("userInfo", userRolesVo);
                return resp;
            } else {
                throw new StatusSystemErrorException("系统错误：修改邮箱失败！");
            }
        } else { // 如果不同，则进行记录，当失败次数达到5次，半个小时后才可重试
            Integer count = (Integer) redisUtils.get(countKey);
            if (count == null) {
                redisUtils.set(countKey, 1, 60 * 30); // 三十分钟不尝试，该限制会自动清空消失
                count = 0;
            } else if (count < 5) {
                redisUtils.incr(countKey, 1);
            }
            count++;
            if (count == 5) {
                redisUtils.del(countKey); // 清空统计
                redisUtils.set(lockKey, "lock", 60 * 30); // 设置锁定更改
            }

            resp.setCode(400);
            resp.setMsg("密码错误！您已累计修改邮箱失败" + count + "次...");
            return resp;
        }
    }

    public ChangeAccountVo changeMobile(ChangeMobileDto changeMobileDto) throws StatusSystemErrorException, StatusFailException {

        String password = changeMobileDto.getPassword();
        String newMobile = changeMobileDto.getNewMobile();
        String code = changeMobileDto.getCode();
        // 数据可用性判断
        String codeKey = Constants.Mobile.MOBILE_KEY_PREFIX.getValue() + newMobile;
        if (!redisUtils.hasKey(codeKey)) {
            throw new StatusFailException("验证码不存在或已过期");
        }
        if (!redisUtils.get(codeKey).equals(code)) {
            throw new StatusFailException("验证码不正确");
        }
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(newMobile)) {
            throw new StatusFailException("密码或新手机号不能为空！");
        }
        if (!Validator.isMobile(newMobile)) {
            throw new StatusFailException("手机号格式错误！");
        }
        // 获取当前登录的用户
        org.apache.shiro.session.Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        // 如果已经被锁定半小时不能修改
        String lockKey = Constants.Account.CODE_CHANGE_MOBILE_LOCK + userRolesVo.getUid();
        // 统计失败的key
        String countKey = Constants.Account.CODE_CHANGE_MOBILE_FAIL + userRolesVo.getUid();

        ChangeAccountVo resp = new ChangeAccountVo();
        if (redisUtils.hasKey(lockKey)) {
            long expire = redisUtils.getExpire(lockKey);
            Date now = new Date();
            long minute = expire / 60;
            long second = expire % 60;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            resp.setCode(403);
            Date afterDate = new Date(now.getTime() + expire * 1000);
            String msg = "由于您多次修改手机号失败，修改手机号功能已锁定，请在" + minute + "分" + second + "秒后(" + formatter.format(afterDate) + ")再进行尝试！";
            resp.setMsg(msg);
            return resp;
        }
        // 与当前登录用户的密码进行比较判断
        if (userRolesVo.getPassword().equals(SecureUtil.md5(password))) { // 如果相同，则进行修改操作
            UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("mobile", newMobile)
                    .eq("uuid", userRolesVo.getUid());

            boolean isOk = userInfoEntityService.update(updateWrapper);
            if (isOk) {

                UserInfoVo userInfoVo = new UserInfoVo();
                BeanUtil.copyProperties(userRolesVo, userInfoVo, "roles");
                userInfoVo.setRoleList(userRolesVo.getRoles().stream().map(Role::getRole).collect(Collectors.toList()));

                resp.setCode(200);
                resp.setMsg("修改邮箱成功！");
                resp.setUserInfo(userInfoVo);
                // 清空记录
                redisUtils.del(countKey);
                // 更新session
                userRolesVo.setMobile(newMobile);
                session.setAttribute("userInfo", userRolesVo);
                return resp;
            } else {
                throw new StatusSystemErrorException("系统错误：修改手机号失败！");
            }
        } else { // 如果不同，则进行记录，当失败次数达到5次，半个小时后才可重试
            Integer count = (Integer) redisUtils.get(countKey);
            if (count == null) {
                redisUtils.set(countKey, 1, 60 * 30); // 三十分钟不尝试，该限制会自动清空消失
                count = 0;
            } else if (count < 5) {
                redisUtils.incr(countKey, 1);
            }
            count++;
            if (count == 5) {
                redisUtils.del(countKey); // 清空统计
                redisUtils.set(lockKey, "lock", 60 * 30); // 设置锁定更改
            }

            resp.setCode(400);
            resp.setMsg("密码错误！您已累计修改手机号失败" + count + "次...");
            return resp;
        }
    }

    public UserInfoVo changeUserInfo(UserInfoVo userInfoVo) throws StatusFailException {

        // 获取当前登录的用户
        org.apache.shiro.session.Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        String realname = userInfoVo.getRealname();
        String nickname = userInfoVo.getNickname();
        if (!StringUtils.isEmpty(realname) && realname.length() > 50) {
            throw new StatusFailException("真实姓名的长度不能超过50位");
        }
        if (!StringUtils.isEmpty(nickname) && nickname.length() > 20) {
            throw new StatusFailException("昵称的长度不能超过20位");
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUuid(userRolesVo.getUid())
                .setCfUsername(userInfoVo.getCfUsername())
                .setRealname(realname)
                .setNickname(nickname)
                .setSignature(userInfoVo.getSignature())
                .setBlog(userInfoVo.getBlog())
                .setGender(userInfoVo.getGender())
                .setEmail(userRolesVo.getEmail())
                .setGithub(userInfoVo.getGithub())
                .setSchool(userInfoVo.getSchool())
                .setNumber(userInfoVo.getNumber());

        boolean isOk = userInfoEntityService.updateById(userInfo);

        if (isOk) {
            // 更新session
            UserRolesVo userRoles = userRoleEntityService.getUserRoles(userRolesVo.getUid(), null);
            session.setAttribute("userInfo", userRoles);

            UserInfoVo userInfoRes = new UserInfoVo();
            BeanUtil.copyProperties(userRoles, userInfoRes, "roles");
            userInfoRes.setRoleList(userRoles.getRoles().stream().map(Role::getRole).collect(Collectors.toList()));

            return userInfoRes;
        } else {
            throw new StatusFailException("更新个人信息失败！");
        }

    }
}