/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.problem.impl;

import com.iuaenasong.oj.pojo.entity.problem.Tag;
import com.iuaenasong.oj.mapper.TagMapper;
import com.iuaenasong.oj.dao.problem.TagEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TagEntityServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagEntityService {

}
