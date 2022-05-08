/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.manager.admin.exam;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.crawler.problem.ProblemStrategy;
import com.iuaenasong.oj.dao.exam.ExamEntityService;
import com.iuaenasong.oj.dao.exam.ExamProblemEntityService;
import com.iuaenasong.oj.dao.judge.JudgeEntityService;
import com.iuaenasong.oj.dao.problem.ProblemEntityService;
import com.iuaenasong.oj.manager.admin.problem.RemoteProblemManager;
import com.iuaenasong.oj.pojo.dto.ExamProblemDto;
import com.iuaenasong.oj.pojo.dto.ProblemDto;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.pojo.entity.exam.ExamProblem;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.utils.Constants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdminExamProblemManager {

    @Autowired
    private ExamProblemEntityService examProblemEntityService;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private JudgeEntityService judgeEntityService;

    @Autowired
    private RemoteProblemManager remoteProblemManager;

    @Autowired
    private ExamEntityService examEntityService;

    public HashMap<String, Object> getProblemList(Integer limit, Integer currentPage, String keyword,
                                                  Long eid, Integer problemType, String oj) {
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        IPage<Problem> iPage = new Page<>(currentPage, limit);
        // 根据eid在ExamProblem表中查询到对应pid集合
        QueryWrapper<ExamProblem> examProblemQueryWrapper = new QueryWrapper<>();
        examProblemQueryWrapper.eq("eid", eid);
        List<Long> pidList = new LinkedList<>();

        List<ExamProblem> examProblemList = examProblemEntityService.list(examProblemQueryWrapper);
        HashMap<Long, ExamProblem> examProblemMap = new HashMap<>();
        examProblemList.forEach(examProblem -> {
            examProblemMap.put(examProblem.getPid(), examProblem);
            pidList.add(examProblem.getPid());
        });

        HashMap<String, Object> examProblem = new HashMap<>();

        QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();

        if (problemType != null) { // 必备条件 隐藏的不可取来做考试题目
            problemQueryWrapper.eq("is_public", true)
                    // vj题目不限制赛制
                    .and(wrapper -> wrapper.eq("type", problemType)
                            .or().eq("is_remote", true))
                    .ne("auth", 2); // 同时需要与考试相同类型的题目，权限需要是公开的（隐藏的不可加入！）
            Exam exam = examEntityService.getById(eid);
            if (exam.getGid() != null) { //团队考试不能查看公共题库的隐藏题目
                problemQueryWrapper.ne("auth", 3);
            }
        }

        // 逻辑判断，如果是查询已有的就应该是in，如果是查询不要重复的，使用not in
        if (problemType != null) {
            problemQueryWrapper.notIn(pidList.size() > 0, "id", pidList);
        } else {
            problemQueryWrapper.in(pidList.size() > 0, "id", pidList);
        }

        // 根据oj筛选过滤
        if (oj != null && !"All".equals(oj)) {
            if (!Constants.RemoteOJ.isRemoteOJ(oj)) {
                problemQueryWrapper.eq("is_remote", false);
            } else {
                problemQueryWrapper.eq("is_remote", true).likeRight("problem_id", oj);
            }
        }

        if (!StringUtils.isEmpty(keyword)) {
            problemQueryWrapper.and(wrapper -> wrapper.like("title", keyword).or()
                    .like("problem_id", keyword).or()
                    .like("author", keyword));
        }

        if (pidList.size() == 0 && problemType == null) {
            problemQueryWrapper = new QueryWrapper<>();
            problemQueryWrapper.eq("id", null);
        }

        IPage<Problem> problemListPage = problemEntityService.page(iPage, problemQueryWrapper);

        if (pidList.size() > 0 && problemType == null) {
            List<Problem> problemList = problemListPage.getRecords();

            List<Problem> sortedProblemList = problemList.stream().sorted(Comparator.comparing(Problem::getId, (a, b) -> {
                ExamProblem x = examProblemMap.get(a);
                ExamProblem y = examProblemMap.get(b);
                if (x == null && y != null) {
                    return 1;
                } else if (x != null && y == null) {
                    return -1;
                } else if (x == null) {
                    return -1;
                } else {
                    return x.getDisplayId().compareTo(y.getDisplayId());
                }
            })).collect(Collectors.toList());
            problemListPage.setRecords(sortedProblemList);
        }

        examProblem.put("problemList", problemListPage);
        examProblem.put("examProblemMap", examProblemMap);

        return examProblem;
    }

    public Problem getProblem(Long pid) throws StatusFailException, StatusForbiddenException {

        Problem problem = problemEntityService.getById(pid);

        if (problem != null) { // 查询成功
            // 获取当前登录的用户
            Session session = SecurityUtils.getSubject().getSession();
            UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

            boolean isRoot = SecurityUtils.getSubject().hasRole("root");
            boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
            // 只有超级管理员和题目管理员、题目创建者才能操作
            if (!isRoot && !isProblemAdmin && !userRolesVo.getUsername().equals(problem.getAuthor())) {
                throw new StatusForbiddenException("对不起，你无权限查看题目！");
            }

            return problem;
        } else {
            throw new StatusFailException("查询失败！");
        }
    }

    public void deleteProblem(Long pid, Long eid) {
        //  考试id不为null，表示就是从考试列表移除而已
        if (eid != null) {
            QueryWrapper<ExamProblem> examProblemQueryWrapper = new QueryWrapper<>();
            examProblemQueryWrapper.eq("eid", eid).eq("pid", pid);
            examProblemEntityService.remove(examProblemQueryWrapper);
            // 把该题目在考试的提交全部删掉
            UpdateWrapper<Judge> judgeUpdateWrapper = new UpdateWrapper<>();
            judgeUpdateWrapper.eq("eid", eid).eq("pid", pid);
            judgeEntityService.remove(judgeUpdateWrapper);
        } else {

            problemEntityService.removeById(pid);
        }

        if (eid == null) {
            FileUtil.del(Constants.File.TESTCASE_BASE_FOLDER.getPath() + File.separator + "problem_" + pid);
        }
    }

    public Map<Object, Object> addProblem(ProblemDto problemDto) throws StatusFailException {

        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("problem_id", problemDto.getProblem().getProblemId().toUpperCase());
        Problem problem = problemEntityService.getOne(queryWrapper);
        if (problem != null) {
            throw new StatusFailException("该题目的Problem ID已存在，请更换！");
        }
        // 设置为考试题目
        problemDto.getProblem().setAuth(3);
        boolean isOk = problemEntityService.adminAddProblem(problemDto);
        if (isOk) { // 添加成功
            // 顺便返回新的题目id，好下一步添加外键操作
            return MapUtil.builder().put("pid", problemDto.getProblem().getId()).map();
        } else {
            throw new StatusFailException("添加失败");
        }
    }

    public void updateProblem(ProblemDto problemDto) throws StatusForbiddenException, StatusFailException {
        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        // 只有超级管理员和题目管理员、题目创建者才能操作
        if (!isRoot && !isProblemAdmin && !userRolesVo.getUsername().equals(problemDto.getProblem().getAuthor())) {
            throw new StatusForbiddenException("对不起，你无权限修改题目！");
        }

        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("problem_id", problemDto.getProblem().getProblemId().toUpperCase());
        Problem problem = problemEntityService.getOne(queryWrapper);

        // 如果problem_id不是原来的且已存在该problem_id，则修改失败！
        if (problem != null && problem.getId().longValue() != problemDto.getProblem().getId()) {
            throw new StatusFailException("当前的Problem ID 已被使用，请重新更换新的！");
        }

        // 记录修改题目的用户
        problemDto.getProblem().setModifiedUser(userRolesVo.getUsername());

        boolean isOk = problemEntityService.adminUpdateProblem(problemDto);
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

    public ExamProblem getExamProblem(Long eid, Long pid) throws StatusFailException {
        QueryWrapper<ExamProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("eid", eid).eq("pid", pid);
        ExamProblem examProblem = examProblemEntityService.getOne(queryWrapper);
        if (examProblem == null) {
            throw new StatusFailException("查询失败");
        }
        return examProblem;
    }

    public ExamProblem setExamProblem(ExamProblem examProblem) throws StatusFailException {
        boolean isOk = examProblemEntityService.saveOrUpdate(examProblem);
        if (isOk) {
            examProblemEntityService.syncExamRecord(examProblem.getPid(), examProblem.getEid(), examProblem.getDisplayId(), examProblem.getScore());
            return examProblem;
        } else {
            throw new StatusFailException("更新失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addProblemFromPublic(ExamProblemDto examProblemDto) throws StatusFailException {

        Long pid = examProblemDto.getPid();
        Long eid = examProblemDto.getEid();
        String displayId = examProblemDto.getDisplayId();

        QueryWrapper<ExamProblem> examProblemQueryWrapper = new QueryWrapper<>();
        examProblemQueryWrapper.eq("eid", eid)
                .and(wrapper -> wrapper.eq("pid", pid)
                        .or()
                        .eq("display_id", displayId));
        ExamProblem examProblem = examProblemEntityService.getOne(examProblemQueryWrapper, false);
        if (examProblem != null) {
            throw new StatusFailException("添加失败，该题目已添加或者题目的考试展示ID已存在！");
        }

        // 考试中题目显示默认为原标题
        Problem problem = problemEntityService.getById(pid);
        String displayName = problem.getTitle();

        // 修改成考试题目
        boolean updateProblem = problemEntityService.saveOrUpdate(problem.setAuth(3));

        boolean isOk = examProblemEntityService.saveOrUpdate(new ExamProblem()
                .setEid(eid).setPid(pid).setDisplayTitle(displayName).setDisplayId(displayId));
        if (!isOk || !updateProblem) {
            throw new StatusFailException("添加失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void importExamRemoteOJProblem(String name, String problemId, Long eid, String displayId) throws StatusFailException {
        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("problem_id", name.toUpperCase() + "-" + problemId);
        Problem problem = problemEntityService.getOne(queryWrapper, false);

        // 如果该题目不存在，需要先导入
        if (problem == null) {
            Session session = SecurityUtils.getSubject().getSession();
            UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");
            try {
                ProblemStrategy.RemoteProblemInfo otherOJProblemInfo = remoteProblemManager.getOtherOJProblemInfo(name.toUpperCase(), problemId, userRolesVo.getUsername());
                if (otherOJProblemInfo != null) {
                    problem = remoteProblemManager.adminAddOtherOJProblem(otherOJProblemInfo, name);
                    if (problem == null) {
                        throw new StatusFailException("导入新题目失败！请重新尝试！");
                    }
                } else {
                    throw new StatusFailException("导入新题目失败！原因：可能是与该OJ链接超时或题号格式错误！");
                }
            } catch (Exception e) {
                throw new StatusFailException(e.getMessage());
            }
        }

        QueryWrapper<ExamProblem> examProblemQueryWrapper = new QueryWrapper<>();
        Problem finalProblem = problem;
        examProblemQueryWrapper.eq("eid", eid)
                .and(wrapper -> wrapper.eq("pid", finalProblem.getId())
                        .or()
                        .eq("display_id", displayId));
        ExamProblem examProblem = examProblemEntityService.getOne(examProblemQueryWrapper, false);
        if (examProblem != null) {
            throw new StatusFailException("添加失败，该题目已添加或者题目的考试展示ID已存在！");
        }

        // 考试中题目显示默认为原标题
        String displayName = problem.getTitle();

        // 修改成考试题目
        boolean updateProblem = problemEntityService.saveOrUpdate(problem.setAuth(3));

        boolean isOk = examProblemEntityService.saveOrUpdate(new ExamProblem()
                .setEid(eid).setPid(problem.getId()).setDisplayTitle(displayName).setDisplayId(displayId));

        if (!isOk || !updateProblem) {
            throw new StatusFailException("添加失败");
        }
    }

}