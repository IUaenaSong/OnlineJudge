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
import com.iuaenasong.oj.pojo.vo.UserMsgVo;
import com.iuaenasong.oj.pojo.vo.UserUnreadMsgCountVo;
import com.iuaenasong.oj.service.msg.UserMessageService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/msg")
public class UserMessageController {

    @Resource
    private UserMessageService userMessageService;

    
    @RequestMapping(value = "/unread", method = RequestMethod.GET)
    @RequiresAuthentication
    public CommonResult<UserUnreadMsgCountVo> getUnreadMsgCount() {
        return userMessageService.getUnreadMsgCount();
    }

    
    @RequestMapping(value = "/clean", method = RequestMethod.DELETE)
    @RequiresAuthentication
    public CommonResult<Void> cleanMsg(@RequestParam("type") String type,
                                       @RequestParam(value = "id", required = false) Long id) {
        return userMessageService.cleanMsg(type, id);
    }

    
    @RequestMapping(value = "/comment", method = RequestMethod.GET)
    @RequiresAuthentication
    public CommonResult<IPage<UserMsgVo>> getCommentMsg(@RequestParam(value = "limit", required = false) Integer limit,
                                                        @RequestParam(value = "currentPage", required = false) Integer currentPage) {
        return userMessageService.getCommentMsg(limit, currentPage);
    }

    
    @RequestMapping(value = "/reply", method = RequestMethod.GET)
    @RequiresAuthentication
    public CommonResult<IPage<UserMsgVo>> getReplyMsg(@RequestParam(value = "limit", required = false) Integer limit,
                                                      @RequestParam(value = "currentPage", required = false) Integer currentPage) {
        return userMessageService.getReplyMsg(limit, currentPage);
    }

    
    @RequestMapping(value = "/like", method = RequestMethod.GET)
    @RequiresAuthentication
    public CommonResult<IPage<UserMsgVo>> getLikeMsg(@RequestParam(value = "limit", required = false) Integer limit,
                                                     @RequestParam(value = "currentPage", required = false) Integer currentPage) {
        return userMessageService.getLikeMsg(limit, currentPage);
    }

}