/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.discussion.Discussion;
import com.iuaenasong.oj.pojo.entity.discussion.DiscussionReport;
import com.iuaenasong.oj.pojo.entity.problem.Category;
import com.iuaenasong.oj.pojo.vo.DiscussionVo;

import java.util.List;

public interface DiscussionService {

    public CommonResult<IPage<Discussion>> getDiscussionList(Integer limit,
                                                            Integer currentPage,
                                                            Integer categoryId,
                                                            String pid,
                                                            Boolean onlyMine,
                                                            String keyword,
                                                            Boolean admin);

    public CommonResult<DiscussionVo>  getDiscussion(Integer did);

    public CommonResult<Void>  addDiscussion(Discussion discussion);

    public CommonResult<Void>  updateDiscussion(Discussion discussion);

    public CommonResult<Void>  removeDiscussion(Integer did);

    public CommonResult<Void>  addDiscussionLike(Integer did, Boolean toLike);

    public CommonResult<List<Category>>  getDiscussionCategory();

    public CommonResult<Void>  addDiscussionReport(DiscussionReport discussionReport);

}
