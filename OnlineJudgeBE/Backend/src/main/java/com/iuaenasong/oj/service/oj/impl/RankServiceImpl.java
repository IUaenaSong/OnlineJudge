/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.manager.oj.RankManager;
import com.iuaenasong.oj.service.oj.RankService;

import javax.annotation.Resource;

@Service
public class RankServiceImpl implements RankService {

    @Resource
    private RankManager rankManager;

    @Override
    public CommonResult<IPage> getRankList(Integer limit, Integer currentPage, String searchUser, Integer type) {
        try {
            return CommonResult.successResponse(rankManager.getRankList(limit, currentPage, searchUser, type));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}