/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.dao.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iuaenasong.oj.dao.ExamRecordEntityService;
import com.iuaenasong.oj.mapper.ExamRecordMapper;
import com.iuaenasong.oj.pojo.entity.exam.ExamRecord;
import com.iuaenasong.oj.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ExamRecordEntityServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordEntityService {

    @Autowired
    private ExamRecordMapper examRecordMapper;

    private static List<Integer> penaltyStatus = Arrays.asList(
            Constants.Judge.STATUS_PARTIAL_ACCEPTED.getStatus(),
            Constants.Judge.STATUS_PRESENTATION_ERROR.getStatus(),
            Constants.Judge.STATUS_WRONG_ANSWER.getStatus(),
            Constants.Judge.STATUS_TIME_LIMIT_EXCEEDED.getStatus(),
            Constants.Judge.STATUS_MEMORY_LIMIT_EXCEEDED.getStatus(),
            Constants.Judge.STATUS_RUNTIME_ERROR.getStatus());

    @Override
    public void UpdateExamRecord(Integer score, Integer status, Long submitId, Integer useTime) {
        UpdateWrapper<ExamRecord> updateWrapper = new UpdateWrapper<>();
        // 如果是AC
        if (status.intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus()) {
            updateWrapper.set("status", Constants.Exam.RECORD_AC.getCode());
            // 编译错误
        } else if (status.intValue() == Constants.Judge.STATUS_COMPILE_ERROR.getStatus()) {
            updateWrapper.set("status", Constants.Exam.RECORD_NOT_AC_NOT_PENALTY.getCode());
            // 需要被罚时的状态
        } else if (penaltyStatus.contains(status)) {
            updateWrapper.set("status", Constants.Exam.RECORD_NOT_AC_PENALTY.getCode());

        } else {
            updateWrapper.set("status", Constants.Exam.RECORD_NOT_AC_NOT_PENALTY.getCode());
        }

        if (score != null) {
            updateWrapper.set("score", score);
        }

        updateWrapper.set("use_time", useTime);

        updateWrapper.eq("submit_id", submitId); // submit_id一定只有一个
        boolean result = examRecordMapper.update(null, updateWrapper) > 0;
        if (!result) {
            tryAgainUpdate(updateWrapper);
        }
    }

    public void tryAgainUpdate(UpdateWrapper<ExamRecord> updateWrapper) {
        boolean retryable;
        int attemptNumber = 0;
        do {
            boolean result = examRecordMapper.update(null, updateWrapper) > 0;
            if (result) {
                break;
            } else {
                attemptNumber++;
                retryable = attemptNumber < 8;
                if (attemptNumber == 8) {
                    log.error("更新exam_record表超过最大重试次数");
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        } while (retryable);
    }
}
