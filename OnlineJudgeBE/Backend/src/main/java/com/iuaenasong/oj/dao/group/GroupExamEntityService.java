/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.dao.group;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.vo.ExamVo;

public interface GroupExamEntityService extends IService<Exam> {

    IPage<ExamVo> getExamList(int limit, int currentPage, Long gid);

    IPage<Exam> getAdminExamList(int limit, int currentPage, Long gid);

}
