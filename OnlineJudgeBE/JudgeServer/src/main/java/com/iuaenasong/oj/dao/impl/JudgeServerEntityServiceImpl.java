/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.judge.SandboxRun;
import com.iuaenasong.oj.mapper.JudgeServerMapper;
import com.iuaenasong.oj.pojo.entity.judge.JudgeServer;
import com.iuaenasong.oj.dao.JudgeServerEntityService;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

@Service
@Slf4j(topic = "oj")
@RefreshScope
public class JudgeServerEntityServiceImpl extends ServiceImpl<JudgeServerMapper, JudgeServer> implements JudgeServerEntityService {

    @Value("${oj-judge-server.max-task-num}")
    private Integer maxTaskNum;

    @Value("${oj-judge-server.remote-judge.open}")
    private Boolean isOpenRemoteJudge;

    @Value("${oj-judge-server.remote-judge.max-task-num}")
    private Integer RemoteJudgeMaxTaskNum;

    @Value("${oj-judge-server.name}")
    private String name;

    @Override
    public HashMap<String, Object> getJudgeServerInfo() {

        HashMap<String, Object> res = new HashMap<>();

        res.put("version", "20220306");
        res.put("currentTime", new Date());
        res.put("judgeServerName", name);
        res.put("cpu", Runtime.getRuntime().availableProcessors());
        res.put("languages", Arrays.asList("G++ 7.5.0", "GCC 7.5.0", "Python 3.7.5",
                "Python 2.7.17", "OpenJDK 1.8", "Golang 1.16", "C# Mono 4.6.2",
                "PHP 7.3.33","JavaScript Node 14.19.0","JavaScript V8 8.4.109",
                "PyPy 2.7.18 (7.3.8)","PyPy 3.8.12 (7.3.8)"));

        if (maxTaskNum == -1) {
            res.put("maxTaskNum", Runtime.getRuntime().availableProcessors() + 1);
        } else {
            res.put("maxTaskNum", maxTaskNum);
        }
        if (isOpenRemoteJudge) {
            res.put("isOpenRemoteJudge", true);
            if (RemoteJudgeMaxTaskNum == -1) {
                res.put("remoteJudgeMaxTaskNum", Runtime.getRuntime().availableProcessors() * 2 + 1);
            } else {
                res.put("remoteJudgeMaxTaskNum", RemoteJudgeMaxTaskNum);
            }
        }

        String versionResp = "";

        try {
            versionResp = SandboxRun.getRestTemplate().getForObject(SandboxRun.getSandboxBaseUrl() + "/version", String.class);
        } catch (Exception e) {
            res.put("SandBoxMsg", MapUtil.builder().put("error", e.getMessage()).map());
            return res;
        }

        res.put("SandBoxMsg", JSONUtil.parseObj(versionResp));
        return res;
    }
}