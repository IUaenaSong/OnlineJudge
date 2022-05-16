/**
 * @Author LengYun
 * @Since 2022/05/14 10:43
 * @Description
 */

package com.iuaenasong.oj.controller.admin;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.SwitchConfigDto;
import com.iuaenasong.oj.service.admin.system.ConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/admin/switch")
public class SwitchController {

    @Resource
    private ConfigService configService;

    @RequiresPermissions("system_info_admin")
    @RequestMapping("/info")
    public CommonResult<SwitchConfigDto> getSwitchConfig() {

        return configService.getSwitchConfig();
    }

    @RequiresPermissions("system_info_admin")
    @PutMapping("/update")
    public CommonResult<Void> setSwitchConfig(@RequestBody SwitchConfigDto config) {
        return configService.setSwitchConfig(config);
    }
}