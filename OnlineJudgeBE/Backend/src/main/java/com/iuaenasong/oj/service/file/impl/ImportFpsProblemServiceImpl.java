/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.file.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.manager.file.ImportFpsProblemManager;
import com.iuaenasong.oj.service.file.ImportFpsProblemService;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class ImportFpsProblemServiceImpl implements ImportFpsProblemService {

    @Resource
    private ImportFpsProblemManager importFpsProblemManager;

    @Override
    public CommonResult<Void> importFPSProblem(MultipartFile file) {
        try {
            importFpsProblemManager.importFPSProblem(file);
            return CommonResult.successResponse();
        } catch (IOException | StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}