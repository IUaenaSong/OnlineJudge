/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.msg;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.entity.msg.MsgRemind;
import com.iuaenasong.oj.pojo.vo.UserMsgVo;
import com.iuaenasong.oj.pojo.vo.UserUnreadMsgCountVo;

public interface MsgRemindEntityService extends IService<MsgRemind> {

    UserUnreadMsgCountVo getUserUnreadMsgCount(String uid);

    IPage<UserMsgVo> getUserMsg(Page<UserMsgVo> page, String uid,String action);
}