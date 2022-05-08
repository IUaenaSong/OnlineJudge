/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.judge.self;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import com.iuaenasong.oj.dao.judge.JudgeEntityService;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.utils.Constants;
import com.iuaenasong.oj.utils.RedisUtils;

@Component
@Slf4j(topic = "oj")
@RefreshScope
public class JudgeDispatcher {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private JudgeEntityService judgeEntityService;

    @Autowired
    private JudgeReceiver judgeReceiver;

    @Value("${oj.judge.token}")
    private String judgeToken;

    public void sendTask(Judge judge, Boolean isContest, Boolean isExam) {
        JSONObject task = new JSONObject();
        task.set("judge", judge);
        task.set("token", judgeToken);
        task.set("isContest", isContest);
        task.set("isExam", isExam);
        try {
            boolean isOk;
            if (isContest) {
                isOk = redisUtils.llPush(Constants.Queue.CONTEST_JUDGE_WAITING.getName(), JSONUtil.toJsonStr(task));
            } else if (isExam) {
                isOk = redisUtils.llPush(Constants.Queue.EXAM_JUDGE_WAITING.getName(), JSONUtil.toJsonStr(task));
            } else {
                isOk = redisUtils.llPush(Constants.Queue.GENERAL_JUDGE_WAITING.getName(), JSONUtil.toJsonStr(task));
            }
            if (!isOk) {
                judgeEntityService.updateById(new Judge()
                        .setSubmitId(judge.getSubmitId())
                        .setStatus(Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus())
                        .setErrorMessage("Please try to submit again!")
                );
            }
            // 调用判题任务处理
            judgeReceiver.processWaitingTask();
        } catch (Exception e) {
            log.error("调用redis将判题纳入判题等待队列异常,此次判题任务判为系统错误--------------->{}", e.getMessage());
            judgeEntityService.failToUseRedisPublishJudge(judge.getSubmitId(), judge.getPid(), isContest, isExam);
        }
    }
}