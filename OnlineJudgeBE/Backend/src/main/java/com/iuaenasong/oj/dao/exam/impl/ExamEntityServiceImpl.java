/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.dao.exam.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iuaenasong.oj.dao.exam.ExamEntityService;
import com.iuaenasong.oj.mapper.ExamMapper;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.vo.ExamRegisterCountVo;
import com.iuaenasong.oj.pojo.vo.ExamVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamEntityServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamEntityService {

    @Autowired
    private ExamMapper examMapper;

    @Override
    public ExamVo getExamInfoById(long eid) {
        List<Long> eidList = Collections.singletonList(eid);
        ExamVo examVo = examMapper.getExamInfoById(eid);
        if (examVo != null) {
            List<ExamRegisterCountVo> examRegisterCountVoList = examMapper.getExamRegisterCount(eidList);
            if(!CollectionUtils.isEmpty(examRegisterCountVoList)) {
                ExamRegisterCountVo examRegisterCountVo = examRegisterCountVoList.get(0);
                examVo.setCount(examRegisterCountVo.getCount());
            }
        }
        return examVo;
    }

}
