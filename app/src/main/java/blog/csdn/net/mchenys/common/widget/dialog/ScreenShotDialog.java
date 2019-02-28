package blog.csdn.net.mchenys.common.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.config.Env;
import blog.csdn.net.mchenys.common.sns.SnsImageShareUtil;
import blog.csdn.net.mchenys.common.sns.bean.SnsShareContent;
import blog.csdn.net.mchenys.common.sns.config.SnsConfig;
import blog.csdn.net.mchenys.common.sns.config.SnsManager;
import blog.csdn.net.mchenys.common.utils.BitmapUtils;
import blog.csdn.net.mchenys.common.utils.DisplayUtils;
import blog.csdn.net.mchenys.common.utils.ShareUtils;
import blog.csdn.net.mchenys.common.utils.StringUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;
import blog.csdn.net.mchenys.common.utils.UIUtils;

/**
 * 截屏弹窗
 * Created by mChenys on 2018/7/26.
 */

public class ScreenShotDialog extends Dialog implements View.OnClickListener, ShareUtils.Callback {
    private ImageView mCoverIv;
    private String imagePath;
    private Activity mActivity;
    private boolean canShare;

    public ScreenShotDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_screen_shot);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = Env.screenWidth - DisplayUtils.dip2px(context, 70);
        window.setAttributes(lp);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        mCoverIv = (ImageView) findViewById(R.id.iv_cover);
        findViewById(R.id.ll_wx_circle).setOnClickListener(this);
        findViewById(R.id.ll_wx_friend).setOnClickListener(this);
        findViewById(R.id.ll_qq_zone).setOnClickListener(this);
        findViewById(R.id.ll_qq_friend).setOnClickListener(this);
        findViewById(R.id.ll_sina).setOnClickListener(this);
        findViewById(R.id.iv_close).setOnClickListener(this);

    }

    public void show(String imagePath) {
        mActivity = UIUtils.scanForActivity(getContext());
        if (mActivity == null || mActivity.isFinishing()) {
            return;
        }
        if (!isShowing() && !StringUtils.isEmpty(imagePath)) {
            this.imagePath = imagePath;
            Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFile(imagePath, mCoverIv.getWidth(), mCoverIv.getHeight());
            mCoverIv.setImageBitmap(bitmap);
            canShare = true;
            show();
        }

    }

    @Override
    public void onClick(View v) {
        SnsShareContent snsShareContent = null;
        if (null == this.imagePath || null == mActivity) {
            ToastUtils.showShort("分享图片失败");
            return;
        } else {
            snsShareContent = ShareUtils.wrapShareContent(null, null, null, null, null, imagePath);
            SnsImageShareUtil.setImage(getContext(), imagePath);
        }
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.ll_wx_circle:
                dismiss();
                ShareUtils.shareWithoutSurface(mActivity, snsShareContent, this, SnsConfig.SHARE_WECHAT_FRIEND);
                break;
            case R.id.ll_wx_friend:
                dismiss();
                ShareUtils.shareWithoutSurface(mActivity, snsShareContent, this, SnsConfig.SHARE_WECHAT);
                break;
            case R.id.ll_qq_zone:
                ShareUtils.shareWithoutSurface(mActivity, snsShareContent, this, SnsConfig.SHARE_TENCENT_ZONE);
                break;
            case R.id.ll_qq_friend:
                dismiss();
                ShareUtils.shareWithoutSurface(mActivity, snsShareContent, this, SnsConfig.SHARE_TENCENT);
                break;
            case R.id.ll_sina:
                dismiss();
                ShareUtils.shareWithoutSurface(mActivity, snsShareContent, this, SnsConfig.SHARE_SINA);
                break;

        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (canShare) {
                SnsManager.getSSOLogin().onActivityResult(requestCode, resultCode, data);
                SnsManager.getSnsShare().onActivityResult(requestCode, resultCode, data);
                canShare = false;
            }

        }
    }


    @Override
    public void onSucceed() {

    }

    @Override
    public void onFailed() {
        canShare = false;
    }
}
