package blog.csdn.net.mchenys.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import blog.csdn.net.mchenys.common.sns.config.SnsConfig;
import blog.csdn.net.mchenys.common.sns.config.SnsManager;


/**
 * 该包不可移动位置，和微信分享,登录有关
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, SnsConfig.CONSUMER_WEIXIN_APPID, true);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        dispatchResp(resp);
        this.finish();
    }

    /**
     * 区分是从微信登录返回还是从分享返回
     *
     * @param resp
     */
    private void dispatchResp(BaseResp resp) {
        switch (resp.getType()) {
            case ConstantsAPI.COMMAND_SENDAUTH: //授权登录成功
                SnsManager.getSSOLogin().weChatCallBack(resp);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL://取消登录
                SnsManager.getSSOLogin().weChatCallBack(resp);
                break;
            case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX://分享回调成功
                SnsManager.getSnsShare().wxShareCallback(resp);
                break;

        }
    }
}
