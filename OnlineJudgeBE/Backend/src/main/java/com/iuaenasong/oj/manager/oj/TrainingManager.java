/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.oj;

import cn.hutool.core.bean.BeanUtil;
import com.iuaenasong.oj.dao.group.GroupMemberEntityService;
import com.iuaenasong.oj.validator.GroupValidator;
import com.iuaenasong.oj.validator.TrainingValidator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.iuaenasong.oj.common.exception.StatusAccessDeniedException;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.manager.admin.training.AdminTrainingRecordManager;
import com.iuaenasong.oj.pojo.dto.RegisterTrainingDto;
import com.iuaenasong.oj.pojo.entity.training.*;
import com.iuaenasong.oj.pojo.vo.*;
import com.iuaenasong.oj.dao.training.*;
import com.iuaenasong.oj.dao.user.UserInfoEntityService;
import com.iuaenasong.oj.utils.Constants;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TrainingManager {

    @Resource
    private TrainingEntityService trainingEntityService;

    @Resource
    private TrainingRegisterEntityService trainingRegisterEntityService;

    @Resource
    private TrainingCategoryEntityService trainingCategoryEntityService;

    @Resource
    private TrainingProblemEntityService trainingProblemEntityService;

    @Resource
    private TrainingRecordEntityService trainingRecordEntityService;

    @Resource
    private UserInfoEntityService userInfoEntityService;

    @Resource
    private AdminTrainingRecordManager adminTrainingRecordManager;

    @Autowired
    private GroupValidator groupValidator;

    @Resource
    private TrainingValidator trainingValidator;

    @Autowired
    private GroupMemberEntityService groupMemberEntityService;
    
    public IPage<TrainingVo> getTrainingList(Integer limit, Integer currentPage, String keyword, Long categoryId, String auth) {

        // ????????????????????????????????????????????????
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 30;
        return trainingEntityService.getTrainingList(limit, currentPage, categoryId, auth, keyword);
    }

    
    public TrainingVo getTraining(Long tid) throws StatusFailException, StatusAccessDeniedException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Training training = trainingEntityService.getById(tid);
        if (training == null || !training.getStatus()) {
            throw new StatusFailException("???????????????????????????????????????");
        }

        if (!training.getIsPublic()) {
            if (!groupValidator.isGroupMember(userRolesVo.getUid(), training.getGid()) && !isRoot) {
                throw new StatusForbiddenException("?????????????????????????????????");
            }
        }

        TrainingVo trainingVo = BeanUtil.copyProperties(training, TrainingVo.class);
        TrainingCategory trainingCategory = trainingCategoryEntityService.getTrainingCategoryByTrainingId(training.getId());
        trainingVo.setCategoryName(trainingCategory.getName());
        trainingVo.setCategoryColor(trainingCategory.getColor());
        List<Long> trainingProblemIdList = trainingProblemEntityService.getTrainingProblemIdList(training.getId());
        trainingVo.setProblemCount(trainingProblemIdList.size());

        if (userRolesVo != null && trainingValidator.isInTrainingOrAdmin(training, userRolesVo)) {
            Integer userTrainingACProblemCount = trainingProblemEntityService.getUserTrainingACProblemCount(userRolesVo.getUid(), trainingProblemIdList);
            trainingVo.setAcCount(userTrainingACProblemCount);
        } else {
            trainingVo.setAcCount(0);
        }

        return trainingVo;
    }

    
    public List<ProblemVo> getTrainingProblemList(Long tid) throws StatusAccessDeniedException,
            StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Training training = trainingEntityService.getById(tid);
        if (training == null || !training.getStatus()) {
            throw new StatusFailException("???????????????????????????????????????");
        }

        if (!training.getIsPublic()) {
            if (!groupValidator.isGroupMember(userRolesVo.getUid(), training.getGid()) && !isRoot) {
                throw new StatusForbiddenException("?????????????????????????????????");
            }
        }

        trainingValidator.validateTrainingAuth(training);

        return trainingProblemEntityService.getTrainingProblemList(tid);

    }

    
    public void toRegisterTraining(RegisterTrainingDto registerTrainingDto) throws StatusFailException, StatusForbiddenException {

        Long tid = registerTrainingDto.getTid();
        String password = registerTrainingDto.getPassword();

        if (tid == null || StringUtils.isEmpty(password)) {
            throw new StatusFailException("???????????????????????????");
        }

        // ???????????????????????????
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Training training = trainingEntityService.getById(tid);

        if (training == null || !training.getStatus()) {
            throw new StatusFailException("????????????????????????????????????????????????!");
        }

        if (!training.getPrivatePwd().equals(password)) { // ????????????
            throw new StatusFailException("???????????????????????????????????????");
        }

        if (!training.getIsPublic()) {
            if (!groupValidator.isGroupMember(userRolesVo.getUid(), training.getGid()) && !isRoot) {
                throw new StatusForbiddenException("?????????????????????????????????");
            }
        }

        QueryWrapper<TrainingRegister> registerQueryWrapper = new QueryWrapper<>();
        registerQueryWrapper.eq("tid", tid).eq("uid", userRolesVo.getUid());
        if (trainingRegisterEntityService.count(registerQueryWrapper) > 0) {
            throw new StatusFailException("????????????????????????????????????????????????");
        }

        boolean isOk = trainingRegisterEntityService.save(new TrainingRegister()
                .setTid(tid)
                .setUid(userRolesVo.getUid()));

        if (!isOk) {
            throw new StatusFailException("??????????????????????????????????????????");
        } else {
            adminTrainingRecordManager.syncUserSubmissionToRecordByTid(tid, userRolesVo.getUid());
        }
    }

    
    public AccessVo getTrainingAccess(Long tid) throws StatusFailException {

        // ???????????????????????????
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        QueryWrapper<TrainingRegister> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tid", tid).eq("uid", userRolesVo.getUid());
        TrainingRegister trainingRegister = trainingRegisterEntityService.getOne(queryWrapper, false);
        boolean access = false;
        if (trainingRegister != null) {
            access = true;
            Training training = trainingEntityService.getById(tid);
            if (training == null || !training.getStatus()) {
                throw new StatusFailException("??????????????????????????????!");
            }
        }

        AccessVo accessVo = new AccessVo();
        accessVo.setAccess(access);

        return accessVo;
    }

    
    public IPage<TrainingRankVo> getTrainingRank(Long tid, Integer limit, Integer currentPage) throws
            StatusAccessDeniedException, StatusForbiddenException, StatusFailException {

        Training training = trainingEntityService.getById(tid);
        if (training == null || !training.getStatus()) {
            throw new StatusFailException("???????????????????????????????????????");
        }

        trainingValidator.validateTrainingAuth(training);

        // ?????????????????????????????????????????????
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 30;

        return getTrainingRank(tid, training.getAuthor(), currentPage, limit);
    }

    private IPage<TrainingRankVo> getTrainingRank(Long tid, String username, int currentPage, int limit) {

        Map<Long, String> tpIdMapDisplayId = getTPIdMapDisplayId(tid);
        List<TrainingRecordVo> trainingRecordVoList = trainingRecordEntityService.getTrainingRecord(tid);

        List<String> superAdminUidList = userInfoEntityService.getSuperAdminUidList();

        Training training = trainingEntityService.getById(tid);
        List<String> groupRootUidList = groupMemberEntityService.getGroupRootUidList(training.getGid());
        if (!CollectionUtils.isEmpty(groupRootUidList)) {
            superAdminUidList.addAll(groupRootUidList);
        }

        List<TrainingRankVo> result = new ArrayList<>();

        HashMap<String, Integer> uidMapIndex = new HashMap<>();
        int pos = 0;

        for (TrainingRecordVo trainingRecordVo : trainingRecordVoList) {
            // ?????????????????????????????????????????????????????????
            if (username.equals(trainingRecordVo.getUsername()) || superAdminUidList.contains(trainingRecordVo.getUid())) {
                continue;
            }

            TrainingRankVo trainingRankVo;
            Integer index = uidMapIndex.get(trainingRecordVo.getUid());
            if (index == null) {
                trainingRankVo = new TrainingRankVo();
                trainingRankVo.setRealname(trainingRecordVo.getRealname())
                        .setAvatar(trainingRecordVo.getAvatar())
                        .setSchool(trainingRecordVo.getSchool())
                        .setGender(trainingRecordVo.getGender())
                        .setUid(trainingRecordVo.getUid())
                        .setUsername(trainingRecordVo.getUsername())
                        .setNickname(trainingRecordVo.getNickname())
                        .setAc(0)
                        .setTotalRunTime(0);
                HashMap<String, HashMap<String, Object>> submissionInfo = new HashMap<>();
                trainingRankVo.setSubmissionInfo(submissionInfo);

                result.add(trainingRankVo);
                uidMapIndex.put(trainingRecordVo.getUid(), pos);
                pos++;
            } else {
                trainingRankVo = result.get(index);
            }
            String displayId = tpIdMapDisplayId.get(trainingRecordVo.getTpid());
            HashMap<String, Object> problemSubmissionInfo = trainingRankVo
                    .getSubmissionInfo()
                    .getOrDefault(displayId, new HashMap<>());

            // ?????????????????????AC???????????????????????????????????????
            if ((Boolean) problemSubmissionInfo.getOrDefault("isAC", false)) {
                if (trainingRecordVo.getStatus().intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus()) {
                    int runTime = (int) problemSubmissionInfo.getOrDefault("runTime", 0);
                    if (runTime > trainingRecordVo.getUseTime()) {
                        trainingRankVo.setTotalRunTime(trainingRankVo.getTotalRunTime() - runTime + trainingRecordVo.getUseTime());
                        problemSubmissionInfo.put("runTime", trainingRecordVo.getUseTime());
                    }
                }
                continue;
            }

            problemSubmissionInfo.put("status", trainingRecordVo.getStatus());
            problemSubmissionInfo.put("score", trainingRecordVo.getScore());

            // ????????????
            if (trainingRecordVo.getStatus().intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus()) {
                // ?????????????????????ac+1
                trainingRankVo.setAc(trainingRankVo.getAc() + 1);
                problemSubmissionInfo.put("isAC", true);
                problemSubmissionInfo.put("runTime", trainingRecordVo.getUseTime());
                trainingRankVo.setTotalRunTime(trainingRankVo.getTotalRunTime() + trainingRecordVo.getUseTime());
            }

            trainingRankVo.getSubmissionInfo().put(displayId, problemSubmissionInfo);
        }

        List<TrainingRankVo> orderResultList = result.stream().sorted(Comparator.comparing(TrainingRankVo::getAc, Comparator.reverseOrder()) // ?????????ac?????????
                .thenComparing(TrainingRankVo::getTotalRunTime) //?????????????????????
        ).collect(Collectors.toList());

        // ???????????????????????????????????????
        Page<TrainingRankVo> page = new Page<>(currentPage, limit);
        int count = orderResultList.size();
        List<TrainingRankVo> pageList = new ArrayList<>();
        //???????????????????????????????????????
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

    private Map<Long, String> getTPIdMapDisplayId(Long tid) {
        QueryWrapper<TrainingProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tid", tid);
        List<TrainingProblem> trainingProblemList = trainingProblemEntityService.list(queryWrapper);
        return trainingProblemList.stream().collect(Collectors.toMap(TrainingProblem::getId, TrainingProblem::getDisplayId));
    }

    
    @Async
    public void checkAndSyncTrainingRecord(Long pid, Long submitId, String uid) {

        QueryWrapper<TrainingProblem> trainingProblemQueryWrapper = new QueryWrapper<>();
        trainingProblemQueryWrapper.eq("pid", pid);

        List<TrainingProblem> trainingProblemList = trainingProblemEntityService.list(trainingProblemQueryWrapper);
        List<TrainingRecord> trainingRecordList = new ArrayList<>();
        for (TrainingProblem trainingProblem : trainingProblemList) {
            TrainingRecord trainingRecord = new TrainingRecord();
            trainingRecord.setPid(pid)
                    .setTid(trainingProblem.getTid())
                    .setTpid(trainingProblem.getId())
                    .setSubmitId(submitId)
                    .setUid(uid);
            trainingRecordList.add(trainingRecord);
        }
        if (trainingRecordList.size() > 0) {
            trainingRecordEntityService.saveBatch(trainingRecordList);
        }
    }

}