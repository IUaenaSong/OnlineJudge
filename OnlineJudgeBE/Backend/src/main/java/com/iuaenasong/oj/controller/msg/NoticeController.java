/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.msg;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.vo.SysMsgVo;
import com.iuaenasong.oj.service.msg.NoticeService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/msg")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    @RequestMapping(value = "/sys", method = RequestMethod.GET)
    @RequiresAuthentication
    public CommonResult<IPage<SysMsgVo>> getSysNotice(@RequestParam(value = "limit", required = false) Integer limit,
                                                      @RequestParam(value = "currentPage", required = false) Integer currentPage) {

        return noticeService.getSysNotice(limit, currentPage);
    }

    @RequestMapping(value = "/mine", method = RequestMethod.GET)
    @RequiresAuthentication
    public CommonResult<IPage<SysMsgVo>> getMineNotice(@RequestParam(value = "limit", required = false) Integer limit,
                                                       @RequestParam(value = "currentPage", required = false) Integer currentPage) {

        return noticeService.getMineNotice(limit, currentPage);
    }
}