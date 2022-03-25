/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.judge;

public abstract class AbstractReceiver {

    public void handleWaitingTask(String... queues) {
        for (String queue : queues) {
            String taskJsonStr = getTaskByRedis(queue);
            if (taskJsonStr != null) {
                handleJudgeMsg(taskJsonStr);
            }
        }
    }

    public abstract String getTaskByRedis(String queue);

    public abstract void handleJudgeMsg(String taskJsonStr);
}