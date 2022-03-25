/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.discussion;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.discussion.Discussion;
import com.iuaenasong.oj.pojo.entity.discussion.DiscussionReport;

import java.util.List;

public interface AdminDiscussionService {

    public CommonResult<Void> updateDiscussion(Discussion discussion);

    public CommonResult<Void> removeDiscussion(List<Integer> didList);

    public CommonResult<IPage<DiscussionReport>> getDiscussionReport(Integer limit, Integer currentPage);

    public CommonResult<Void> updateDiscussionReport(DiscussionReport discussionReport);
}