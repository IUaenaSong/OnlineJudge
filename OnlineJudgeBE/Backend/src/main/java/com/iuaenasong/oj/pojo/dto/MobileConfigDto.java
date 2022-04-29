/**
 * @Author LengYun
 * @Since 2022/04/09 09:58
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@ApiModel(value="MobileConfig", description="")
public class MobileConfigDto {

    private String mobileDomain;

    private String mobileRegionId;

    private String mobileAccessKeyId;

    private String mobileSecret;

    private String mobileSignName;

    private String mobileTemplateCode;
}