/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.file;

import org.springframework.web.multipart.MultipartFile;
import com.iuaenasong.oj.common.result.CommonResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ProblemFileService {

    public CommonResult<Void> importProblem(MultipartFile file);

    public void exportProblem(List<Long> pidList, HttpServletResponse response);
}
