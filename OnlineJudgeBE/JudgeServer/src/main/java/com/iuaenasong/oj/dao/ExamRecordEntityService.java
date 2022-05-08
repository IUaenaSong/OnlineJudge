/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.entity.exam.ExamRecord;

public interface ExamRecordEntityService extends IService<ExamRecord> {
    void UpdateExamRecord(Integer score, Integer status, Long submitId, Integer useTime);
}
