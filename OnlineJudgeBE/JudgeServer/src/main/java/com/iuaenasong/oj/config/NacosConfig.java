/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.iuaenasong.oj.utils.IpUtils;

import java.util.HashMap;

@Configuration
public class NacosConfig {

    private static final int cpuNum = Runtime.getRuntime().availableProcessors();

    @Value("${oj-judge-server.max-task-num}")
    private Integer maxTaskNum;

    @Value("${oj-judge-server.remote-judge.max-task-num}")
    private Integer maxRemoteTaskNum;

    @Value("${oj-judge-server.remote-judge.open}")
    private Boolean openRemoteJudge;

    @Value("${oj-judge-server.ip}")
    private String ip;

    @Value("${oj-judge-server.port}")
    private Integer port;

    @Value("${oj-judge-server.name}")
    private String name;

    
    @Bean
    @Primary
    public NacosDiscoveryProperties nacosProperties() {
        NacosDiscoveryProperties nacosDiscoveryProperties = new NacosDiscoveryProperties();
        //此处我只改了ip，其他参数可以根据自己的需求改变
        nacosDiscoveryProperties.setIp(IpUtils.getServiceIp());
        HashMap<String, String> meta = new HashMap<>();
        int max = cpuNum * 2 + 1;
        if (maxTaskNum != -1) {
            max = maxTaskNum;
        }
        meta.put("maxTaskNum", String.valueOf(max));
        if (openRemoteJudge) {
            max = (cpuNum * 2 + 1) * 2;
            if (maxRemoteTaskNum != -1) {
                max = maxRemoteTaskNum;
            }
            meta.put("maxRemoteTaskNum", String.valueOf(max));
        }
        meta.put("judgeName", name);
        nacosDiscoveryProperties.setMetadata(meta);
        if (!ip.equals("-1")) {
            nacosDiscoveryProperties.setIp(ip);
        }
        nacosDiscoveryProperties.setPort(port);

        nacosDiscoveryProperties.setService("oj-judge-server");
        return nacosDiscoveryProperties;
    }

}