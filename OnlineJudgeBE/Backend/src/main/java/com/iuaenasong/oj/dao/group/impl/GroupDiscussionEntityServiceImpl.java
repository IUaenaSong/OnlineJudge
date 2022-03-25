/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.group.impl;

import com.iuaenasong.oj.dao.group.GroupDiscussionEntityService;
import com.iuaenasong.oj.mapper.GroupDiscussionMapper;
import com.iuaenasong.oj.pojo.entity.discussion.Discussion;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class GroupDiscussionEntityServiceImpl extends ServiceImpl<GroupDiscussionMapper, Discussion> implements GroupDiscussionEntityService {
}
