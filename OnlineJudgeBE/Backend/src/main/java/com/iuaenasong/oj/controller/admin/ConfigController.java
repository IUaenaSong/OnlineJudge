/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.admin;

import cn.hutool.json.JSONObject;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.service.admin.system.ConfigService;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    
    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    @RequestMapping("/get-service-info")
    public CommonResult<JSONObject> getServiceInfo() {
        return configService.getServiceInfo();
    }

    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    @RequestMapping("/get-judge-service-info")
    public CommonResult<List<JSONObject>> getJudgeServiceInfo() {
        return configService.getJudgeServiceInfo();
    }

    @RequiresPermissions("system_info_admin")
    @RequestMapping("/get-web-config")
    public CommonResult<Map<Object,Object>> getWebConfig() {
        return configService.getWebConfig();
    }

    @RequiresPermissions("system_info_admin")
    @DeleteMapping("/home-carousel")
    public CommonResult<Void> deleteHomeCarousel(@RequestParam("id") Long id) {

        return configService.deleteHomeCarousel(id);
    }

    @RequiresPermissions("system_info_admin")
    @RequestMapping(value = "/set-web-config", method = RequestMethod.PUT)
    public CommonResult<Void> setWebConfig(@RequestBody HashMap<String, Object> params) {

        return configService.setWebConfig(params);
    }

    @RequiresPermissions("system_info_admin")
    @RequestMapping("/get-email-config")
    public CommonResult<Map<Object,Object>> getEmailConfig() {

        return configService.getEmailConfig();
    }

    @RequiresPermissions("system_info_admin")
    @PutMapping("/set-email-config")
    public CommonResult<Void> setEmailConfig(@RequestBody HashMap<String, Object> params) {

        return configService.setEmailConfig(params);
    }

    @RequiresPermissions("system_info_admin")
    @PostMapping("/test-email")
    public CommonResult<Void> testEmail(@RequestBody HashMap<String, Object> params) throws MessagingException {

        return configService.testEmail(params);
    }

    @RequiresPermissions("system_info_admin")
    @RequestMapping("/get-mobile-config")
    public CommonResult<Map<Object,Object>> getMobileConfig() {

        return configService.getMobileConfig();
    }

    @RequiresPermissions("system_info_admin")
    @PutMapping("/set-mobile-config")
    public CommonResult<Void> setMobileConfig(@RequestBody HashMap<String, Object> params) {

        return configService.setMobileConfig(params);
    }

    @RequiresPermissions("system_info_admin")
    @PostMapping("/test-mobile")
    public CommonResult<Void> testMobile(@RequestBody HashMap<String, Object> params) throws MessagingException {

        return configService.testMobile(params);
    }

    @RequiresPermissions("system_info_admin")
    @RequestMapping("/get-db-and-redis-config")
    public CommonResult<Map<Object,Object>> getDBAndRedisConfig() {

        return configService.getDBAndRedisConfig();
    }

    @RequiresPermissions("system_info_admin")
    @PutMapping("/set-db-and-redis-config")
    public CommonResult<Void> setDBAndRedisConfig(@RequestBody HashMap<String, Object> params) {
        return configService.setDBAndRedisConfig(params);
    }

}