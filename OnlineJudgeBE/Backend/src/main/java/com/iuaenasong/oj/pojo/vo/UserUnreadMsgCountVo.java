/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value="用户未读消息统计", description="")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUnreadMsgCountVo {

    @ApiModelProperty(value = "未读评论")
    private Integer comment;

    @ApiModelProperty(value = "未读回复")
    private Integer reply;

    @ApiModelProperty(value = "未读点赞")
    private Integer like;

    @ApiModelProperty(value = "未读系统通知")
    private Integer sys;

    @ApiModelProperty(value = "未读我的消息")
    private Integer mine;
}