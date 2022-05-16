/**
 * @Author LengYun
 * @Since 2022/05/14 10:43
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwitchConfigDto {

    private Boolean openPublicDiscussion;

    private Boolean openGroupDiscussion;

    private Boolean openContestComment;

    private Boolean openPublicJudge;

    private Boolean openGroupJudge;

    private Boolean openContestJudge;

    private Integer defaultSubmitInterval;

    private Integer defaultCreateGroupDailyLimit;

    private Integer defaultCreateGroupLimit;

    private Integer defaultCreateGroupACInitValue;

    private Integer defaultCreateDiscussionDailyLimit;

    private Integer defaultCreateDiscussionACInitValue;

    private Integer defaultCreateCommentACInitValue;

    private List<String> hduUsernameList;

    private List<String> hduPasswordList;

    private List<String> cfUsernameList;

    private List<String> cfPasswordList;

    private List<String> pojUsernameList;

    private List<String> pojPasswordList;

    private List<String> atcoderUsernameList;

    private List<String> atcoderPasswordList;

    private List<String> spojUsernameList;

    private List<String> spojPasswordList;
}
