/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.file;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface UserFileService {

    public void generateUserExcel(String key, HttpServletResponse response) throws IOException;
}
