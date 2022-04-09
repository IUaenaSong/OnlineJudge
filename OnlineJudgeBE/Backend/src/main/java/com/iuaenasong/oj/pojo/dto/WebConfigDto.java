/**
 * @Author LengYun
 * @Since 2022/04/09 09:58
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@ApiModel(value = "WebConfig", description = "")
public class WebConfigDto {

    private String baseUrl;

    private String name;

    private String shortName;

    private String description;

    private Boolean register;

    private String recordName;

    private String recordUrl;

    private String projectName;

    private String projectUrl;
}
