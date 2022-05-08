/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.file;

import org.springframework.web.multipart.MultipartFile;
import com.iuaenasong.oj.common.result.CommonResult;

import java.util.Map;

public interface MarkDownFileService {

    public CommonResult<Map<Object,Object>> uploadMDImg(MultipartFile image, Long gid, Long eid);

    public CommonResult<Void> deleteMDImg(Long fileId);

    public CommonResult<Map<Object,Object>> uploadMd(MultipartFile file, Long gid, Long eid);
}
