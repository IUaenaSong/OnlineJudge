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
@ApiModel(value="DBAndRedisConfig", description="")
public class DBAndRedisConfigDto {

    private String dbName;

    private String dbHost;

    private Integer dbPort;

    private String dbUsername;

    private String dbPassword ;

    private String redisHost;

    private Integer redisPort;

    private String redisPassword ;
}