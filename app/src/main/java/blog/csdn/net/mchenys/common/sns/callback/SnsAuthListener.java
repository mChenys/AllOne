//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package blog.csdn.net.mchenys.common.sns.callback;

import android.content.Context;

import blog.csdn.net.mchenys.common.sns.bean.SnsUser;


public abstract class SnsAuthListener {
    public SnsAuthListener() {
    }

    public abstract void onSucceeded(Context var1, SnsUser var2);

    public void onFail(Context context, String errMessage) {
    }
}
