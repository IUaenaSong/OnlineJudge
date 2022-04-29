/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.service.group.exam;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.vo.AdminExamVo;
import com.iuaenasong.oj.pojo.vo.ExamVo;

public interface GroupExamService {

    public CommonResult<IPage<ExamVo>> getExamList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<IPage<Exam>> getAdminExamList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<AdminExamVo> getExam(Long eid);

    public CommonResult<Void> addExam(AdminExamVo adminExamVo);

    public CommonResult<Void> updateExam(AdminExamVo adminExamVo);

    public CommonResult<Void> deleteExam(Long eid);

    public CommonResult<Void> changeExamVisible(Long eid, Boolean visible);

}
