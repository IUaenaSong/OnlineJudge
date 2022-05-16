/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.config;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.iuaenasong.oj.dao.JudgeServerEntityService;
import com.iuaenasong.oj.pojo.entity.judge.JudgeServer;
import com.iuaenasong.oj.utils.IpUtils;

import java.util.HashMap;

@Component
@Slf4j
public class StartupRunner implements CommandLineRunner {

    @Value("${oj-judge-server.max-task-num}")
    private Integer maxTaskNum;

    @Value("${oj-judge-server.remote-judge.max-task-num}")
    private Integer maxRemoteTaskNum;

    @Value("${oj-judge-server.remote-judge.open}")
    private Boolean openRemoteJudge;

    @Value("${oj-judge-server.name}")
    private String name;

    @Value("${oj-judge-server.ip}")
    private String ip;

    @Value("${oj-judge-server.port}")
    private Integer port;

    private static final int cpuNum = Runtime.getRuntime().availableProcessors();

    @Autowired
    private JudgeServerEntityService judgeServerEntityService;

    @Override
    @Transactional
    public void run(String... args) {

        log.info("IP  of the current judge server:" + ip);
        log.info("Port of the current judge server:" + port);

        if (maxTaskNum == -1) {
            maxTaskNum = cpuNum + 1;
        }
        if (ip.equals("-1")) {
            ip = IpUtils.getLocalIpv4Address();
        }
        UpdateWrapper<JudgeServer> judgeServerQueryWrapper = new UpdateWrapper<>();
        judgeServerQueryWrapper.eq("ip", ip).eq("port", port);
        judgeServerEntityService.remove(judgeServerQueryWrapper);
        boolean isOk1 = judgeServerEntityService.save(new JudgeServer()
                .setCpuCore(cpuNum)
                .setIp(ip)
                .setPort(port)
                .setUrl(ip + ":" + port)
                .setMaxTaskNumber(maxTaskNum)
                .setIsRemote(false)
                .setName(name));
        boolean isOk2 = true;
        if (openRemoteJudge) {
            if (maxRemoteTaskNum == -1) {
                maxRemoteTaskNum = cpuNum * 2 + 1;
            }
            isOk2 = judgeServerEntityService.save(new JudgeServer()
                    .setCpuCore(cpuNum)
                    .setIp(ip)
                    .setPort(port)
                    .setUrl(ip + ":" + port)
                    .setMaxTaskNumber(maxRemoteTaskNum)
                    .setIsRemote(true)
                    .setName(name));
        }

        if (!isOk1 || !isOk2) {
            log.error("初始化判题机信息到数据库失败，请重新启动试试！");
        } else {
            HashMap<String, Object> judgeServerInfo = judgeServerEntityService.getJudgeServerInfo();
            log.info("OJ-JudgeServer had successfully started! The judge config and sandbox config Info:" + judgeServerInfo);
        }

    }

}