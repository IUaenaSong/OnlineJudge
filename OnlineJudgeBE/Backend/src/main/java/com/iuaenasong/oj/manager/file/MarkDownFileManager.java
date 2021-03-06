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
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        if (image == null) {
            throw new StatusFailException("??????????????????????????????");
        }
        if (image.getSize() > 1024 * 1024 * 4) {
            throw new StatusFailException("???????????????????????????????????????4M???");
        }
        //??????????????????
        String suffix = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1);
        if (!"jpg,jpeg,gif,png,webp".toUpperCase().contains(suffix.toUpperCase())) {
            throw new StatusFailException("?????????jpg,jpeg,gif,png,webp??????????????????");
        }

        //???????????????????????????????????????
        FileUtil.mkdir(Constants.File.MARKDOWN_FILE_FOLDER.getPath());

        //??????UUID?????????????????????
        String filename = IdUtil.simpleUUID() + "." + suffix;
        try {
            //???????????????????????????
            image.transferTo(FileUtil.file(Constants.File.MARKDOWN_FILE_FOLDER.getPath() + File.separator + filename));
        } catch (Exception e) {
            log.error("????????????????????????-------------->", e);
            throw new StatusSystemErrorException("?????????????????????????????????????????????");
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
            throw new StatusFailException("???????????????????????????");
        }

        if (!file.getType().equals("md")) {
            throw new StatusForbiddenException("???????????????????????????");
        }

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");

        Long gid = file.getGid();

        if (!file.getUid().equals(userRolesVo.getUid()) && !isRoot && !isProblemAdmin && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        boolean isOk = FileUtil.del(file.getFilePath());
        if (isOk) {
            fileEntityService.removeById(fileId);
        } else {
            throw new StatusFailException("????????????");
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
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        if (file == null) {
            throw new StatusFailException("??????????????????????????????");
        }
        if (file.getSize() >= 1024 * 1024 * 128) {
            throw new StatusFailException("?????????????????????????????????128M???");
        }
        //??????????????????
        String suffix = "";
        String filename = "";
        if (file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")) {
            suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            //??????UUID?????????????????????
            filename = IdUtil.simpleUUID() + "." + suffix;
        } else {
            filename = IdUtil.simpleUUID();
        }
        //???????????????????????????????????????
        FileUtil.mkdir(Constants.File.MARKDOWN_FILE_FOLDER.getPath());

        try {
            //???????????????????????????
            file.transferTo(FileUtil.file(Constants.File.MARKDOWN_FILE_FOLDER.getPath() + File.separator + filename));
        } catch (Exception e) {
            log.error("??????????????????-------------->", e);
            throw new StatusSystemErrorException("???????????????????????????????????????");
        }

        return MapUtil.builder()
                .put("link", Constants.File.FILE_API.getPath() + filename)
                .map();
    }

}