/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.judge;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.iuaenasong.oj.mapper.RemoteJudgeAccountMapper;
import com.iuaenasong.oj.dao.judge.JudgeServerEntityService;
import com.iuaenasong.oj.pojo.entity.judge.JudgeServer;
import com.iuaenasong.oj.pojo.entity.judge.RemoteJudgeAccount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j(topic = "oj")
public class ChooseUtils {

    @Autowired
    private NacosDiscoveryProperties discoveryProperties;

    @Value("${service-url.name}")
    private String JudgeServiceName;

    @Autowired
    private JudgeServerEntityService judgeServerEntityService;

    @Autowired
    private RemoteJudgeAccountMapper remoteJudgeAccountMapper;

    
    @Transactional(rollbackFor = Exception.class)
    public JudgeServer chooseServer(Boolean isRemote) {
        // 获取该微服务的所有健康实例
        List<Instance> instances = getInstances(JudgeServiceName);
        if (instances.size() <= 0) {
            return null;
        }
        List<String> keyList = new ArrayList<>();
        // 获取当前健康实例取出ip和port拼接
        for (Instance instance : instances) {
            keyList.add(instance.getIp() + ":" + instance.getPort());
        }

        // 过滤出小于或等于规定最大并发判题任务数的服务实例且健康的判题机
        QueryWrapper<JudgeServer> judgeServerQueryWrapper = new QueryWrapper<>();
        judgeServerQueryWrapper
                .in("url", keyList)
                .eq("is_remote", isRemote)
                .orderByAsc("task_number")
                .last("for update"); // 开启悲观锁

        
        List<JudgeServer> judgeServerList = judgeServerEntityService.list(judgeServerQueryWrapper);

        // 获取可用判题机
        for (JudgeServer judgeServer : judgeServerList) {
            if (judgeServer.getTaskNumber() < judgeServer.getMaxTaskNumber()) {
                judgeServer.setTaskNumber(judgeServer.getTaskNumber() + 1);
                boolean isOk = judgeServerEntityService.updateById(judgeServer);
                if (isOk) {
                    return judgeServer;
                }
            }
        }

        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public JudgeServer chooseFixedServer(Boolean isRemote, String fixedTag, Integer index, Integer total) {
        // 获取该微服务的所有健康实例
        List<Instance> instances = getInstances(JudgeServiceName);
        if (instances.size() <= 0) {
            return null;
        }
        List<String> keyList = new ArrayList<>();
        // 获取当前健康实例取出ip和port拼接
        for (Instance instance : instances) {
            keyList.add(instance.getIp() + ":" + instance.getPort());
        }

        // 过滤出小于或等于规定最大并发判题任务数的服务实例且健康的判题机
        QueryWrapper<JudgeServer> judgeServerQueryWrapper = new QueryWrapper<>();
        judgeServerQueryWrapper
                .in("url", keyList)
                .eq("is_remote", isRemote)
                .last("for update"); // 开启悲观锁

        
        List<JudgeServer> judgeServerList = judgeServerEntityService.list(judgeServerQueryWrapper);
        // CF的VJ判題需要一机一题(根据序号保持一定的固定)
        int len = judgeServerList.size();
        for (int i = 0; i < len; i++) {
            if (i % total == index) {
                JudgeServer judgeServer = judgeServerList.get(i);
                UpdateWrapper<JudgeServer> judgeServerUpdateWrapper = new UpdateWrapper<>();
                judgeServerUpdateWrapper.set(fixedTag, false)
                        .eq("id", judgeServer.getId())
                        .eq(fixedTag, true);
                boolean isOk = judgeServerEntityService.update(judgeServerUpdateWrapper);
                if (isOk) {
                    return judgeServer;
                }
            }
        }
        return null;
    }

    
    private List<Instance> getInstances(String serviceId) {
        // 获取服务发现的相关API
        NamingService namingService = discoveryProperties.namingServiceInstance();
        try {
            // 获取该微服务的所有健康实例
            return namingService.selectInstances(serviceId, true);
        } catch (NacosException e) {
            log.error("获取微服务健康实例发生异常--------->{}", e);
            return Collections.emptyList();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public RemoteJudgeAccount chooseRemoteAccount(String remoteOJAccountType, String username, Boolean isNeedAccountRejudge) {

        // 过滤出当前远程oj可用的账号列表 悲观锁
        List<RemoteJudgeAccount> remoteJudgeAccountList = remoteJudgeAccountMapper.getAvailableAccount(remoteOJAccountType);

        for (RemoteJudgeAccount remoteJudgeAccount : remoteJudgeAccountList) {
            // POJ已有submitId的重判需要使用原来的账号获取结果
            if (isNeedAccountRejudge) {
                if (remoteJudgeAccount.getUsername().equals(username)) {
                    int count = remoteJudgeAccountMapper.updateAccountStatusById(remoteJudgeAccount.getId());
                    if (count > 0) {
                        return remoteJudgeAccount;
                    }
                }
            } else {
                int count = remoteJudgeAccountMapper.updateAccountStatusById(remoteJudgeAccount.getId());
                if (count > 0) {
                    return remoteJudgeAccount;
                }
            }
        }

        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public HashMap<String, Object> chooseFixedAccount(String remoteOJAccountType) {
        List<Instance> instances = getInstances(JudgeServiceName);
        // 过滤出当前远程可用的账号列表 悲观锁
        QueryWrapper<RemoteJudgeAccount> remoteJudgeAccountQueryWrapper = new QueryWrapper<>();
        remoteJudgeAccountQueryWrapper.eq("oj", remoteOJAccountType)
                .last("for update");

        List<RemoteJudgeAccount> remoteJudgeAccountList = remoteJudgeAccountMapper.selectList(remoteJudgeAccountQueryWrapper);
        int len = remoteJudgeAccountList.size();
        for (int i = 0; i < len && i < instances.size(); i++) {
            RemoteJudgeAccount remoteJudgeAccount = remoteJudgeAccountList.get(i);
            int count = remoteJudgeAccountMapper.updateAccountStatusById(remoteJudgeAccount.getId());
            if (count > 0) {
                HashMap<String, Object> result = new HashMap<>();
                result.put("index", i);
                result.put("size", len);
                result.put("account", remoteJudgeAccount);
                return result;
            }
        }
        return null;
    }

}