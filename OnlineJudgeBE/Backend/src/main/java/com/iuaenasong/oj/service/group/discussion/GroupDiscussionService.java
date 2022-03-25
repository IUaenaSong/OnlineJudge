/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.group.discussion;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.discussion.Discussion;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface GroupDiscussionService {

    public CommonResult<IPage<Discussion>> getDiscussionList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<IPage<Discussion>> getAdminDiscussionList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<Void> addDiscussion(Discussion discussion);

    public CommonResult<Void> updateDiscussion(Discussion discussion);

    public CommonResult<Void> deleteDiscussion(Long did);

}
