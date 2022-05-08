/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Accessors(chain = true)
public class SubmitQuestionDto implements Serializable {

    @NotBlank(message = "考试号不能为空")
    private Long eid;

    @NotBlank(message = "问题号不能为空")
    private String displayId;

    @NotBlank
    private String answer;
}