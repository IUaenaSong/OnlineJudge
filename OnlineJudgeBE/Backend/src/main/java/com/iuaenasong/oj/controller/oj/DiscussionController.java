/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.problem.Category;
import com.iuaenasong.oj.pojo.entity.discussion.Discussion;
import com.iuaenasong.oj.pojo.entity.discussion.DiscussionReport;
import com.iuaenasong.oj.pojo.vo.DiscussionVo;
import com.iuaenasong.oj.service.oj.DiscussionService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    @GetMapping("/get-discussion-list")
    public CommonResult<IPage<Discussion>> getDiscussionList(@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                                             @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                                             @RequestParam(value = "cid", required = false) Integer categoryId,
                                                             @RequestParam(value = "pid", required = false) String pid,
                                                             @RequestParam(value = "onlyMine", required = false, defaultValue = "false") Boolean onlyMine,
                                                             @RequestParam(value = "keyword", required = false) String keyword,
                                                             @RequestParam(value = "admin", defaultValue = "false") Boolean admin) {

        return discussionService.getDiscussionList(limit, currentPage, categoryId, pid, onlyMine, keyword, admin);

    }

    @GetMapping("/get-discussion-detail")
    public CommonResult<DiscussionVo> getDiscussion(@RequestParam(value = "did", required = true) Integer did) {
        return discussionService.getDiscussion(did);
    }

    @PostMapping("/discussion")
    @RequiresPermissions("discussion_add")
    @RequiresAuthentication
    public CommonResult<Void> addDiscussion(@RequestBody Discussion discussion) {
        return discussionService.addDiscussion(discussion);
    }

    @PutMapping("/discussion")
    @RequiresPermissions("discussion_edit")
    @RequiresAuthentication
    public CommonResult<Void> updateDiscussion(@RequestBody Discussion discussion) {
        return discussionService.updateDiscussion(discussion);
    }

    @DeleteMapping("/discussion")
    @RequiresPermissions("discussion_del")
    @RequiresAuthentication
    public CommonResult<Void> removeDiscussion(@RequestParam("did") Integer did) {
        return discussionService.removeDiscussion(did);
    }

    @GetMapping("/discussion-like")
    @RequiresAuthentication
    public CommonResult<Void> addDiscussionLike(@RequestParam("did") Integer did,
                                                @RequestParam("toLike") Boolean toLike) {
        return discussionService.addDiscussionLike(did, toLike);
    }

    @GetMapping("/discussion-category")
    public CommonResult<List<Category>> getDiscussionCategory() {
        return discussionService.getDiscussionCategory();
    }

    @PostMapping("/discussion-report")
    @RequiresAuthentication
    public CommonResult<Void> addDiscussionReport(@RequestBody DiscussionReport discussionReport) {
        return discussionService.addDiscussionReport(discussionReport);
    }

}