/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.group;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.group.GroupMember;
import com.iuaenasong.oj.pojo.vo.GroupMemberVo;
import com.iuaenasong.oj.service.group.member.GroupMemberService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiresAuthentication
@RequestMapping("/api/group")
public class GroupMemberController {

    @Autowired
    private GroupMemberService groupMemberService;

    @GetMapping("/get-member-list")
    public CommonResult<IPage<GroupMemberVo>> getMemberList(@RequestParam(value = "limit", required = false) Integer limit,
                                                            @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                            @RequestParam(value = "keyword", required = false) String keyword,
                                                            @RequestParam(value = "auth", required = false) Integer auth,
                                                            @RequestParam(value = "gid", required = true) Long gid) {
        return groupMemberService.getMemberList(limit, currentPage, keyword, auth, gid);
    }

    @GetMapping("/get-apply-list")
    public CommonResult<IPage<GroupMemberVo>> getApplyList(@RequestParam(value = "limit", required = false) Integer limit,
                                                           @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                           @RequestParam(value = "keyword", required = false) String keyword,
                                                           @RequestParam(value = "auth", required = false) Integer auth,
                                                           @RequestParam(value = "gid", required = true) Long gid) {
        return groupMemberService.getApplyList(limit, currentPage, keyword, auth, gid);
    }

    @PostMapping("/member")
    @RequiresAuthentication
    public CommonResult<Void> addGroupMember(@RequestParam(value = "uid", required = true) String uid,
                                             @RequestParam(value = "gid", required = true) Long gid,
                                             @RequestParam(value = "code", required = false) String code,
                                             @RequestParam(value = "reason", required = false) String reason) {
        return groupMemberService.addMember(uid, gid, code, reason);
    }

    @PutMapping("/member")
    @RequiresAuthentication
    public CommonResult<Void> updateMember(@RequestBody GroupMember groupMember) {
        return groupMemberService.updateMember(groupMember);
    }

    @DeleteMapping("/member")
    @RequiresAuthentication
    public CommonResult<Void> deleteMember(@RequestParam(value = "uid", required = true) String uid,
                                                @RequestParam(value = "gid", required = true) Long gid) {
        return groupMemberService.deleteMember(uid, gid);
    }

}
