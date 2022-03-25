/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import com.iuaenasong.oj.pojo.entity.discussion.Reply;

@Data
@Accessors(chain = true)
public class ReplyDto {

    private Reply reply;

    private Integer did;

    private Integer quoteId;

    private String quoteType;
}