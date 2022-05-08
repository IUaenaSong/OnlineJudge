/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iuaenasong.oj.pojo.entity.exam.ExamRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {

}
