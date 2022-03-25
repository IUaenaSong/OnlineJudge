/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.crawler.language;

import com.iuaenasong.oj.pojo.entity.problem.Language;
import com.iuaenasong.oj.utils.Constants;

;import java.util.List;

public class LanguageContext {

    private LanguageStrategy languageStrategy;

    public LanguageContext(LanguageStrategy languageStrategy) {
        this.languageStrategy = languageStrategy;
    }

    public LanguageContext(Constants.RemoteOJ remoteOJ) {
        switch (remoteOJ) {
            case SPOJ:
                languageStrategy = new SPOJLanguageStrategy();
                break;
            case ATCODER:
                languageStrategy = new AtCoderLanguageStrategy();
                break;
            default:
                throw new RuntimeException("未知的OJ的名字，暂时不支持！");
        }
    }

    public List<Language> buildLanguageList(){
        return languageStrategy.buildLanguageList();
    }

    public String getLanguageNameById(String id) {
        return languageStrategy.getLanguageNameById(id);
    }

    public List<Language> buildLanguageListByIds(List<Language> allLanguageList, List<String> langIdList) {
        return languageStrategy.buildLanguageListByIds(allLanguageList, langIdList);
    }
}