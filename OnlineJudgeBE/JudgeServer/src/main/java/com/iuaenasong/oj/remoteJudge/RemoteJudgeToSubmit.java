/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.remoteJudge;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import com.iuaenasong.oj.dao.JudgeEntityService;
import com.iuaenasong.oj.judge.JudgeContext;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.remoteJudge.entity.RemoteJudgeDTO;
import com.iuaenasong.oj.remoteJudge.task.RemoteJudgeStrategy;
import com.iuaenasong.oj.service.RemoteJudgeService;
import com.iuaenasong.oj.utils.Constants;

@Component
@Slf4j(topic = "oj")
@RefreshScope
public class RemoteJudgeToSubmit {

    @Autowired
    private JudgeEntityService judgeEntityService;

    @Autowired
    private RemoteJudgeService remoteJudgeService;

    @Autowired
    private JudgeContext judgeContext;

    @Value("${oj-judge-server.name}")
    private String name;

    public boolean process(RemoteJudgeStrategy remoteJudgeStrategy) {

        RemoteJudgeDTO remoteJudgeDTO = remoteJudgeStrategy.getRemoteJudgeDTO();
        log.info("Ready Send Task to RemoteJudgeDTO => {}", remoteJudgeDTO);

        String errLog = null;
        try {
            remoteJudgeStrategy.submit();
        } catch (Exception e) {
            log.error("Submit Failed! Error:", e);
            errLog = e.getMessage();
        }

        Long submitId = remoteJudgeDTO.getSubmitId();
        // 提交失败 前端手动按按钮再次提交 修改状态 STATUS_SUBMITTED_FAILED
        if (submitId == null || submitId == -1L) {
            // 将使用的账号放回对应列表
            log.error("[{}] Submit Failed! Begin to return the account to other task!", remoteJudgeDTO.getOj());
            remoteJudgeService.changeAccountStatus(remoteJudgeDTO.getOj(),
                    remoteJudgeDTO.getUsername());

            if (RemoteJudgeContext.openCodeforcesFixServer) {
                if (remoteJudgeDTO.getOj().equals(Constants.RemoteJudge.GYM_JUDGE.getName())
                        || remoteJudgeDTO.getOj().equals(Constants.RemoteJudge.CF_JUDGE.getName())) {
                    // 对CF特殊，归还判题机权限
                    log.error("[{}] Submit Failed! Begin to return the Server Status to other task!", remoteJudgeDTO.getOj());
                    remoteJudgeService.changeServerSubmitCFStatus(remoteJudgeDTO.getServerIp(), remoteJudgeDTO.getServerPort());
                }
            }

            // 更新此次提交状态为提交失败！
            UpdateWrapper<Judge> judgeUpdateWrapper = new UpdateWrapper<>();
            judgeUpdateWrapper.set("status", Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus())
                    .set("error_message", errLog)
                    .eq("submit_id", remoteJudgeDTO.getJudgeId());
            judgeEntityService.update(judgeUpdateWrapper);
            // 更新其它表
            judgeContext.updateOtherTable(remoteJudgeDTO.getSubmitId(),
                    Constants.Judge.STATUS_SYSTEM_ERROR.getStatus(),
                    remoteJudgeDTO.getCid(),
                    remoteJudgeDTO.getEid(),
                    remoteJudgeDTO.getUid(),
                    remoteJudgeDTO.getPid(),
                    remoteJudgeDTO.getIsPublic(),
                    null,
                    null);
            return false;
        }

        // 提交成功顺便更新状态为-->STATUS_PENDING 等待判题中...
        judgeEntityService.updateById(new Judge()
                .setSubmitId(remoteJudgeDTO.getJudgeId())
                .setStatus(Constants.Judge.STATUS_PENDING.getStatus())
                .setVjudgeSubmitId(submitId)
                .setVjudgeUsername(remoteJudgeDTO.getUsername())
                .setVjudgePassword(remoteJudgeDTO.getPassword())
                .setJudger(name)
        );

        log.info("[{}] Submit Successfully! The submit_id of remote judge is [{}]. Waiting the result of the task!",
                submitId, remoteJudgeDTO.getOj());
        return true;
    }
}
