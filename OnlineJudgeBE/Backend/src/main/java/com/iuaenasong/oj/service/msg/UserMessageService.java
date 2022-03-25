/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.msg;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.vo.UserMsgVo;
import com.iuaenasong.oj.pojo.vo.UserUnreadMsgCountVo;

public interface UserMessageService {

    public CommonResult<UserUnreadMsgCountVo> getUnreadMsgCount();

    public CommonResult<Void> cleanMsg(String type, Long id);

    public CommonResult<IPage<UserMsgVo>> getCommentMsg( Integer limit,Integer currentPage);

    public CommonResult<IPage<UserMsgVo>> getReplyMsg(Integer limit, Integer currentPage);

    public CommonResult<IPage<UserMsgVo>> getLikeMsg(Integer limit, Integer currentPage);

}
