/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.remoteJudge;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.dao.JudgeEntityService;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.pojo.entity.judge.ToJudge;
import com.iuaenasong.oj.remoteJudge.entity.RemoteJudgeDTO;
import com.iuaenasong.oj.remoteJudge.task.RemoteJudgeFactory;
import com.iuaenasong.oj.remoteJudge.task.RemoteJudgeStrategy;
import com.iuaenasong.oj.util.Constants;

import javax.annotation.Resource;

@Service
@Slf4j(topic = "oj")
public class RemoteJudgeContext {

    @Resource
    private RemoteJudgeToSubmit remoteJudgeToSubmit;

    @Resource
    private RemoteJudgeGetResult remoteJudgeGetResult;

    @Resource
    private JudgeEntityService judgeEntityService;

    public static final boolean openCodeforcesFixServer = false;

    @Async
    public void judge(ToJudge toJudge) {
        String[] source = toJudge.getRemoteJudgeProblem().split("-");
        String remoteOj = source[0];
        String remoteProblemId = source[1];

        RemoteJudgeDTO remoteJudgeDTO = RemoteJudgeDTO.builder()
                .judgeId(toJudge.getJudge().getSubmitId())
                .uid(toJudge.getJudge().getUid())
                .cid(toJudge.getJudge().getCid())
                .pid(toJudge.getJudge().getPid())
                .username(toJudge.getUsername())
                .password(toJudge.getPassword())
                .oj(remoteOj)
                .completeProblemId(remoteProblemId)
                .userCode(toJudge.getJudge().getCode())
                .language(toJudge.getJudge().getLanguage())
                .serverIp(toJudge.getJudgeServerIp())
                .serverPort(toJudge.getJudgeServerPort())
                .submitId(toJudge.getJudge().getVjudgeSubmitId())
                .isPublic(toJudge.getJudge().getIsPublic())
                .build();

        initProblemId(remoteJudgeDTO);

        Boolean isHasSubmitIdRemoteReJudge = toJudge.getIsHasSubmitIdRemoteReJudge();

        RemoteJudgeStrategy remoteJudgeStrategy = buildJudgeStrategy(remoteJudgeDTO);
        if (remoteJudgeStrategy != null) {
            if (isHasSubmitIdRemoteReJudge != null && isHasSubmitIdRemoteReJudge) {
                // 拥有远程oj的submitId远程判题的重判
                remoteJudgeGetResult.process(remoteJudgeStrategy);
            } else {
                // 调用远程判题
                boolean isSubmitOk = remoteJudgeToSubmit.process(remoteJudgeStrategy);
                if (isSubmitOk) {
                    remoteJudgeGetResult.process(remoteJudgeStrategy);
                }
            }
        }
    }

    private void initProblemId(RemoteJudgeDTO remoteJudgeDTO){
        switch (remoteJudgeDTO.getOj()){
            case "GYM":
            case "CF":
                if (NumberUtil.isInteger(remoteJudgeDTO.getCompleteProblemId())) {
                    remoteJudgeDTO.setContestId(ReUtil.get("([0-9]+)[0-9]{2}", remoteJudgeDTO.getCompleteProblemId(), 1));
                    remoteJudgeDTO.setProblemNum(ReUtil.get("[0-9]+([0-9]{2})", remoteJudgeDTO.getCompleteProblemId(), 1));
                } else {
                    remoteJudgeDTO.setContestId(ReUtil.get("([0-9]+)[A-Z]{1}[0-9]{0,1}", remoteJudgeDTO.getCompleteProblemId(), 1));
                    remoteJudgeDTO.setProblemNum(ReUtil.get("[0-9]+([A-Z]{1}[0-9]{0,1})", remoteJudgeDTO.getCompleteProblemId(), 1));
                }
                break;
            case "AC":
                String[] arr = remoteJudgeDTO.getCompleteProblemId().split("_");
                remoteJudgeDTO.setContestId(arr[0]);
                remoteJudgeDTO.setProblemNum(arr[1]);
                break;
        }
    }

    private RemoteJudgeStrategy buildJudgeStrategy(RemoteJudgeDTO remoteJudgeDTO) {
        RemoteJudgeStrategy remoteJudgeStrategy = RemoteJudgeFactory.selectJudge(remoteJudgeDTO.getOj());
        if (remoteJudgeStrategy == null) {
            // 更新此次提交状态为系统失败！
            UpdateWrapper<Judge> judgeUpdateWrapper = new UpdateWrapper<>();
            judgeUpdateWrapper.set("status", Constants.Judge.STATUS_SYSTEM_ERROR.getStatus())
                    .set("error_message", "The judge server does not support this oj:" + remoteJudgeDTO.getOj())
                    .eq("submit_id", remoteJudgeDTO.getJudgeId());
            judgeEntityService.update(judgeUpdateWrapper);
            return null;
        }
        remoteJudgeStrategy.setRemoteJudgeDTO(remoteJudgeDTO);
        return remoteJudgeStrategy;
    }

}