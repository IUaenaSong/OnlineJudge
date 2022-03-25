/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service;

public interface RemoteJudgeService {

    public void changeAccountStatus(String remoteJudge, String username);

    public void changeServerSubmitCFStatus(String ip, Integer port);
}
