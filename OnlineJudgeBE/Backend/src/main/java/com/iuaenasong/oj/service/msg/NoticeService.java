/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.msg;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.vo.SysMsgVo;

public interface NoticeService {

    public CommonResult<IPage<SysMsgVo>> getSysNotice(Integer limit,Integer currentPage);

    public CommonResult<IPage<SysMsgVo>> getMineNotice(Integer limit, Integer currentPage);
}
