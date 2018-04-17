//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package blog.csdn.net.mchenys.common.sns.config;


import blog.csdn.net.mchenys.common.sns.autho.SnsSSOLoginEngine;
import blog.csdn.net.mchenys.common.sns.share.SnsShareEngine;

/**
 * 管理分享和登录的实例
 */
public class SnsManager {
    private static SnsManager SnsManager;
    private SnsShareEngine mSnsShareEngine;
    private SnsSSOLoginEngine mSnsSSOLoginEngine;



    public static SnsManager getInstance() {
        if(SnsManager == null) {
            synchronized(SnsManager.class) {
                if(SnsManager == null) {
                    SnsManager = new SnsManager();
                }
            }
        }

        return SnsManager;
    }

    public static SnsShareEngine getSnsShare() {
        if(getInstance().mSnsShareEngine == null) {
            synchronized(SnsManager.class) {
                if(getInstance().mSnsShareEngine == null) {
                    getInstance().mSnsShareEngine = new SnsShareEngine();
                }
            }
        }

        return getInstance().mSnsShareEngine;
    }

    public static void setSnsShare(SnsShareEngine snsShareEngine) {
        getInstance().mSnsShareEngine = snsShareEngine;
    }

    public static SnsSSOLoginEngine getSSOLogin() {
        if(getInstance().mSnsSSOLoginEngine == null) {
            synchronized(SnsManager.class) {
                if(getInstance().mSnsSSOLoginEngine == null) {
                    getInstance().mSnsSSOLoginEngine = new SnsSSOLoginEngine();
                }
            }
        }

        return getInstance().mSnsSSOLoginEngine;
    }
}
