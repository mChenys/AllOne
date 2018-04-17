package blog.csdn.net.mchenys.module.terminal;

import android.view.View;
import android.widget.Toast;

import blog.csdn.net.mchenys.common.base.BaseTerminalActivity;
import blog.csdn.net.mchenys.common.sns.bean.SnsShareContent;
import blog.csdn.net.mchenys.common.utils.ShareUtils;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;


/**
 * 商品详情页
 * Created by mChenys on 2017/12/28.
 */

public class ShopDetailActivity extends BaseTerminalActivity {
    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.setLeft(null, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleBar.setRightIcon1(null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //测试分享
                SnsShareContent shareContent = ShareUtils.wrapShareContent("测试分享","我是描述",
                        "我是内容","我是隐藏的","https://www.baidu.com",
                        "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo_top_ca79a146.png");

                ShareUtils.share(mContext, shareContent, new ShareUtils.Callback() {
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
        });
    }
}
