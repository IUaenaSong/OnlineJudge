/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.group;

import com.iuaenasong.oj.annotation.OJAccess;
import com.iuaenasong.oj.annotation.OJAccessEnum;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.discussion.Discussion;
import com.iuaenasong.oj.service.group.discussion.GroupDiscussionService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiresAuthentication
@RequestMapping("/api/group")
@OJAccess({OJAccessEnum.GROUP_DISCUSSION})
public class GroupDiscussionController {

    @Autowired
    private GroupDiscussionService groupDiscussionService;

    @GetMapping("/get-discussion-list")
    public CommonResult<IPage<Discussion>> getDiscussionList(@RequestParam(value = "limit", required = false) Integer limit,
                                                             @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                             @RequestParam(value = "gid", required = true) Long gid) {
        return groupDiscussionService.getDiscussionList(limit, currentPage, gid);
    }

    @GetMapping("/get-admin-discussion-list")
    public CommonResult<IPage<Discussion>> getAdminDiscussionList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                  @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                  @RequestParam(value = "gid", required = true) Long gid) {
        return groupDiscussionService.getAdminDiscussionList(limit, currentPage, gid);
    }

    @PostMapping("/discussion")
    public CommonResult<Void> addDiscussion(@RequestBody Discussion discussion) {
        return groupDiscussionService.addDiscussion(discussion);
    }

    @PutMapping("/discussion")
    public CommonResult<Void> updateDiscussion(@RequestBody Discussion discussion) {
        return groupDiscussionService.updateDiscussion(discussion);
    }

    @DeleteMapping("/discussion")
    public CommonResult<Void> deleteDiscussion(@RequestParam(value = "did", required = true) Long did) {
        return groupDiscussionService.deleteDiscussion(did);
    }

}
