/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.file;

import org.springframework.web.multipart.MultipartFile;
import com.iuaenasong.oj.common.result.CommonResult;

public interface ImportQDUOJProblemService {

    public CommonResult<Void> importQDOJProblem(MultipartFile file);
}
