/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.file.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusSystemErrorException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.common.result.ResultStatus;
import com.iuaenasong.oj.manager.file.ImportQDUOJProblemManager;
import com.iuaenasong.oj.service.file.ImportQDUOJProblemService;

@Service
public class ImportQDUOJProblemServiceImpl implements ImportQDUOJProblemService {

    @Autowired
    private ImportQDUOJProblemManager importQDUOJProblemManager;

    @Override
    public CommonResult<Void> importQDOJProblem(MultipartFile file) {
        try {
            importQDUOJProblemManager.importQDOJProblem(file);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusSystemErrorException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.SYSTEM_ERROR);
        }
    }
}