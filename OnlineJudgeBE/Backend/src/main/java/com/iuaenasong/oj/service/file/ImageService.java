/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.file;

import org.springframework.web.multipart.MultipartFile;
import com.iuaenasong.oj.common.result.CommonResult;

import java.util.Map;

public interface ImageService {

    public CommonResult<Map<Object,Object>> uploadAvatar(MultipartFile image);

    public CommonResult<Map<Object,Object>> uploadGroupAvatar(MultipartFile image, Long gid);

    public CommonResult<Map<Object,Object>> uploadCarouselImg(MultipartFile image);
}
