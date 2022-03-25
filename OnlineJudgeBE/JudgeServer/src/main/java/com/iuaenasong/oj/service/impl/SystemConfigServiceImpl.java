/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.impl;

import cn.hutool.system.oshi.OshiUtil;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.service.SystemConfigService;

import java.util.HashMap;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    public HashMap<String, Object> getSystemConfig() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        int cpuCores = Runtime.getRuntime().availableProcessors(); // cpu核数

        double cpuLoad = 100 - OshiUtil.getCpuInfo().getFree();
        String percentCpuLoad = String.format("%.2f", cpuLoad) + "%"; // cpu使用率

        double totalVirtualMemory = OshiUtil.getMemory().getTotal(); // 总内存
        double freePhysicalMemorySize = OshiUtil.getMemory().getAvailable(); // 空闲内存
        double value = freePhysicalMemorySize / totalVirtualMemory;
        String percentMemoryLoad = String.format("%.2f", (1 - value) * 100) + "%"; // 内存使用率

        result.put("cpuCores", cpuCores);
        result.put("percentCpuLoad", percentCpuLoad);
        result.put("percentMemoryLoad", percentMemoryLoad);
        return result;
    }

}