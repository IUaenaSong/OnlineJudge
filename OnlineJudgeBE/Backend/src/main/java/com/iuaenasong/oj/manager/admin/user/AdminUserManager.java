/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.admin.user;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.iuaenasong.oj.utils.Constants;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.manager.msg.AdminNoticeManager;
import com.iuaenasong.oj.pojo.dto.AdminEditUserDto;
import com.iuaenasong.oj.pojo.entity.user.UserInfo;
import com.iuaenasong.oj.pojo.entity.user.UserRecord;
import com.iuaenasong.oj.pojo.entity.user.UserRole;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.dao.user.UserInfoEntityService;
import com.iuaenasong.oj.dao.user.UserRecordEntityService;
import com.iuaenasong.oj.dao.user.UserRoleEntityService;
import com.iuaenasong.oj.utils.RedisUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AdminUserManager {

    @Autowired
    private UserRoleEntityService userRoleEntityService;

    @Autowired
    private UserInfoEntityService userInfoEntityService;

    @Autowired
    private AdminNoticeManager adminNoticeManager;

    @Autowired
    private UserRecordEntityService userRecordEntityService;

    @Autowired
    private RedisUtils redisUtils;

    public IPage<UserRolesVo> getUserList(Integer limit, Integer currentPage, Boolean onlyAdmin, String keyword) {
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        if (keyword != null) {
            keyword = keyword.trim();
        }
        return userRoleEntityService.getUserList(limit, currentPage, keyword, onlyAdmin);
    }

    public void editUser(AdminEditUserDto adminEditUserDto) throws StatusFailException {

        String username = adminEditUserDto.getUsername();
        String uid = adminEditUserDto.getUid();
        String realname = adminEditUserDto.getRealname();
        String email = adminEditUserDto.getEmail();
        String password = adminEditUserDto.getPassword();
        int type = adminEditUserDto.getType();
        int status = adminEditUserDto.getStatus();
        boolean setNewPwd = adminEditUserDto.getSetNewPwd();

        String titleName = adminEditUserDto.getTitleName();
        String titleColor = adminEditUserDto.getTitleColor();

        if (!StringUtils.isEmpty(realname) && realname.length() > 50) {
            throw new StatusFailException("真实姓名的长度不能超过50位");
        }

        if (!StringUtils.isEmpty(titleName) && titleName.length() > 20) {
            throw new StatusFailException("头衔的长度建议不要超过20位");
        }

        if (!StringUtils.isEmpty(password) && (password.length() < 6 || password.length() > 20)) {
            throw new StatusFailException("密码长度建议为6~20位！");
        }

        if (username.length() > 20) {
            throw new StatusFailException("用户名长度建议不能超过20位!");
        }

        UpdateWrapper<UserInfo> userInfoUpdateWrapper = new UpdateWrapper<>();

        userInfoUpdateWrapper.eq("uuid", uid)
                .set("username", username)
                .set("realname", realname)
                .set("email", email)
                .set(setNewPwd, "password", SecureUtil.md5(password))
                .set("title_name", titleName)
                .set("title_color", titleColor)
                .set("status", status);
        boolean updateUserInfo = userInfoEntityService.update(userInfoUpdateWrapper);

        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.eq("uid", uid);
        UserRole userRole = userRoleEntityService.getOne(userRoleQueryWrapper, false);
        boolean changeUserRole = false;
        int oldType = userRole.getRoleId().intValue();
        if (userRole.getRoleId().intValue() != type) {
            userRole.setRoleId((long) type);
            changeUserRole = userRoleEntityService.updateById(userRole);
            if (type == 1000 || oldType == 1000) {
                // 新增或者去除超级管理员需要删除缓存
                String cacheKey = Constants.Account.SUPER_ADMIN_UID_LIST_CACHE.getCode();
                redisUtils.del(cacheKey);
            }
        }
        if (updateUserInfo && setNewPwd) {
            // 需要重新登录
            userRoleEntityService.deleteCache(uid, true);
        } else if (changeUserRole) {
            // 需要重新授权
            userRoleEntityService.deleteCache(uid, false);
        }

        if (changeUserRole) {
            // 获取当前登录的用户
            Session session = SecurityUtils.getSubject().getSession();
            UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");
            String title = "权限变更通知(Authority Change Notice)";
            String content = userRoleEntityService.getAuthChangeContent(oldType, type);
            adminNoticeManager.addSingleNoticeToUser(userRolesVo.getUid(), uid, title, content, "Sys");
        }

    }

    public void deleteUser(List<String> deleteUserIdList) throws StatusFailException {
        boolean isOk = userInfoEntityService.removeByIds(deleteUserIdList);
        if (!isOk) {
            throw new StatusFailException("删除失败！");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertBatchUser(List<List<String>> users) throws StatusFailException {
        List<UserInfo> userInfoList = new LinkedList<>();
        List<UserRole> userRoleList = new LinkedList<>();
        List<UserRecord> userRecordList = new LinkedList<>();
        if (users != null) {
            for (List<String> user : users) {
                String uuid = IdUtil.simpleUUID();
                UserInfo userInfo = new UserInfo()
                        .setUuid(uuid)
                        .setUsername(user.get(0))
                        .setPassword(SecureUtil.md5(user.get(1)))
                        .setEmail(StringUtils.isEmpty(user.get(2)) ? null : user.get(2));

                if (user.size() >= 4) {
                    String realname = user.get(3);
                    if (!StringUtils.isEmpty(realname)) {
                        userInfo.setRealname(user.get(3));
                    }
                }

                if (user.size() >= 5) {
                    String gender = user.get(4);
                    if ("male".equals(gender.toLowerCase()) || "0".equals(gender)) {
                        userInfo.setGender("male");
                    } else if ("female".equals(gender.toLowerCase()) || "1".equals(gender)) {
                        userInfo.setGender("female");
                    }
                }

                if (user.size() >= 6) {
                    String nickname = user.get(5);
                    if (!StringUtils.isEmpty(nickname)) {
                        userInfo.setNickname(nickname);
                    }
                }

                if (user.size() >= 7) {
                    String school = user.get(6);
                    if (!StringUtils.isEmpty(school)) {
                        userInfo.setSchool(school);
                    }
                }

                userInfoList.add(userInfo);
                userRoleList.add(new UserRole()
                        .setRoleId(1002L)
                        .setUid(uuid));
                userRecordList.add(new UserRecord().setUid(uuid));
            }
            boolean result1 = userInfoEntityService.saveBatch(userInfoList);
            boolean result2 = userRoleEntityService.saveBatch(userRoleList);
            boolean result3 = userRecordEntityService.saveBatch(userRecordList);
            if (result1 && result2 && result3) {
                // 异步同步系统通知
                List<String> uidList = userInfoList.stream().map(UserInfo::getUuid).collect(Collectors.toList());
                adminNoticeManager.syncNoticeToNewRegisterBatchUser(uidList);
            } else {
                throw new StatusFailException("删除失败");
            }
        } else {
            throw new StatusFailException("插入的用户数据不能为空！");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<Object, Object> generateUser(Map<String, Object> params) throws StatusFailException {

        String prefix = (String) params.getOrDefault("prefix", "");
        String suffix = (String) params.getOrDefault("suffix", "");
        int numberFrom = (int) params.getOrDefault("number_from", 1);
        int numberTo = (int) params.getOrDefault("number_to", 10);
        int passwordLength = (int) params.getOrDefault("password_length", 6);

        List<UserInfo> userInfoList = new LinkedList<>();
        List<UserRole> userRoleList = new LinkedList<>();
        List<UserRecord> userRecordList = new LinkedList<>();

        HashMap<String, Object> userInfo = new HashMap<>(); // 存储账号密码放入redis中，等待导出excel
        for (int num = numberFrom; num <= numberTo; num++) {
            String uuid = IdUtil.simpleUUID();
            String password = RandomUtil.randomString(passwordLength);
            String username = prefix + num + suffix;
            userInfoList.add(new UserInfo()
                    .setUuid(uuid)
                    .setUsername(username)
                    .setPassword(SecureUtil.md5(password)));
            userInfo.put(username, password);
            userRoleList.add(new UserRole()
                    .setRoleId(1002L)
                    .setUid(uuid));
            userRecordList.add(new UserRecord().setUid(uuid));
        }
        boolean result1 = userInfoEntityService.saveBatch(userInfoList);
        boolean result2 = userRoleEntityService.saveBatch(userRoleList);
        boolean result3 = userRecordEntityService.saveBatch(userRecordList);
        if (result1 && result2 && result3) {
            String key = IdUtil.simpleUUID();
            redisUtils.hmset(key, userInfo, 1800); // 存储半小时
            // 异步同步系统通知
            List<String> uidList = userInfoList.stream().map(UserInfo::getUuid).collect(Collectors.toList());
            adminNoticeManager.syncNoticeToNewRegisterBatchUser(uidList);
            return MapUtil.builder().put("key", key).map();
        } else {
            throw new StatusFailException("生成指定用户失败！");
        }
    }
}