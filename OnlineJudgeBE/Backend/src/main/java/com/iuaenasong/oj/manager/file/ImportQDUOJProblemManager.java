/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusSystemErrorException;
import com.iuaenasong.oj.pojo.dto.ProblemDto;
import com.iuaenasong.oj.pojo.dto.QDOJProblemDto;
import com.iuaenasong.oj.pojo.entity.problem.Language;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.entity.problem.ProblemCase;
import com.iuaenasong.oj.pojo.entity.problem.Tag;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.dao.problem.LanguageEntityService;
import com.iuaenasong.oj.dao.problem.ProblemEntityService;
import com.iuaenasong.oj.dao.problem.TagEntityService;
import com.iuaenasong.oj.utils.Constants;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j(topic = "oj")
public class ImportQDUOJProblemManager {

    @Autowired
    private LanguageEntityService languageEntityService;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private TagEntityService tagEntityService;

    @Transactional(rollbackFor = Exception.class)
    public void importQDOJProblem(MultipartFile file) throws StatusFailException, StatusSystemErrorException {

        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if (!"zip".toUpperCase().contains(suffix.toUpperCase())) {
            throw new StatusFailException("?????????zip?????????????????????????????????");
        }

        String fileDirId = IdUtil.simpleUUID();
        String fileDir = Constants.File.TESTCASE_TMP_FOLDER.getPath() + File.separator + fileDirId;
        String filePath = fileDir + File.separator + file.getOriginalFilename();
        // ???????????????????????????
        FileUtil.mkdir(fileDir);
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            FileUtil.del(fileDir);
            throw new StatusSystemErrorException("??????????????????qduoj?????????????????????");
        }

        // ????????????????????????????????????
        ZipUtil.unzip(filePath, fileDir);

        // ??????zip??????
        FileUtil.del(filePath);

        // ????????????????????????
        File testCaseFileList = new File(fileDir);
        File[] files = testCaseFileList.listFiles();
        if (files == null || files.length == 0) {
            FileUtil.del(fileDir);
            throw new StatusFailException("?????????????????????????????????????????????");
        }

        HashMap<String, File> problemInfo = new HashMap<>();
        for (File tmp : files) {
            if (tmp.isDirectory()) {
                File[] problemAndTestcase = tmp.listFiles();
                if (problemAndTestcase == null || problemAndTestcase.length == 0) {
                    FileUtil.del(fileDir);
                    throw new StatusFailException("????????????" + tmp.getName() + "?????????????????????");
                }
                for (File problemFile : problemAndTestcase) {
                    if (problemFile.isFile()) {
                        // ?????????????????????json??????
                        if (!problemFile.getName().endsWith("json")) {
                            FileUtil.del(fileDir);
                            throw new StatusFailException("????????????" + tmp.getName() + "?????????????????????????????????????????????????????????json?????????");
                        }
                        problemInfo.put(tmp.getName(), problemFile);
                    }
                }
            }
        }

        // ??????json??????????????????
        HashMap<String, QDOJProblemDto> problemVoMap = new HashMap<>();
        for (String key : problemInfo.keySet()) {
            try {
                FileReader fileReader = new FileReader(problemInfo.get(key));
                JSONObject problemJson = JSONUtil.parseObj(fileReader.readString());
                QDOJProblemDto qdojProblemDto = QDOJProblemToProblemVo(problemJson);
                problemVoMap.put(key, qdojProblemDto);
            } catch (Exception e) {
                FileUtil.del(fileDir);
                throw new StatusFailException("?????????????????????" + key + "?????????json??????????????????" + e.getLocalizedMessage());
            }
        }

        QueryWrapper<Language> languageQueryWrapper = new QueryWrapper<>();
        languageQueryWrapper.eq("oj", "ME");
        List<Language> languageList = languageEntityService.list(languageQueryWrapper);

        HashMap<String, Long> languageMap = new HashMap<>();
        for (Language language : languageList) {
            languageMap.put(language.getName(), language.getId());
        }

        // ???????????????????????????
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        List<Tag> tagList = tagEntityService.list(new QueryWrapper<Tag>().eq("oj", "ME"));
        HashMap<String, Tag> tagMap = new HashMap<>();
        for (Tag tag : tagList) {
            tagMap.put(tag.getName().toUpperCase(), tag);
        }

        List<ProblemDto> problemDtos = new LinkedList<>();
        for (String key : problemInfo.keySet()) {
            QDOJProblemDto qdojProblemDto = problemVoMap.get(key);
            // ?????????????????????
            List<Language> languages = new LinkedList<>();
            for (String lang : qdojProblemDto.getLanguages()) {
                Long lid = languageMap.getOrDefault(lang, null);
                languages.add(new Language().setId(lid).setName(lang));
            }

            // ???????????????
            List<Tag> tags = new LinkedList<>();
            for (String tagStr : qdojProblemDto.getTags()) {
                Tag tag = tagMap.getOrDefault(tagStr.toUpperCase(), null);
                if (tag == null) {
                    tags.add(new Tag().setName(tagStr).setOj("ME"));
                } else {
                    tags.add(tag);
                }
            }

            Problem problem = qdojProblemDto.getProblem();
            if (problem.getAuthor() == null) {
                problem.setAuthor(userRolesVo.getUsername());
            }
            ProblemDto problemDto = new ProblemDto();

            String mode = Constants.JudgeMode.DEFAULT.getMode();
            if (qdojProblemDto.getIsSpj()) {
                mode = Constants.JudgeMode.SPJ.getMode();
            }

            problemDto.setJudgeMode(mode)
                    .setProblem(problem)
                    .setCodeTemplates(qdojProblemDto.getCodeTemplates())
                    .setTags(tags)
                    .setLanguages(languages)
                    .setUploadTestcaseDir(fileDir + File.separator + key + File.separator + "testcase")
                    .setIsUploadTestCase(true)
                    .setSamples(qdojProblemDto.getSamples());

            problemDtos.add(problemDto);
        }
        for (ProblemDto problemDto : problemDtos) {
            problemEntityService.adminAddProblem(problemDto);
        }
    }

    private QDOJProblemDto QDOJProblemToProblemVo(JSONObject problemJson) {
        QDOJProblemDto qdojProblemDto = new QDOJProblemDto();
        List<String> tags = (List<String>) problemJson.get("tags");
        qdojProblemDto.setTags(tags.stream().map(UnicodeUtil::toString).collect(Collectors.toList()));
        qdojProblemDto.setLanguages(Arrays.asList("C", "C With O2", "C++", "C++ With O2", "Java", "Python3", "Python2", "Golang", "C#"));
        Object spj = problemJson.getObj("spj");
        boolean isSpj = !JSONUtil.isNull(spj);
        qdojProblemDto.setIsSpj(isSpj);

        Problem problem = new Problem();
        if (isSpj) {
            JSONObject spjJson = JSONUtil.parseObj(spj);
            problem.setSpjCode(spjJson.getStr("code"))
                    .setSpjLanguage(spjJson.getStr("language"));
        }
        problem.setAuth(1)
                .setIsUploadCase(true)
                .setSource(problemJson.getStr("source", null))
                .setDifficulty(1)
                .setProblemId(problemJson.getStr("display_id"))
                .setIsRemoveEndBlank(true)
                .setOpenCaseResult(true)
                .setCodeShare(false)
                .setType(problemJson.getStr("rule_type").equals("ACM") ? 0 : 1)
                .setTitle(problemJson.getStr("title"))
                .setDescription(UnicodeUtil.toString(problemJson.getJSONObject("description").getStr("value")))
                .setInput(UnicodeUtil.toString(problemJson.getJSONObject("input_description").getStr("value")))
                .setOutput(UnicodeUtil.toString(problemJson.getJSONObject("output_description").getStr("value")))
                .setHint(UnicodeUtil.toString(problemJson.getJSONObject("hint").getStr("value")))
                .setTimeLimit(problemJson.getInt("time_limit"))
                .setMemoryLimit(problemJson.getInt("memory_limit"));

        JSONArray samples = problemJson.getJSONArray("samples");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < samples.size(); i++) {
            JSONObject sample = (JSONObject) samples.get(i);
            String input = sample.getStr("input");
            String output = sample.getStr("output");
            sb.append("<input>").append(input).append("</input>");
            sb.append("<output>").append(output).append("</output>");
        }
        problem.setExamples(sb.toString());

        int sumScore = 0;
        JSONArray testcaseList = problemJson.getJSONArray("test_case_score");
        List<ProblemCase> problemSamples = new LinkedList<>();
        for (int i = 0; i < testcaseList.size(); i++) {
            JSONObject testcase = (JSONObject) testcaseList.get(i);
            String input = testcase.getStr("input_name");
            String output = testcase.getStr("output_name");
            Integer score = testcase.getInt("score", null);
            problemSamples.add(new ProblemCase().setInput(input).setOutput(output).setScore(score));
            if (score != null) {
                sumScore += score;
            }
        }
        problem.setIsRemote(false);
        problem.setIoScore(sumScore);
        qdojProblemDto.setSamples(problemSamples);
        qdojProblemDto.setProblem(problem);
        return qdojProblemDto;

    }
}