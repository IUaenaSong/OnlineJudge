/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.dao.problem.ProblemEntityService;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.validator.GroupValidator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusSystemErrorException;
import com.iuaenasong.oj.common.result.ResultStatus;
import com.iuaenasong.oj.pojo.entity.problem.ProblemCase;
import com.iuaenasong.oj.dao.problem.ProblemCaseEntityService;
import com.iuaenasong.oj.utils.Constants;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j(topic = "oj")
public class TestCaseManager {

    @Autowired
    private ProblemCaseEntityService problemCaseEntityService;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private GroupValidator groupValidator;

    public Map<Object,Object> uploadTestcaseZip(MultipartFile file, Long gid) throws StatusFailException, StatusSystemErrorException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        Boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        Boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        Boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        if (!isRoot && !isProblemAdmin && !isAdmin && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        //获取文件后缀
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if (!"zip".toUpperCase().contains(suffix.toUpperCase())) {
            throw new StatusFailException("请上传zip格式的测试数据压缩包！");
        }
        String fileDirId = IdUtil.simpleUUID();
        String fileDir = Constants.File.TESTCASE_TMP_FOLDER.getPath() + File.separator + fileDirId;
        String filePath = fileDir + File.separator + file.getOriginalFilename();
        // 文件夹不存在就新建
        FileUtil.mkdir(fileDir);
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            log.error("评测数据文件上传异常-------------->{}", e.getMessage());
            throw new StatusSystemErrorException("服务器异常：评测数据上传失败！");
        }

        // 将压缩包压缩到指定文件夹
        ZipUtil.unzip(filePath, fileDir);
        // 删除zip文件
        FileUtil.del(filePath);
        // 检查文件是否存在
        File testCaseFileList = new File(fileDir);
        File[] files = testCaseFileList.listFiles();
        if (files == null || files.length == 0) {
            FileUtil.del(fileDir);
            throw new StatusFailException("评测数据压缩包里文件不能为空！");
        }

        HashMap<String, String> inputData = new HashMap<>();
        HashMap<String, String> outputData = new HashMap<>();

        // 遍历读取与检查是否in和out文件一一对应，否则报错
        for (File tmp : files) {
            String tmpPreName = null;
            if (tmp.getName().endsWith(".in")) {
                tmpPreName = tmp.getName().substring(0, tmp.getName().lastIndexOf(".in"));
                inputData.put(tmpPreName, tmp.getName());
            } else if (tmp.getName().endsWith(".out")) {
                tmpPreName = tmp.getName().substring(0, tmp.getName().lastIndexOf(".out"));
                outputData.put(tmpPreName, tmp.getName());
            } else if (tmp.getName().endsWith(".ans")) {
                tmpPreName = tmp.getName().substring(0, tmp.getName().lastIndexOf(".ans"));
                outputData.put(tmpPreName, tmp.getName());
            } else if (tmp.getName().endsWith(".txt")) {
                tmpPreName = tmp.getName().substring(0, tmp.getName().lastIndexOf(".txt"));
                if (tmpPreName.contains("input")) {
                    inputData.put(tmpPreName.replaceAll("input", "$*$"), tmp.getName());
                } else if (tmpPreName.contains("output")) {
                    outputData.put(tmpPreName.replaceAll("output", "$*$"), tmp.getName());
                }
            }
        }

        // 进行数据对应检查,同时生成返回数据
        List<HashMap<String, String>> problemCaseList = new LinkedList<>();
        for (String key : inputData.keySet()) {
            HashMap<String, String> testcaseMap = new HashMap<>();
            String inputFileName = inputData.get(key);
            testcaseMap.put("input", inputFileName);

            String outputFileName = key + ".out";
            if (inputFileName.endsWith(".txt")) {
                outputFileName = inputFileName.replaceAll("input", "output");
            }

            // 若有名字对应的out文件不存在的，直接生成对应的out文件
            if (outputData.getOrDefault(key, null) == null) {
                FileWriter fileWriter = new FileWriter(fileDir + File.separator + outputFileName);
                fileWriter.write("");
            }

            testcaseMap.put("output", outputFileName);
            problemCaseList.add(testcaseMap);
        }

        List<HashMap<String, String>> fileList = problemCaseList.stream()
                .sorted((o1, o2) -> {
                    String a = o1.get("input").split("\\.")[0];
                    String b = o2.get("input").split("\\.")[0];
                    if (a.length() > b.length()) {
                        return 1;
                    } else if (a.length() < b.length()) {
                        return -1;
                    }
                    return a.compareTo(b);
                })
                .collect(Collectors.toList());

        return MapUtil.builder()
                        .put("fileList", fileList)
                        .put("fileListDir", fileDir)
                        .map();
    }

    public void downloadTestcase(Long pid, HttpServletResponse response) throws StatusFailException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        Boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        Boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");

        Problem problem = problemEntityService.getById(pid);

        Long gid = problem.getGid();

        if (gid != null) {
            if (!isRoot && !problem.getAuthor().equals(userRolesVo.getUsername()) && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        } else {
            if (!isRoot && !isProblemAdmin && !problem.getAuthor().equals(userRolesVo.getUsername()) && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        }

        String workDir = Constants.File.TESTCASE_BASE_FOLDER.getPath() + File.separator + "problem_" + pid;
        File file = new File(workDir);
        if (!file.exists()) { // 本地为空 尝试去数据库查找
            QueryWrapper<ProblemCase> problemCaseQueryWrapper = new QueryWrapper<>();
            problemCaseQueryWrapper.eq("pid", pid);
            List<ProblemCase> problemCaseList = problemCaseEntityService.list(problemCaseQueryWrapper);

            if (CollectionUtils.isEmpty(problemCaseList)){
                throw new StatusFailException("对不起，该题目的评测数据为空！");
            }

            boolean hasTestCase = true;
            if (problemCaseList.get(0).getInput().endsWith(".in") && (problemCaseList.get(0).getOutput().endsWith(".out") ||
                    problemCaseList.get(0).getOutput().endsWith(".ans"))) {
                hasTestCase = false;
            }
            if (!hasTestCase){
                throw new StatusFailException("对不起，该题目的评测数据为空！");
            }

            FileUtil.mkdir(workDir);
            // 写入本地
            for (int i = 0; i < problemCaseList.size(); i++) {
                String filePreName = workDir + File.separator + (i + 1);
                String inputName = filePreName + ".in";
                String outputName = filePreName + ".out";
                FileWriter infileWriter = new FileWriter(inputName);
                infileWriter.write(problemCaseList.get(i).getInput());
                FileWriter outfileWriter = new FileWriter(outputName);
                outfileWriter.write(problemCaseList.get(i).getOutput());
            }
        }

        String fileName = "problem_" + pid + "_testcase_" + System.currentTimeMillis() + ".zip";
        // 将对应文件夹的文件压缩成zip
        ZipUtil.zip(workDir, Constants.File.FILE_DOWNLOAD_TMP_FOLDER.getPath() + File.separator + fileName);
        // 将zip变成io流返回给前端
        FileReader fileReader = new FileReader(Constants.File.FILE_DOWNLOAD_TMP_FOLDER.getPath() + File.separator + fileName);
        BufferedInputStream bins = new BufferedInputStream(fileReader.getInputStream());//放到缓冲流里面
        OutputStream outs = null;//获取文件输出IO流
        BufferedOutputStream bouts = null;
        try {
            outs = response.getOutputStream();
            bouts = new BufferedOutputStream(outs);
            response.setContentType("application/x-download");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            int bytesRead = 0;
            byte[] buffer = new byte[1024 * 10];
            //开始向网络传输文件流
            while ((bytesRead = bins.read(buffer, 0, 1024 * 10)) != -1) {
                bouts.write(buffer, 0, bytesRead);
            }
            bouts.flush();
        } catch (IOException e) {
            log.error("下载题目测试数据的压缩文件异常------------>{}", e.getMessage());
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, Object> map = new HashMap<>();
            map.put("status", ResultStatus.SYSTEM_ERROR);
            map.put("msg", "下载文件失败，请重新尝试！");
            map.put("data", null);
            try {
                response.getWriter().println(JSONUtil.toJsonStr(map));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } finally {
            try {
                bins.close();
                if (outs != null) {
                    outs.close();
                }
                if (bouts != null) {
                    bouts.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 清空临时文件
            FileUtil.del(Constants.File.FILE_DOWNLOAD_TMP_FOLDER.getPath() + File.separator + fileName);
        }
    }
}