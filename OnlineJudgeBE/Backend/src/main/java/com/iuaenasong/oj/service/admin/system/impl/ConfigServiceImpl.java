/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.system.impl;

import cn.hutool.json.JSONObject;
import com.iuaenasong.oj.pojo.dto.*;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.manager.admin.system.ConfigManager;
import com.iuaenasong.oj.service.admin.system.ConfigService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ConfigManager configManager;

    @Override
    public CommonResult<JSONObject> getServiceInfo() {
        return CommonResult.successResponse(configManager.getServiceInfo());
    }

    @Override
    public CommonResult<List<JSONObject>> getJudgeServiceInfo() {
        return CommonResult.successResponse(configManager.getJudgeServiceInfo());
    }

    @Override
    public CommonResult<Void> deleteHomeCarousel(Long id) {
        try {
            configManager.deleteHomeCarousel(id);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<WebConfigDto> getWebConfig() {
        return CommonResult.successResponse(configManager.getWebConfig());
    }

    @Override
    public CommonResult<Void> setWebConfig(WebConfigDto webConfigDto) {
        try {
            configManager.setWebConfig(webConfigDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<EmailConfigDto> getEmailConfig() {
        return CommonResult.successResponse(configManager.getEmailConfig());
    }

    @Override
    public CommonResult<Void> setEmailConfig(EmailConfigDto emailConfigDto) {
        try {
            configManager.setEmailConfig(emailConfigDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> testEmail(TestEmailDto testEmailDto) {
        try {
            configManager.testEmail(testEmailDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<MobileConfigDto> getMobileConfig() {
        return CommonResult.successResponse(configManager.getMobileConfig());
    }

    @Override
    public CommonResult<Void> setMobileConfig(MobileConfigDto mobileConfigDto) {
        try {
            configManager.setMobileConfig(mobileConfigDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> testMobile(TestMobileDto testMobileDto) {
        try {
            configManager.testMobile(testMobileDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<DBAndRedisConfigDto> getDBAndRedisConfig() {
        return CommonResult.successResponse(configManager.getDBAndRedisConfig());
    }

    @Override
    public CommonResult<Void> setDBAndRedisConfig(DBAndRedisConfigDto dbAndRedisConfigDto) {
        try {
            configManager.setDBAndRedisConfig(dbAndRedisConfigDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<SwitchConfigDto> getSwitchConfig() {
        return CommonResult.successResponse(configManager.getSwitchConfig());
    }

    @Override
    public CommonResult<Void> setSwitchConfig(SwitchConfigDto config) {
        try {
            configManager.setSwitchConfig(config);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}