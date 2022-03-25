/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.HashMap;

@Data
public class CommentListVo {

    private IPage<CommentVo> commentList;

    private HashMap<Integer, Boolean>  commentLikeMap;
}