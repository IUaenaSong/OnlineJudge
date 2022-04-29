/**
 * @Author LengYun
 * @Since 2022/04/29 11:57
 * @Description
 */

package com.iuaenasong.oj.service.group.rank.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.manager.group.rank.GroupRankManager;
import com.iuaenasong.oj.pojo.vo.OIRankVo;
import com.iuaenasong.oj.service.group.rank.GroupRankService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GroupRankServiceImpl implements GroupRankService {

    @Resource
    private GroupRankManager groupRankManager;

    @Override
    public CommonResult<IPage<OIRankVo>> getGroupRankList(Integer limit,
                                                          Integer currentPage,
                                                          String searchUser,
                                                          Integer type,
                                                          Long gid) {
        try {
            return CommonResult.successResponse(groupRankManager.getGroupRankList(limit, currentPage, searchUser, type, gid));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}
