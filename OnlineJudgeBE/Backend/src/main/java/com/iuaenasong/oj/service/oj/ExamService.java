/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.service.oj;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.vo.*;

public interface ExamService {

    public CommonResult<ExamVo> getExamInfo(Long eid);

    public CommonResult<AccessVo> getExamAccess(Long eid);

}
