/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.problem.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import com.iuaenasong.oj.mapper.ProblemMapper;
import com.iuaenasong.oj.pojo.dto.ProblemDto;
import com.iuaenasong.oj.pojo.entity.problem.*;
import com.iuaenasong.oj.pojo.vo.ImportProblemVo;
import com.iuaenasong.oj.pojo.vo.ProblemCountVo;
import com.iuaenasong.oj.pojo.vo.ProblemVo;
import com.iuaenasong.oj.dao.judge.JudgeEntityService;
import com.iuaenasong.oj.dao.problem.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.utils.Constants;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProblemEntityServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements ProblemEntityService {

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private JudgeEntityService judgeEntityService;

    @Autowired
    private ProblemCaseEntityService problemCaseEntityService;

    @Autowired
    private ProblemLanguageEntityService problemLanguageEntityService;

    @Autowired
    private TagEntityService tagEntityService;

    @Autowired
    private ProblemTagEntityService problemTagEntityService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CodeTemplateEntityService codeTemplateEntityService;

    @Override
    public Page<ProblemVo> getProblemList(int limit, int currentPage, Long pid, String title, Integer difficulty,
                                          List<Long> tid, String oj) {

        //????????????
        Page<ProblemVo> page = new Page<>(currentPage, limit);
        Integer tagListSize = null;
        if (tid != null) {
            tid = tid.stream().distinct().collect(Collectors.toList());
            tagListSize = tid.size();
        }

        List<ProblemVo> problemList = problemMapper.getProblemList(page, pid, title, difficulty, tid, tagListSize, oj);

        if (problemList.size() > 0) {
            List<Long> pidList = problemList.stream().map(ProblemVo::getPid).collect(Collectors.toList());
            List<ProblemCountVo> problemListCount = judgeEntityService.getProblemListCount(pidList);
            for (ProblemVo problemVo : problemList) {
                for (ProblemCountVo problemCountVo : problemListCount) {
                    if (problemVo.getPid().equals(problemCountVo.getPid())) {
                        problemVo.setProblemCountVo(problemCountVo);
                        break;
                    }
                }
            }
        }

        return page.setRecords(problemList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adminUpdateProblem(ProblemDto problemDto) {

        Problem problem = problemDto.getProblem();
        if (Constants.JudgeMode.DEFAULT.getMode().equals(problemDto.getJudgeMode())) {
            problem.setSpjLanguage(null).setSpjCode(null);
        }

        String ojName = "ME";
        if (problem.getIsRemote()) {
            String problemId = problem.getProblemId();
            ojName = problemId.split("-")[0];
        }

        
        String problemId = problem.getProblemId().toUpperCase();
        QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.eq("problem_id", problemId);
        Problem existedProblem = problemMapper.selectOne(problemQueryWrapper);

        problem.setProblemId(problem.getProblemId().toUpperCase());
        // ???????????????????????????????????????????????????id
        long pid = problemDto.getProblem().getId();

        if (existedProblem != null && existedProblem.getId() != pid) {
            throw new RuntimeException("The problem_id [" + problemId + "] already exists. Do not reuse it!");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("pid", pid);
        //???????????????????????????????????????
        List<ProblemLanguage> oldProblemLanguages = (List<ProblemLanguage>) problemLanguageEntityService.listByMap(map);
        List<ProblemCase> oldProblemCases = (List<ProblemCase>) problemCaseEntityService.listByMap(map);
        List<CodeTemplate> oldProblemTemplate = (List<CodeTemplate>) codeTemplateEntityService.listByMap(map);
        List<ProblemTag> oldProblemTags = (List<ProblemTag>) problemTagEntityService.listByMap(map);

        Map<Long, Integer> mapOldPT = new HashMap<>();
        Map<Long, Integer> mapOldPL = new HashMap<>();
        Map<Integer, Integer> mapOldPCT = new HashMap<>();
        List<Long> needDeleteProblemCases = new LinkedList<>();
        HashMap<Long, ProblemCase> oldProblemMap = new HashMap<>();

        // ?????????????????????tag???id
        oldProblemTags.stream().forEach(problemTag -> {
            mapOldPT.put(problemTag.getTid(), 0);
        });
        // ?????????????????????language???id
        oldProblemLanguages.stream().forEach(problemLanguage -> {
            mapOldPL.put(problemLanguage.getLid(), 0);
        });
        // ?????????????????????codeTemplate???id
        oldProblemTemplate.stream().forEach(codeTemplate -> {
            mapOldPCT.put(codeTemplate.getId(), 0);
        });
        // ?????????????????????case???id
        oldProblemCases.stream().forEach(problemCase -> {
            needDeleteProblemCases.add(problemCase.getId());
            oldProblemMap.put(problemCase.getId(), problemCase);
        });

        // ?????????????????????????????????????????????????????????
        
        List<ProblemTag> problemTagList = new LinkedList<>(); // ????????????problem_tag?????????
        for (Tag tag : problemDto.getTags()) {
            if (tag.getId() == null) { // ???????????????????????????????????????
                tag.setOj(ojName);
                boolean addTagResult = tagEntityService.save(tag);
                if (addTagResult) {
                    problemTagList.add(new ProblemTag()
                            .setPid(pid).setTid(tag.getId()));
                }
                // ?????????tag ??????????????????
            } else if (mapOldPT.getOrDefault(tag.getId(), null) == null) {
                problemTagList.add(new ProblemTag()
                        .setPid(pid).setTid(tag.getId()));
            } else { // ????????????????????????????????????????????????problem_tag?????????????????????????????????????????????
                mapOldPT.put(tag.getId(), 1); // ????????????????????????tag?????????
            }
        }
        // ?????????????????????tagId??????
        List<Long> needDeleteTids = new LinkedList<>();
        for (Long key : mapOldPT.keySet()) {
            if (mapOldPT.get(key) == 0) { // ???????????????????????????????????????Tid???????????????tag????????????problem??????
                needDeleteTids.add(key);
            }
        }
        boolean deleteTagsFromProblemResult = true;
        if (needDeleteTids.size() > 0) {
            QueryWrapper<ProblemTag> tagWrapper = new QueryWrapper<>();
            tagWrapper.eq("pid", pid).in("tid", needDeleteTids);
            // ????????????????????????
            deleteTagsFromProblemResult = problemTagEntityService.remove(tagWrapper);
        }
        // ????????????????????????
        boolean addTagsToProblemResult = true;
        if (problemTagList.size() > 0) {
            addTagsToProblemResult = problemTagEntityService.saveOrUpdateBatch(problemTagList);
        }

        
        boolean deleteTemplate = true;
        boolean saveOrUpdateCodeTemplate = true;
        for (CodeTemplate codeTemplate : problemDto.getCodeTemplates()) {
            if (codeTemplate.getId() != null) {
                mapOldPCT.put(codeTemplate.getId(), 1);
            }
        }
        // ?????????????????????
        List<Integer> needDeleteCTs = new LinkedList<>();
        for (Integer key : mapOldPCT.keySet()) {
            if (mapOldPCT.get(key) == 0) {
                needDeleteCTs.add(key);
            }
        }
        if (needDeleteCTs.size() > 0) {
            deleteTemplate = codeTemplateEntityService.removeByIds(needDeleteCTs);
        }
        if (problemDto.getCodeTemplates().size() > 0) {
            saveOrUpdateCodeTemplate = codeTemplateEntityService.saveOrUpdateBatch(problemDto.getCodeTemplates());
        }

        // ??????????????????language??????????????????name?????????????????????language??????id?????????problem_language
        //??????problem_language????????????
        List<ProblemLanguage> problemLanguageList = new LinkedList<>();
        for (Language language : problemDto.getLanguages()) { // ????????????
            if (mapOldPL.get(language.getId()) != null) { // ?????????????????????????????????language?????????????????????
                mapOldPL.put(language.getId(), 1); // ?????????????????????????????????language
            } else { // ???????????????????????????????????????language
                problemLanguageList.add(new ProblemLanguage().setLid(language.getId()).setPid(pid));
            }
        }
        // ?????????????????????languageId??????
        List<Long> needDeleteLids = new LinkedList<>();
        for (Long key : mapOldPL.keySet()) {
            if (mapOldPL.get(key) == 0) { // ???????????????????????????????????????Lid???????????????language????????????problem??????
                needDeleteLids.add(key);
            }
        }
        boolean deleteLanguagesFromProblemResult = true;
        if (needDeleteLids.size() > 0) {
            QueryWrapper<ProblemLanguage> LangWrapper = new QueryWrapper<>();
            LangWrapper.eq("pid", pid).in("lid", needDeleteLids);
            // ????????????????????????
            deleteLanguagesFromProblemResult = problemLanguageEntityService.remove(LangWrapper);
        }
        // ????????????????????????
        boolean addLanguagesToProblemResult = true;
        if (problemLanguageList.size() > 0) {
            addLanguagesToProblemResult = problemLanguageEntityService.saveOrUpdateBatch(problemLanguageList);
        }

        boolean checkProblemCase = true;

        if (!problem.getIsRemote() && problemDto.getSamples().size() > 0) { // ??????????????????????????????????????????
            int sumScore = 0;
            // ????????????case??????
            List<ProblemCase> newProblemCaseList = new LinkedList<>();
            // ???????????????case??????
            List<ProblemCase> needUpdateProblemCaseList = new LinkedList<>();
            // ???????????????case???????????????????????????????????????????????????????????????????????????id
            for (ProblemCase problemCase : problemDto.getSamples()) {
                if (problemCase.getId() != null) { // ????????????case
                    needDeleteProblemCases.remove(problemCase.getId());
                    // ?????????????????????????????????????????? ????????????????????????case??????
                    ProblemCase oldProblemCase = oldProblemMap.get(problemCase.getId());
                    if (!oldProblemCase.getInput().equals(problemCase.getInput()) ||
                            !oldProblemCase.getOutput().equals(problemCase.getOutput())) {
                        needUpdateProblemCaseList.add(problemCase);
                    } else if (problem.getType().intValue() == Constants.Contest.TYPE_OI.getCode()) {
                        // ????????????
                        if (!Objects.equals(oldProblemCase.getScore(), problemCase.getScore())) {
                            needUpdateProblemCaseList.add(problemCase);
                        }
                    }
                } else {
                    newProblemCaseList.add(problemCase.setPid(pid));
                }

                if (problemCase.getScore() != null) {
                    sumScore += problemCase.getScore();
                }
            }
            // ??????oi??????????????????????????????????????????
            if (problem.getType().intValue() == Constants.Contest.TYPE_OI.getCode()) {
                problem.setIoScore(sumScore);
            }
            // ????????????????????????
            boolean deleteCasesFromProblemResult = true;
            if (needDeleteProblemCases.size() > 0) {
                deleteCasesFromProblemResult = problemCaseEntityService.removeByIds(needDeleteProblemCases);
            }
            // ????????????????????????
            boolean addCasesToProblemResult = true;
            if (newProblemCaseList.size() > 0) {
                addCasesToProblemResult = problemCaseEntityService.saveBatch(newProblemCaseList);
            }
            // ????????????????????????
            boolean updateCasesToProblemResult = true;
            if (needUpdateProblemCaseList.size() > 0) {
                updateCasesToProblemResult = problemCaseEntityService.saveOrUpdateBatch(needUpdateProblemCaseList);
            }
            checkProblemCase = addCasesToProblemResult && deleteCasesFromProblemResult && updateCasesToProblemResult;

            // ???????????????????????????????????????????????????????????? ????????????????????????
            String caseVersion = String.valueOf(System.currentTimeMillis());
            String testcaseDir = problemDto.getUploadTestcaseDir();
            if (needDeleteProblemCases.size() > 0 || newProblemCaseList.size() > 0
                    || needUpdateProblemCaseList.size() > 0 || !StringUtils.isEmpty(testcaseDir)) {
                problem.setCaseVersion(caseVersion);
                // ??????????????????????????????????????????????????????????????????????????????????????????????????????,????????????????????????????????????
                if (problemDto.getIsUploadTestCase()) {
                    // ????????????bean????????????????????????===???????????????????????????info
                    applicationContext.getBean(ProblemEntityServiceImpl.class).initUploadTestCase(problemDto.getJudgeMode(), caseVersion, pid, testcaseDir, problemDto.getSamples());
                } else {
                    applicationContext.getBean(ProblemEntityServiceImpl.class).initHandTestCase(problemDto.getJudgeMode(), problem.getCaseVersion(), pid, problemDto.getSamples());
                }
            }
            // ?????????spj???interactive???????????? ????????????????????????
            else if (problemDto.getChangeModeCode() != null && problemDto.getChangeModeCode()) {
                problem.setCaseVersion(caseVersion);
                if (problemDto.getIsUploadTestCase()) {
                    // ????????????bean????????????????????????===???????????????????????????info
                    applicationContext.getBean(ProblemEntityServiceImpl.class).initUploadTestCase(problemDto.getJudgeMode(), caseVersion, pid, null, problemDto.getSamples());
                } else {
                    applicationContext.getBean(ProblemEntityServiceImpl.class).initHandTestCase(problemDto.getJudgeMode(), problem.getCaseVersion(), pid, problemDto.getSamples());
                }
            }
        }

        // ??????problem???
        boolean problemUpdateResult = problemMapper.updateById(problem) == 1;

        if (problemUpdateResult && checkProblemCase && deleteLanguagesFromProblemResult && deleteTagsFromProblemResult
                && addLanguagesToProblemResult && addTagsToProblemResult && deleteTemplate && saveOrUpdateCodeTemplate) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adminAddProblem(ProblemDto problemDto) {

        Problem problem = problemDto.getProblem();

        if (Constants.JudgeMode.DEFAULT.getMode().equals(problemDto.getJudgeMode())) {
            problem.setSpjLanguage(null)
                    .setSpjCode(null);
        }

        
        String problemId = problem.getProblemId().toUpperCase();
        QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.eq("problem_id", problemId);
        int existedProblem = problemMapper.selectCount(problemQueryWrapper);
        if (existedProblem > 0) {
            throw new RuntimeException("The problem_id [" + problemId + "] already exists. Do not reuse it!");
        }

        // ??????????????????????????????
        problem.setCaseVersion(String.valueOf(System.currentTimeMillis()));
        problem.setProblemId(problemId);
        boolean addProblemResult = problemMapper.insert(problem) == 1;
        long pid = problem.getId();
        // ??????????????????????????????language
        List<ProblemLanguage> problemLanguageList = new LinkedList<>();
        for (Language language : problemDto.getLanguages()) {
            problemLanguageList.add(new ProblemLanguage().setPid(pid).setLid(language.getId()));
        }
        boolean addLangToProblemResult = problemLanguageEntityService.saveOrUpdateBatch(problemLanguageList);

        // ??????????????????????????????codeTemplate
        boolean addProblemCodeTemplate = true;
        if (problemDto.getCodeTemplates() != null && problemDto.getCodeTemplates().size() > 0) {
            for (CodeTemplate codeTemplate : problemDto.getCodeTemplates()) {
                codeTemplate.setPid(pid);
            }
            addProblemCodeTemplate = codeTemplateEntityService.saveOrUpdateBatch(problemDto.getCodeTemplates());
        }

        boolean addCasesToProblemResult = true;
        // ??????????????????????????????case
        if (problemDto.getIsUploadTestCase()) { // ??????????????????????????????????????????????????????????????????????????????????????????
            int sumScore = 0;
            String testcaseDir = problemDto.getUploadTestcaseDir();
            // ?????????io??????????????????
            List<ProblemCase> problemCases = problemDto.getSamples();
            if (problemCases.size() == 0) {
                throw new RuntimeException("The test cases of problem must not be empty!");
            }
            for (ProblemCase problemCase : problemCases) {
                if (problemCase.getScore() != null) {
                    sumScore += problemCase.getScore();
                }
                if (StringUtils.isEmpty(problemCase.getOutput())) {
                    String filePreName = problemCase.getInput().split("\\.")[0];
                    problemCase.setOutput(filePreName + ".out");
                }
                problemCase.setPid(pid);
            }
            // ??????oi??????????????????????????????????????????
            if (problem.getType().intValue() == Constants.Contest.TYPE_OI.getCode()) {
                UpdateWrapper<Problem> problemUpdateWrapper = new UpdateWrapper<>();
                problemUpdateWrapper.eq("id", pid)
                        .set("io_score", sumScore);
                problemMapper.update(null, problemUpdateWrapper);
            }
            addCasesToProblemResult = problemCaseEntityService.saveOrUpdateBatch(problemCases);
            // ????????????bean????????????????????????===???????????????????????????info
            applicationContext.getBean(ProblemEntityServiceImpl.class).initUploadTestCase(problemDto.getJudgeMode(),
                    problem.getCaseVersion(), pid, testcaseDir, problemDto.getSamples());
        } else {
            // oi??????????????????????????????????????????????????????oi???score??????????????????100???
            if (problem.getType().intValue() == Constants.Contest.TYPE_OI.getCode()) {
                int sumScore = 0;
                for (ProblemCase problemCase : problemDto.getSamples()) {
                    // ?????????????????????pid??????????????????
                    problemCase.setPid(pid);
                    if (problemCase.getScore() != null) {
                        sumScore += problemCase.getScore();
                    }
                }
                addCasesToProblemResult = problemCaseEntityService.saveOrUpdateBatch(problemDto.getSamples());
                UpdateWrapper<Problem> problemUpdateWrapper = new UpdateWrapper<>();
                problemUpdateWrapper.eq("id", pid)
                        .set("io_score", sumScore);
                problemMapper.update(null, problemUpdateWrapper);
            } else {
                problemDto.getSamples().forEach(problemCase -> problemCase.setPid(pid)); // ?????????????????????pid
                addCasesToProblemResult = problemCaseEntityService.saveOrUpdateBatch(problemDto.getSamples());
            }
            initHandTestCase(problemDto.getJudgeMode(), problem.getCaseVersion(), pid, problemDto.getSamples());
        }

        // ??????????????????????????????tag?????????tag????????????????????????????????????????????????????????????
        List<ProblemTag> problemTagList = new LinkedList<>();
        if (problemDto.getTags() != null) {
            for (Tag tag : problemDto.getTags()) {
                if (tag.getId() == null) { //id?????? ????????????tag?????????????????? ?????????????????????????????????tagId
                    Tag existedTag = tagEntityService.getOne(new QueryWrapper<Tag>().eq("name", tag.getName())
                            .eq("oj", "ME"), false);
                    if (existedTag == null) {
                        tag.setOj("ME");
                        tagEntityService.save(tag);
                    } else {
                        tag = existedTag;
                    }
                }
                problemTagList.add(new ProblemTag().setTid(tag.getId()).setPid(pid));
            }
        }
        boolean addTagsToProblemResult = true;
        if (problemTagList.size() > 0) {
            addTagsToProblemResult = problemTagEntityService.saveOrUpdateBatch(problemTagList);
        }

        if (addProblemResult && addCasesToProblemResult && addLangToProblemResult
                && addTagsToProblemResult && addProblemCodeTemplate) {
            return true;
        } else {
            return false;
        }
    }

    // ?????????????????????????????????????????????json??????
    @Async
    public void initUploadTestCase(String mode,
                                   String version,
                                   Long problemId,
                                   String tmpTestcaseDir,
                                   List<ProblemCase> problemCaseList) {

        String testCasesDir = Constants.File.TESTCASE_BASE_FOLDER.getPath() + File.separator + "problem_" + problemId;

        // ??????????????????????????????????????????????????????????????????????????????(??????)
        if (!StringUtils.isEmpty(tmpTestcaseDir)) {
            FileUtil.clean(testCasesDir);
            FileUtil.copyFilesFromDir(new File(tmpTestcaseDir), new File(testCasesDir), true);
        }

        JSONObject result = new JSONObject();
        result.set("mode", mode);
        result.set("version", version);
        result.set("testCasesSize", problemCaseList.size());

        JSONArray testCaseList = new JSONArray(problemCaseList.size());

        for (ProblemCase problemCase : problemCaseList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("caseId", problemCase.getId());
            jsonObject.set("score", problemCase.getScore());
            jsonObject.set("inputName", problemCase.getInput());
            jsonObject.set("outputName", problemCase.getOutput());

            // ??????????????????
            FileReader inputFile = new FileReader(testCasesDir + File.separator + problemCase.getInput(), CharsetUtil.UTF_8);
            String input = inputFile.readString().replaceAll("\r\n", "\n");

            FileWriter inputFileWriter = new FileWriter(testCasesDir + File.separator + problemCase.getInput(), CharsetUtil.UTF_8);
            inputFileWriter.write(input);

            // ??????????????????
            FileReader outputFile = new FileReader(testCasesDir + File.separator + problemCase.getOutput(), CharsetUtil.UTF_8);
            String output = outputFile.readString().replaceAll("\r\n", "\n");

            FileWriter outFileWriter = new FileWriter(testCasesDir + File.separator + problemCase.getOutput(), CharsetUtil.UTF_8);
            outFileWriter.write(output);

            // spj???interactive???????????????????????????????????????????????????????????????????????????
            if (Constants.JudgeMode.DEFAULT.getMode().equals(mode)) {
                // ?????????MD5
                jsonObject.set("outputMd5", DigestUtils.md5DigestAsHex(output.getBytes(StandardCharsets.UTF_8)));
                // ???????????????
                jsonObject.set("outputSize", output.getBytes(StandardCharsets.UTF_8).length);
                // ?????????????????????MD5???????????????pe
                jsonObject.set("allStrippedOutputMd5", DigestUtils.md5DigestAsHex(output.replaceAll("\\s+", "").getBytes(StandardCharsets.UTF_8)));
                // ???????????????????????????MD5
                jsonObject.set("EOFStrippedOutputMd5", DigestUtils.md5DigestAsHex(rtrim(output).getBytes(StandardCharsets.UTF_8)));
            }

            testCaseList.add(jsonObject);
        }

        result.set("testCases", testCaseList);

        FileWriter infoFile = new FileWriter(testCasesDir + "/info", CharsetUtil.UTF_8);
        // ??????????????????
        infoFile.write(JSONUtil.toJsonStr(result));
        // ???????????????????????????
        FileUtil.del(tmpTestcaseDir);
    }

    // ???????????????????????????????????????????????????json??????
    @Async
    public void initHandTestCase(String mode,
                                 String version,
                                 Long problemId,
                                 List<ProblemCase> problemCaseList) {

        JSONObject result = new JSONObject();
        result.set("mode", mode);
        result.set("version", version);
        result.set("testCasesSize", problemCaseList.size());

        JSONArray testCaseList = new JSONArray(problemCaseList.size());

        String testCasesDir = Constants.File.TESTCASE_BASE_FOLDER.getPath() + File.separator + "problem_" + problemId;
        FileUtil.del(testCasesDir);
        for (int index = 0; index < problemCaseList.size(); index++) {
            JSONObject jsonObject = new JSONObject();
            String inputName = (index + 1) + ".in";
            jsonObject.set("caseId", problemCaseList.get(index).getId());
            jsonObject.set("score", problemCaseList.get(index).getScore());
            jsonObject.set("inputName", inputName);
            // ??????????????????
            FileWriter infileWriter = new FileWriter(testCasesDir + "/" + inputName, CharsetUtil.UTF_8);
            // ??????????????????????????????????????????
            String inputData = problemCaseList.get(index).getInput().replaceAll("\r\n", "\n");
            infileWriter.write(inputData);

            String outputName = (index + 1) + ".out";
            jsonObject.set("outputName", outputName);
            // ??????????????????
            String outputData = problemCaseList.get(index).getOutput().replaceAll("\r\n", "\n");
            FileWriter outFile = new FileWriter(testCasesDir + "/" + outputName, CharsetUtil.UTF_8);
            outFile.write(outputData);

            // spj???interactive???????????????????????????????????????????????????????????????????????????
            if (Constants.JudgeMode.DEFAULT.getMode().equals(mode)) {
                // ?????????MD5
                jsonObject.set("outputMd5", DigestUtils.md5DigestAsHex(outputData.getBytes(StandardCharsets.UTF_8)));
                // ???????????????
                jsonObject.set("outputSize", outputData.getBytes(StandardCharsets.UTF_8).length);
                // ?????????????????????MD5???????????????pe
                jsonObject.set("allStrippedOutputMd5", DigestUtils.md5DigestAsHex(outputData.replaceAll("\\s+", "").getBytes(StandardCharsets.UTF_8)));
                // ???????????????????????????MD5
                jsonObject.set("EOFStrippedOutputMd5", DigestUtils.md5DigestAsHex(rtrim(outputData).getBytes(StandardCharsets.UTF_8)));
            }

            testCaseList.add(jsonObject);
        }

        result.set("testCases", testCaseList);

        FileWriter infoFile = new FileWriter(testCasesDir + "/info", CharsetUtil.UTF_8);
        // ??????????????????
        infoFile.write(JSONUtil.toJsonStr(result));
    }

    @Override
    @SuppressWarnings("All")
    public ImportProblemVo buildExportProblem(Long pid, List<HashMap<String, Object>> problemCaseList,
                                              HashMap<Long, String> languageMap, HashMap<Long, String> tagMap) {
        // ?????????????????????
        ImportProblemVo importProblemVo = new ImportProblemVo();
        Problem problem = problemMapper.selectById(pid);
        problem.setCaseVersion(null)
                .setGmtCreate(null)
                .setId(null)
                .setAuth(1)
                .setIsUploadCase(true)
                .setAuthor(null)
                .setGmtModified(null);
        HashMap<String, Object> problemMap = new HashMap<>();
        BeanUtil.beanToMap(problem, problemMap, false, true);
        importProblemVo.setProblem(problemMap);
        QueryWrapper<CodeTemplate> codeTemplateQueryWrapper = new QueryWrapper<>();
        codeTemplateQueryWrapper.eq("pid", pid).eq("status", true);
        List<CodeTemplate> codeTemplates = codeTemplateEntityService.list(codeTemplateQueryWrapper);
        List<HashMap<String, String>> codeTemplateList = new LinkedList<>();
        for (CodeTemplate codeTemplate : codeTemplates) {
            HashMap<String, String> tmp = new HashMap<>();
            tmp.put("language", languageMap.get(codeTemplate.getLid()));
            tmp.put("code", codeTemplate.getCode());
            codeTemplateList.add(tmp);
        }
        importProblemVo.setCodeTemplates(codeTemplateList);
        importProblemVo.setJudgeMode(problem.getJudgeMode());
        importProblemVo.setSamples(problemCaseList);

        if (!StringUtils.isEmpty(problem.getUserExtraFile())) {
            HashMap<String, String> userExtraFileMap = (HashMap<String, String>) JSONUtil.toBean(problem.getUserExtraFile(), Map.class);
            importProblemVo.setUserExtraFile(userExtraFileMap);
        }

        if (!StringUtils.isEmpty(problem.getJudgeExtraFile())) {
            HashMap<String, String> judgeExtraFileMap = (HashMap<String, String>) JSONUtil.toBean(problem.getJudgeExtraFile(), Map.class);
            importProblemVo.setUserExtraFile(judgeExtraFileMap);
        }

        QueryWrapper<ProblemTag> problemTagQueryWrapper = new QueryWrapper<>();
        problemTagQueryWrapper.eq("pid", pid);
        List<ProblemTag> problemTags = problemTagEntityService.list(problemTagQueryWrapper);
        importProblemVo.setTags(problemTags.stream().map(problemTag -> tagMap.get(problemTag.getTid())).collect(Collectors.toList()));

        QueryWrapper<ProblemLanguage> problemLanguageQueryWrapper = new QueryWrapper<>();
        problemLanguageQueryWrapper.eq("pid", pid);
        List<ProblemLanguage> problemLanguages = problemLanguageEntityService.list(problemLanguageQueryWrapper);
        importProblemVo.setLanguages(problemLanguages.stream().map(problemLanguage -> languageMap.get(problemLanguage.getLid())).collect(Collectors.toList()));

        return importProblemVo;
    }

    // ??????????????????????????????
    public static String rtrim(String value) {
        if (value == null) return null;
        StringBuilder sb = new StringBuilder();
        String[] strArr = value.split("\n");
        for (String str : strArr) {
            sb.append(str.replaceAll("\\s+$", "")).append("\n");
        }
        return sb.toString().replaceAll("\\s+$", "");
    }

}
