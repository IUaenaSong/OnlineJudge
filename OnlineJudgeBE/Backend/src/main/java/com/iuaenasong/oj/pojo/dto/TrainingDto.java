/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import com.iuaenasong.oj.pojo.entity.training.Training;
import com.iuaenasong.oj.pojo.entity.training.TrainingCategory;

@Data
@Accessors(chain = true)
public class TrainingDto {

    private Training training;

    private TrainingCategory trainingCategory;
}