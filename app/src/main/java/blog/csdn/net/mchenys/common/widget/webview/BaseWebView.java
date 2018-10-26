package blog.csdn.net.mchenys.common.widget.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.utils.AccountUtils;
import blog.csdn.net.mchenys.module.account.LoginActivity;


/**
 * 客户端 webview 基类
 * <p>
 * Created by mChenys on 2017/12/28.
 */
public class BaseWebView extends WebView {
    private static final String TAG = "BaseWebView";
    public static final String WEBVIEW_LOGIN = "pcaction://user-browser-user-center?callback=";
    private WebSettings mWebSettings;
    private Context mContext;
    private String htmlContent;
    public interface HTMLCallback {
        void onSuccess(String htmlContent);
    }
    private HTMLCallback mHTMLCallback;

    public void setHTMLCallback(HTMLCallback c) {
        mHTMLCallback = c;
    }

    public BaseWebView(Context context) {
        this(context, null);
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MyWebViewJavaScriptSInterface.CODE_GET_HTML:
                    htmlContent = (String) msg.obj;
                    if (null != mHTMLCallback) mHTMLCallback.onSuccess(htmlContent);
                    break;
                default:
                    String callBack = (String) msg.obj;
                    BaseWebView.this.loadUrl("javaScript:" + callBack + "(" + msg.what + ")");
                    break;
            }
            return false;
        }
    });

    /**
     * 如果客户端已经登录，进入网页时需要同步Cookie
     *
     * @param url
     */
    public void syncCookie(String url) {
        if (null == url || "".equals(url)) return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(mContext);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        if (AccountUtils.isLogin()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.setCookie(url, Urls.COMMON_SESSION_ID + AccountUtils.getSessionId());//为url设置cookie
                cookieManager.flush();//同步cookie
            } else {
                cookieManager.removeSessionCookie();
                cookieManager.removeAllCookie();
                cookieManager.setCookie(url, Urls.COMMON_SESSION_ID + AccountUtils.getSessionId());//为url设置cookie
                CookieSyncManager.getInstance().sync();
            }
        } else {
            cookieManager.removeSessionCookie();
            cookieManager.setCookie(url, Urls.COMMON_SESSION_ID);
        }
    }

    public void init(Context context) {
        this.mContext = context;
        mWebSettings = getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebSettings.setLoadsImagesAutomatically(true);//关闭延迟加载图片
      /*  if (NetworkUtils.isNetworkAvailable(mContext)) {
            mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        } else {
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //解决Http和Https混合问题
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        addJavascriptInterface(new MyWebViewJavaScriptSInterface(context, mHandler), "PCJSKit");
        setWebChromeClient(null);
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)) {
                    view.loadUrl(url);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        //屏蔽长按复制
        setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //webview内登录跳转到客户端登录页面
        if (null != view && url.contains(BaseWebView.WEBVIEW_LOGIN)) {
            String funName = url.replace(BaseWebView.WEBVIEW_LOGIN, "");
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra(Constant.KEY_WEB_CALLBACK, funName);
            ((Activity) mContext).startActivityForResult(intent, Constant.REQ_WEB_LOGIN);
            return true;
        }
        return false;
    }

    /**
     * web调起登录后,在此回调js
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.REQ_WEB_LOGIN && resultCode == Activity.RESULT_OK) {
            if (AccountUtils.isLogin()) {
                String sessionId = AccountUtils.getSessionId();
                if (data != null) {
                    String funName = data.getStringExtra(Constant.KEY_WEB_CALLBACK);
                    this.loadUrl("javascript:" + funName + "('" + sessionId + "')");
                }
            }
        }
    }


    @Override
    public void setWebChromeClient(final WebChromeClient client) {
        super.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (null != client) {
                    client.onProgressChanged(view, newProgress);
                } else {
                    super.onProgressChanged(view, newProgress);
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {

                if (null != client) {
                    return client.onJsAlert(view, url, message, result);
                } else {
                    return super.onJsAlert(view, url, message, result);
                }
            }

            @Override
            public boolean onJsConfirm(WebView view, String url,
                                       String message, JsResult result) {

                if (null != client) {
                    return client.onJsConfirm(view, url, message, result);
                } else {
                    return super.onJsConfirm(view, url, message, result);
                }
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, JsPromptResult result) {

                if (null != client) {
                    return client.onJsPrompt(view, url, message, defaultValue, result);
                } else {
                    return super.onJsPrompt(view, url, message, defaultValue, result);
                }
            }

            @Override
            public boolean onJsBeforeUnload(WebView view, String url,
                                            String message, JsResult result) {
                if (null != client) {
                    return client.onJsBeforeUnload(view, url, message, result);
                } else {
                    return super.onJsBeforeUnload(view, url, message, result);

                }
            }

            @Override
            public boolean onJsTimeout() {
                if (null != client) {
                    return client.onJsTimeout();
                } else {
                    return super.onJsTimeout();
                }
            }

            @Override
            public void onReachedMaxAppCacheSize(long requiredStorage,
                                                 long quota, QuotaUpdater quotaUpdater) {

                if (null != client) {
                    client.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
                } else {
                    super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);

                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (null != client) {
                    client.onReceivedTitle(view, title);
                } else {
                    super.onReceivedTitle(view, title);
                }
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                if (null != client) {
                    client.onReceivedIcon(view, icon);
                } else {
                    super.onReceivedIcon(view, icon);

                }
            }

            @Override
            public void onReceivedTouchIconUrl(WebView view, String url,
                                               boolean precomposed) {
                if (null != client) {
                    client.onReceivedTouchIconUrl(view, url, precomposed);
                } else {
                    super.onReceivedTouchIconUrl(view, url, precomposed);
                }
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {

                if (null != client) {
                    client.onShowCustomView(view, callback);
                } else {
                    super.onShowCustomView(view, callback);
                }
            }

            @Override
            public void onRequestFocus(WebView view) {
                if (null != client) {
                    client.onRequestFocus(view);
                } else {
                    super.onRequestFocus(view);
                }
            }
        });
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedCallBack != null) {
            mOnScrollChangedCallBack.onScroll(l - oldl, t - oldt);
        }
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    private OnScrollChangedCallback mOnScrollChangedCallBack;

    public void setOnScrollChangedCallBack(OnScrollChangedCallback mOnScrollChangedCallBack) {
        this.mOnScrollChangedCallBack = mOnScrollChangedCallBack;
    }

    public interface OnScrollChangedCallback {
        void onScroll(int l, int t);
    }


}
