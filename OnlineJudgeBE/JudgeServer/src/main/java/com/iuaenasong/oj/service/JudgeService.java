/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service;

import com.iuaenasong.oj.common.exception.SystemError;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.pojo.entity.judge.ToJudge;

import java.util.HashMap;

public interface JudgeService {

    public void judge(Judge judge);

    public void remoteJudge(ToJudge toJudge);

    public Boolean compileSpj(String code, Long pid, String spjLanguage, HashMap<String, String> extraFiles) throws SystemError;

    public Boolean compileInteractive(String code, Long pid, String interactiveLanguage, HashMap<String, String> extraFiles) throws SystemError;

}
