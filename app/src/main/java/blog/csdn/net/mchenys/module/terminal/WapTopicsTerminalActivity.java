package blog.csdn.net.mchenys.module.terminal;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONObject;

import blog.csdn.net.mchenys.common.base.BaseTerminalActivity;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.sns.bean.SnsShareContent;
import blog.csdn.net.mchenys.common.utils.NetworkUtils;
import blog.csdn.net.mchenys.common.utils.ShareUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;

/**
 * 长图文话题终端页
 * Created by mChenys on 2017/2/9.
 */
public class WapTopicsTerminalActivity extends BaseTerminalActivity {

    private String topicId;

    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setLeft(null, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleBar.setCenterTv("话题详情");
        titleBar.setRightIcon1(null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpenShare();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        setLoadType(TYPE_URL);
        topicId = getIntent().getStringExtra("topicId");
        url = Urls.WAP_TOPIC_TERMINAL + "?deviceId=1fb16ae0d28233c3ad79ade0686e0bd8&topicId=" + topicId + "&currentUserId=0&platform=android";

    }

    private void changeChildSelected(ViewGroup parent, boolean selected) {
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            view.setSelected(selected);
        }
    }


    private void onOpenShare() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            if (isLoadComplete()) {    //内容加载完才允许分享
                showShare();
            } else {
                ToastUtils.showShort(this, "请等待内容加载完...");
            }
        } else {
            ToastUtils.showShort(this, "网络异常");
        }
    }

    private void showShare() {
        JSONObject obj = getMetaData();
        if (null != obj) {
            String image = obj.optString("shareImg");
            String wapurl = obj.optString("shareUrl");//分享的url,和wrapUrl一样
            String title = obj.optString("shareTitle");//标题
            String desc = obj.optString("shareDesc");//描述
            String content = obj.optString("shareContent");

            SnsShareContent shareContent = ShareUtils.wrapShareContent(title, desc, content, null, wapurl, image);
            ShareUtils.share(this, shareContent,  new ShareUtils.Callback() {
                @Override
                public void onSucceed() {
                    Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed() {
                    Toast.makeText(mContext, "分享失败", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


}