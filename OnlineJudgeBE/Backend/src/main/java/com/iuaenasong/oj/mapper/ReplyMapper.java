/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.mapper;

import com.iuaenasong.oj.pojo.vo.ReplyVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.iuaenasong.oj.pojo.entity.discussion.Reply;

import java.util.List;

@Mapper
@Repository
public interface ReplyMapper extends BaseMapper<Reply> {
    public List<ReplyVo> getAllReplyByCommentId(@Param("commentId") Integer commentId,
                                                @Param("myAndAdminUidList") List<String> myAndAdminUidList);
}