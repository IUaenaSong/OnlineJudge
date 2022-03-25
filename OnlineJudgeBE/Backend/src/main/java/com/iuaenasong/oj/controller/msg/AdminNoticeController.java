/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.msg;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.msg.AdminSysNotice;
import com.iuaenasong.oj.pojo.vo.AdminSysNoticeVo;
import com.iuaenasong.oj.service.msg.AdminNoticeService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/admin/msg")
public class AdminNoticeController {

    @Resource
    private AdminNoticeService adminNoticeService;

    @GetMapping("/notice")
    @RequiresAuthentication
    @RequiresRoles("root")
    public CommonResult<IPage<AdminSysNoticeVo>> getSysNotice(@RequestParam(value = "limit", required = false) Integer limit,
                                                              @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                              @RequestParam(value = "type", required = false) String type) {

        return adminNoticeService.getSysNotice(limit, currentPage, type);
    }

    @PostMapping("/notice")
    @RequiresAuthentication
    @RequiresRoles("root")
    public CommonResult<Void> addSysNotice(@RequestBody AdminSysNotice adminSysNotice) {

        return adminNoticeService.addSysNotice(adminSysNotice);
    }

    @DeleteMapping("/notice")
    @RequiresAuthentication
    @RequiresRoles("root")
    public CommonResult<Void> deleteSysNotice(@RequestParam("id") Long id) {

        return adminNoticeService.deleteSysNotice(id);
    }

    @PutMapping("/notice")
    @RequiresAuthentication
    @RequiresRoles("root")
    public CommonResult<Void> updateSysNotice(@RequestBody AdminSysNotice adminSysNotice) {

        return adminNoticeService.updateSysNotice(adminSysNotice);
    }
}