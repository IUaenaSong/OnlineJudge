/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import com.iuaenasong.oj.dao.exam.ExamEntityService;
import com.iuaenasong.oj.pojo.entity.exam.Exam;
import com.iuaenasong.oj.validator.ExamValidator;
import com.iuaenasong.oj.validator.GroupValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusSystemErrorException;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.dao.common.FileEntityService;
import com.iuaenasong.oj.utils.Constants;

import javax.annotation.Resource;
import java.io.File;
import java.util.Map;

@Component
@Slf4j(topic = "oj")
public class MarkDownFileManager {

    @Resource
    private FileEntityService fileEntityService;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private ExamEntityService examEntityService;

    @Autowired
    private ExamValidator examValidator;

    public Map<Object, Object> uploadMDImg(MultipartFile image, Long gid, Long eid) throws StatusFailException, StatusSystemErrorException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        Exam exam = examEntityService.getById(eid);

        if (exam != null) {
            examValidator.validateExamAuth(exam, userRolesVo, isRoot);
        }

        if (!isRoot && !isProblemAdmin && !isAdmin && !groupValidator.isGroupMember(userRolesVo.getUid(), gid) && exam == null) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        if (image == null) {
            throw new StatusFailException("上传的图片不能为空！");
        }
        if (image.getSize() > 1024 * 1024 * 4) {
            throw new StatusFailException("上传的图片文件大小不能大于4M！");
        }
        //获取文件后缀
        String suffix = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1);
        if (!"jpg,jpeg,gif,png,webp".toUpperCase().contains(suffix.toUpperCase())) {
            throw new StatusFailException("请选择jpg,jpeg,gif,png,webp格式的图片！");
        }

        //若不存在该目录，则创建目录
        FileUtil.mkdir(Constants.File.MARKDOWN_FILE_FOLDER.getPath());

        //通过UUID生成唯一文件名
        String filename = IdUtil.simpleUUID() + "." + suffix;
        try {
            //将文件保存指定目录
            image.transferTo(FileUtil.file(Constants.File.MARKDOWN_FILE_FOLDER.getPath() + File.separator + filename));
        } catch (Exception e) {
            log.error("图片文件上传异常-------------->", e);
            throw new StatusSystemErrorException("服务器异常：图片文件上传失败！");
        }

        com.iuaenasong.oj.pojo.entity.common.File file = new com.iuaenasong.oj.pojo.entity.common.File();
        file.setFolderPath(Constants.File.MARKDOWN_FILE_FOLDER.getPath())
                .setName(filename)
                .setFilePath(Constants.File.MARKDOWN_FILE_FOLDER.getPath() + File.separator + filename)
                .setSuffix(suffix)
                .setType("md")
                .setUid(userRolesVo.getUid());
        fileEntityService.save(file);

        return MapUtil.builder()
                .put("link", Constants.File.IMG_API.getPath() + filename)
                .put("fileId", file.getId()).map();

    }

    public void deleteMDImg(Long fileId) throws StatusFailException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        com.iuaenasong.oj.pojo.entity.common.File file = fileEntityService.getById(fileId);

        if (file == null) {
            throw new StatusFailException("错误：文件不存在！");
        }

        if (!file.getType().equals("md")) {
            throw new StatusForbiddenException("错误：不支持删除！");
        }

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");

        Long gid = file.getGid();

        if (!file.getUid().equals(userRolesVo.getUid()) && !isRoot && !isProblemAdmin && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        boolean isOk = FileUtil.del(file.getFilePath());
        if (isOk) {
            fileEntityService.removeById(fileId);
        } else {
            throw new StatusFailException("删除失败");
        }
    }

    public Map<Object, Object> uploadMd(MultipartFile file, Long gid, Long eid) throws StatusFailException, StatusSystemErrorException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        Exam exam = examEntityService.getById(eid);

        if (exam != null) {
            examValidator.validateExamAuth(exam, userRolesVo, isRoot);
        }

        if (!isRoot && !isProblemAdmin && !isAdmin && !groupValidator.isGroupAdmin(userRolesVo.getUid(), gid) && exam == null) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        if (file == null) {
            throw new StatusFailException("上传的文件不能为空！");
        }
        if (file.getSize() >= 1024 * 1024 * 128) {
            throw new StatusFailException("上传的文件大小不能大于128M！");
        }
        //获取文件后缀
        String suffix = "";
        String filename = "";
        if (file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")) {
            suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            //通过UUID生成唯一文件名
            filename = IdUtil.simpleUUID() + "." + suffix;
        } else {
            filename = IdUtil.simpleUUID();
        }
        //若不存在该目录，则创建目录
        FileUtil.mkdir(Constants.File.MARKDOWN_FILE_FOLDER.getPath());

        try {
            //将文件保存指定目录
            file.transferTo(FileUtil.file(Constants.File.MARKDOWN_FILE_FOLDER.getPath() + File.separator + filename));
        } catch (Exception e) {
            log.error("文件上传异常-------------->", e);
            throw new StatusSystemErrorException("服务器异常：文件上传失败！");
        }

        return MapUtil.builder()
                .put("link", Constants.File.FILE_API.getPath() + filename)
                .map();
    }

}