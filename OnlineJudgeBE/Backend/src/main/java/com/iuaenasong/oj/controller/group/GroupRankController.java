package com.iuaenasong.oj.controller.group;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.vo.OIRankVo;
import com.iuaenasong.oj.service.group.rank.GroupRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/group")
public class GroupRankController {

    @Autowired
    private GroupRankService groupRankService;

    @GetMapping("/get-rank-list")
    public CommonResult<IPage<OIRankVo>> getRankList(@RequestParam(value = "limit", required = false) Integer limit,
                                                     @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                     @RequestParam(value = "searchUser", required = false) String searchUser,
                                                     @RequestParam(value = "gid", required = true) Long gid,
                                                     @RequestParam(value = "type", required = true) Integer type) {
        return groupRankService.getGroupRankList(limit, currentPage, searchUser, type, gid);
    }
}
