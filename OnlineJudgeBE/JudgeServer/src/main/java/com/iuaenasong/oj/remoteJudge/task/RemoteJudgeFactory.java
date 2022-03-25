/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.remoteJudge.task;

import com.iuaenasong.oj.remoteJudge.task.Impl.*;
import com.iuaenasong.oj.util.Constants;

public class RemoteJudgeFactory {

    public static RemoteJudgeStrategy selectJudge(String judgeName) {
        Constants.RemoteJudge remoteJudge = Constants.RemoteJudge.getTypeByName(judgeName);
        switch (remoteJudge) {
            case HDU_JUDGE:
                return new HduJudge();
            case CF_JUDGE:
                return new CodeForcesJudge();
            case POJ_JUDGE:
                return new POJJudge();
            case GYM_JUDGE:
                return new GYMJudge();
            case SPOJ_JUDGE:
                return new SPOJJudge();
            case ATCODER_JUDGE:
                return new AtCoderJudge();
            default:
                return null;
        }
    }
}
