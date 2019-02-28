package blog.csdn.net.mchenys.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.Toast;

import java.io.File;

import blog.csdn.net.mchenys.AllOneApplication;
import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.sns.SnsImageShareUtil;
import blog.csdn.net.mchenys.common.sns.activity.SnsSelectPlatformNewActivity;
import blog.csdn.net.mchenys.common.sns.bean.SnsShareContent;
import blog.csdn.net.mchenys.common.sns.callback.SnsShareListener;
import blog.csdn.net.mchenys.common.sns.config.SnsManager;
import blog.csdn.net.mchenys.common.sns.share.SnsShareEngine;


/**
 * 分享工具类
 * Created by mChenys on 2017/12/26.
 */

public class ShareUtils {
    public interface Callback {
        void onSucceed();

        void onFailed();
    }

    /**
     * 通过分享字段包装MFSnsShareContent返回
     *
     * @param title
     * @param desc
     * @param content
     * @param hideContent
     * @param wapurl
     * @param image
     * @return
     */
    public static SnsShareContent wrapShareContent(String title, String desc, String content, String hideContent, String wapurl, String image) {
        SnsShareContent shareEntity = new SnsShareContent();

        if (TextUtils.isEmpty(title)) {//分享到QQ、QQ空间、微信时的标题
            title = "  ";
        }
        if (TextUtils.isEmpty(desc)) {//分享到QQ、QQ空间、微信时的内容简介
            desc = "  ";
        }
        if (TextUtils.isEmpty(content)) {//分享到新浪微博/腾讯微博的自定义文案
            content = desc;
        }
        if (TextUtils.isEmpty(image)) { //图片
            image = "  ";
        }
        if (TextUtils.isEmpty(wapurl)) { //终端url
            wapurl = "  ";
        }
        if (!TextUtils.isEmpty(hideContent)) { //分享到新浪的隐藏内容
            hideContent += " (@太平洋电脑网)";
        }
        shareEntity.setTitle(title);
        shareEntity.setDescription(desc);
        shareEntity.setContent(content);
        shareEntity.setUrl(wapurl);
        shareEntity.setWapUrl(wapurl);
        shareEntity.setHideContent(hideContent);
        if(!TextUtils.isEmpty(image)){
            shareEntity.setImage(image);
            if (!URLUtil.isNetworkUrl(image)) { //本地图片
                shareEntity.setShareImgFile(new File(image));
            }
            SnsImageShareUtil.setImage(AllOneApplication.mAppContext, image);
        }
        return shareEntity;
    }

    /**
     * 直接分享到微博,QQ,微信,微信朋友圈(无界面）
     *
     * @param context
     * @param shareContent
     * @param callback
     * @param type:SnsConfig#SHARE_SINA,SHARE_TENCENT,SHARE_WECHAT,SHARE_WECHAT_FRIEND
     */
    public static void shareWithoutSurface(Context context, SnsShareContent shareContent, Callback callback, int type) {
        if (shareContent == null) return;
        SnsShareListener snsShareListener = getMFSnsShareListener(context, callback);
        if (!TextUtils.isEmpty(shareContent.getImage())) {
            SnsImageShareUtil.setImage(context, shareContent.getImage());
        }
        SnsManager.getSnsShare().share(context, type, shareContent, snsShareListener);
    }


    /**
     * 自定义界面分享
     *
     * @param context
     * @param shareContent
     * @param callback
     */
    public static void share(final Context context, SnsShareContent shareContent, final Callback callback) {
        if (shareContent == null) return;
        shareOri(context, shareContent, new SnsShareListener() {

            @Override
            public void onSucceeded(Context arg0) {
                if (null != callback) {
                    callback.onSucceed();
                }
                if (arg0 instanceof Activity) {
                    ((Activity) arg0).finish();
                }
            }

            @Override
            public void onTencentQQSucceeded(Context context, Object response) {
                Toast.makeText(context, "分享成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSinaSucceeded(Context arg0) {
                Toast.makeText(context, "分享成功！", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailed(Context arg0, String arg1) {
                if (null != callback) {
                    callback.onFailed();
                }
                if (arg0 instanceof Activity) {
                    ((Activity) arg0).finish();
                }
                Toast.makeText(arg0, "分享失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTencentFailed(Context context, Object o) {
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
                Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWeiXinSucceeded(Context arg0) {
                Toast.makeText(context, "分享成功！", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onWeiXinFriendsSucceeded(Context arg0) {
                Toast.makeText(context, "分享成功！", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onWeiXinNoSupported(Context arg0, boolean arg1) {
                if (!arg1) {
                    Toast.makeText(context, "您尚未安装微信或微信版本过低！", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static SnsShareListener getMFSnsShareListener(final Context context, final Callback callback) {
        SnsShareListener snsShareListener = new SnsShareListener() {

            @Override
            public void onSucceeded(Context arg0) {
                if (null != callback) {
                    callback.onSucceed();
                }
            }

            @Override
            public void onTencentQQSucceeded(Context context, Object response) {
                Toast.makeText(context, "分享成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSinaSucceeded(Context arg0) {
                Toast.makeText(context, "分享成功！", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailed(Context arg0, String arg1) {
                if (null != callback) {
                    callback.onFailed();
                }
                Toast.makeText(arg0, "分享失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTencentFailed(Context context, Object o) {
            }

            @Override
            public void onWeiXinSucceeded(Context arg0) {
                Toast.makeText(context, "分享成功！", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onWeiXinFriendsSucceeded(Context arg0) {
                Toast.makeText(context, "分享成功！", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onWeiXinNoSupported(Context arg0, boolean arg1) {
                if (!arg1) {
                    Toast.makeText(context, "您尚未安装微信或微信版本过低！", Toast.LENGTH_SHORT).show();
                }
            }

        };
        return snsShareListener;
    }

    public static void shareOri(Context context, SnsShareContent content, SnsShareListener listener) {
        Intent intent = new Intent(context, SnsSelectPlatformNewActivity.class);
        intent.putExtra("content", content);
        SnsSelectPlatformNewActivity.setShareListener(listener);
        SnsManager.setSnsShare(new SnsShareEngine());
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.bottom_fade_in, R.anim.bottom_fade_out);
    }

    public static void shareOri(Context context, SnsShareContent content, SnsShareListener listener, int animIn,
                                int animOut) {
        Intent intent = new Intent(context, SnsSelectPlatformNewActivity.class);
        intent.putExtra("content", content);
        SnsSelectPlatformNewActivity.setShareListener(listener);
        SnsManager.setSnsShare(new SnsShareEngine());
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(animIn, animOut);
    }
}
