/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.remoteJudge.task;

import lombok.Getter;
import lombok.Setter;
import com.iuaenasong.oj.remoteJudge.entity.RemoteJudgeDTO;
import com.iuaenasong.oj.remoteJudge.entity.RemoteJudgeRes;

public abstract class RemoteJudgeStrategy {

    @Setter
    @Getter
    private RemoteJudgeDTO remoteJudgeDTO;

    public abstract void submit();

    public abstract RemoteJudgeRes result();

    public abstract void login();

    public abstract String getLanguage(String language);

}
