/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.tag;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.problem.Tag;

public interface AdminTagService {

    public CommonResult<Tag> addProblem(Tag tag);

    public CommonResult<Void> updateTag(Tag tag);

    public CommonResult<Void> deleteTag(Long tid);
}
