/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.validator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.iuaenasong.oj.common.exception.StatusAccessDeniedException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.dao.training.TrainingRegisterEntityService;
import com.iuaenasong.oj.pojo.entity.training.Training;
import com.iuaenasong.oj.pojo.entity.training.TrainingRegister;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.utils.Constants;

import javax.annotation.Resource;

@Component
public class TrainingValidator {

    @Resource
    private TrainingRegisterEntityService trainingRegisterEntityService;

    @Autowired
    private GroupValidator groupValidator;

    public void validateTrainingAuth(Training training) throws StatusAccessDeniedException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");
        validateTrainingAuth(training, userRolesVo);
    }

    public void validateTrainingAuth(Training training, UserRolesVo userRolesVo) throws StatusAccessDeniedException, StatusForbiddenException {
        if (Constants.Training.AUTH_PRIVATE.getValue().equals(training.getAuth())) {
            if (userRolesVo == null) {
                throw new StatusAccessDeniedException("该训练属于私有题单，请先登录以校验权限！");
            }
            boolean isRoot = SecurityUtils.getSubject().hasRole("root"); // 是否为超级管理员
            boolean isAuthor = training.getAuthor().equals(userRolesVo.getUsername()); // 是否为该私有训练的创建者

            if (!isRoot && !isAuthor && !groupValidator.isGroupRoot(userRolesVo.getUid(), training.getGid())) { // 如果两者都不是，需要做注册权限校验
                checkTrainingRegister(training.getId(), userRolesVo.getUid());
            }
        }
    }

    private void checkTrainingRegister(Long tid, String uid) throws StatusAccessDeniedException, StatusForbiddenException {
        QueryWrapper<TrainingRegister> trainingRegisterQueryWrapper = new QueryWrapper<>();
        trainingRegisterQueryWrapper.eq("tid", tid);
        trainingRegisterQueryWrapper.eq("uid", uid);
        TrainingRegister trainingRegister = trainingRegisterEntityService.getOne(trainingRegisterQueryWrapper, false);

        if (trainingRegister == null) {
            throw new StatusAccessDeniedException("该训练属于私有，请先使用专属密码注册！");
        }

        if (!trainingRegister.getStatus()) {
            throw new StatusForbiddenException("错误：你已被禁止参加该训练！");
        }
    }

    public boolean isInTrainingOrAdmin(Training training, UserRolesVo userRolesVo) throws StatusAccessDeniedException {
        if (Constants.Training.AUTH_PRIVATE.getValue().equals(training.getAuth())) {
            if (userRolesVo == null) {
                throw new StatusAccessDeniedException("该训练属于私有题单，请先登录以校验权限！");
            }
            boolean isRoot = SecurityUtils.getSubject().hasRole("root"); // 是否为超级管理员
            boolean isAuthor = training.getAuthor().equals(userRolesVo.getUsername()); // 是否为该私有训练的创建者

            if (!isRoot && !isAuthor && !groupValidator.isGroupRoot(userRolesVo.getUid(), training.getGid())) { // 如果两者都不是，需要做注册权限校验
                QueryWrapper<TrainingRegister> trainingRegisterQueryWrapper = new QueryWrapper<>();
                trainingRegisterQueryWrapper.eq("tid", training.getId());
                trainingRegisterQueryWrapper.eq("uid", userRolesVo.getUid());
                TrainingRegister trainingRegister = trainingRegisterEntityService.getOne(trainingRegisterQueryWrapper, false);

                return trainingRegister != null && trainingRegister.getStatus();
            }
        }
        return true;
    }
}