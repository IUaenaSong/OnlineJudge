/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.group;

import com.iuaenasong.oj.pojo.entity.group.Group;
import com.iuaenasong.oj.pojo.vo.GroupVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface GroupEntityService extends IService<Group> {
    IPage<GroupVo> getGroupList(int limit, int currentPage, String keyword, Integer auth, String uid, Boolean onlyMine, Boolean isRoot);
}
