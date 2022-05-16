/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.crawler.problem;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.utils.CodeForcesUtils;
import com.iuaenasong.oj.utils.Constants;

import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GYMProblemStrategy extends CFProblemStrategy {

    public static final String IMAGE_HOST = "https://codeforces.com";

    @Override
    public String getJudgeName() {
        return "GYM";
    }

    @Override
    public String getProblemUrl(String contestId, String problemNum) {
        String problemUrl = "/gym/%s/problem/%s";
        return HOST + String.format(problemUrl, contestId, problemNum);
    }

    @Override
    public String getProblemSource(String html, String problemId, String contestNum, String problemNum) {
        return String.format("<p>Problem：<a style='color:#1A5CC8' href='https://codeforces.com/gym/%s/problem/%s'>%s</a></p><p>" +
                        "Contest：" + ReUtil.get("(<a[^<>]+/gym/\\d+\">.+?</a>)", html, 1)
                        .replace("/gym", HOST + "/gym")
                        .replace("color: black", "color: #009688;") + "</p>",
                contestNum, problemNum, getJudgeName() + "-" + problemId);
    }

    @Override
    public RemoteProblemInfo getProblemInfo(String problemId, String author) {
        try {
            return super.getProblemInfo(problemId, author);
        } catch (Exception ignored) {
            String contestNum = ReUtil.get("([0-9]+)[A-Z]{1}[0-9]{0,1}", problemId, 1);
            String problemNum = ReUtil.get("[0-9]+([A-Z]{1}[0-9]{0,1})", problemId, 1);
            return getPDFHtml(problemId, contestNum, problemNum, author);
        }
    }

    private RemoteProblemInfo getPDFHtml(String problemId, String contestNum, String problemNum, String author) {

        Problem problem = new Problem();

        String url = HOST + "/gym/" + contestNum;
        String html = HttpRequest.get(url)
                .header("cookie", "RCPC=" + CodeForcesUtils.getRCPC())
                .timeout(20000)
                .execute()
                .body();

        // 重定向失效，更新RCPC
        if (html.contains("Redirecting... Please, wait.")) {
            List<String> list = ReUtil.findAll("[a-z0-9]+[a-z0-9]{31}", html, 0, new ArrayList<>());
            CodeForcesUtils.updateRCPC(list);
            html = HttpRequest.get(url)
                    .header("cookie","RCPC="+CodeForcesUtils.getRCPC())
                    .timeout(20000)
                    .execute()
                    .body();
        }

        String regex = "<a href=\"\\/gym\\/" + contestNum + "\\/problem\\/" + problemNum
                + "\"><!--\\s*-->([^<]+)(?:(?:.|\\s)*?<div){2}[^>]*>\\s*([^<]+)<\\/div>\\s*([\\d.]+)\\D*(\\d+)";

        Matcher matcher = Pattern.compile(regex).matcher(html);
        matcher.find();

        problem.setProblemId(getJudgeName() + "-" + problemId);
        problem.setTitle(matcher.group(1));
        problem.setTimeLimit((int) (Double.parseDouble(matcher.group(3)) * 1000));
        problem.setMemoryLimit(Integer.parseInt(matcher.group(4)));

        problem.setSource(String.format("<p>Problem：<a style='color:#1A5CC8' href='https://codeforces.com/gym/%s/attachments'>%s</a></p><p>" +
                        "Contest：" + ReUtil.get("(<a[^<>]+/gym/\\d+\">.+?</a>)", html, 1)
                        .replace("/gym", HOST + "/gym")
                        .replace("color: black", "color: #009688;") + "</p>",
                contestNum, getJudgeName() + "-" + problemId));

        regex = "\\/gym\\/" + contestNum + "\\/attachments\\/download\\S*?\\.pdf";

        matcher = Pattern.compile(regex).matcher(html);
        matcher.find();

        String pdfURI;
        try {
            String fileName = IdUtil.fastSimpleUUID() + ".pdf";
            String filePath = Constants.File.PROBLEM_FILE_FOLDER.getPath() + File.separator + fileName;
            HttpUtil.downloadFile(IMAGE_HOST + matcher.group(0), filePath);
            pdfURI = Constants.File.FILE_API.getPath() + fileName;
        } catch (Exception e1) {
            try {
                pdfURI = HOST + matcher.group(0);
            } catch (Exception e2) {
                String fileName = IdUtil.fastSimpleUUID() + ".pdf";
                String filePath = Constants.File.PROBLEM_FILE_FOLDER.getPath() + File.separator + fileName;
                CodeForcesUtils.downloadPDF(HOST + "/gym/" + contestNum + "/problem/" + problemNum, filePath);
                pdfURI = Constants.File.FILE_API.getPath() + fileName;
            }
        }
        String description = "<p><a style='color:#3091f2' href=\"" + pdfURI + "\">Click here to download the PDF file.</a></p>";
        problem.setDescription(description);
        problem.setType(0)
                .setIsRemote(true)
                .setAuth(1)
                .setAuthor(author)
                .setOpenCaseResult(true)
                .setIsRemoveEndBlank(false)
                .setIsPublic(true)
                .setDifficulty(1); // 默认为中等
        return new RemoteProblemInfo()
                .setProblem(problem)
                .setTagList(null)
                .setRemoteOJ(Constants.RemoteOJ.GYM);
    }
}