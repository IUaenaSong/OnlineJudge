/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.msg;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.msg.AdminSysNotice;
import com.iuaenasong.oj.pojo.vo.AdminSysNoticeVo;

public interface AdminNoticeService {

    public CommonResult<IPage<AdminSysNoticeVo>> getSysNotice(Integer limit, Integer currentPage, String type);

    public CommonResult<Void> addSysNotice(AdminSysNotice adminSysNotice);

    public CommonResult<Void> deleteSysNotice(Long id);

    public CommonResult<Void> updateSysNotice(AdminSysNotice adminSysNotice);
}
