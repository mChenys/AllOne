package blog.csdn.net.mchenys.common.base;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.HashMap;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;
import blog.csdn.net.mchenys.common.utils.AccountUtils;
import blog.csdn.net.mchenys.common.utils.HttpUtils;
import blog.csdn.net.mchenys.common.utils.StringUtils;
import blog.csdn.net.mchenys.common.utils.URIUtils;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import blog.csdn.net.mchenys.common.widget.view.UEView;
import blog.csdn.net.mchenys.common.widget.webview.BaseWebView;

import static android.webkit.WebView.enableSlowWholeDocumentDraw;

/**
 * 终端基类
 * Created by mChenys on 2017/12/27.
 */
public class BaseTerminalActivity extends BaseActivity {
    private static final String TAG = "BaseTerminalActivity";
    protected BaseWebView mWebView;
    private UEView mUEView;
    private FrameLayout mBottomFl, mTopFl, mFixedBottomFl;
    private ImageView mBackTopIv;
    private BaseWebViewClient mBaseWebViewClient;
    protected String url;
    protected String title;
    private String originalUrl;
    private Handler mHandler = new Handler();
    private boolean isLoadComplete;
    private boolean onReceivedError;
    public static final int TYPE_URL = 0; //通过loadUrl加载页面
    public static final int TYPE_CODE = 1; //通过http请求html加载页面,默认方式
    private int loadType = TYPE_CODE;

    public boolean isLoadComplete() {
        return isLoadComplete;
    }

    public void setLoadType(int loadType) {
        this.loadType = loadType;
    }

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_base_terminal;
    }

    protected void initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            enableSlowWholeDocumentDraw();
        }
        url = getIntent().getStringExtra(Constant.KEY_URL);
        title = getIntent().getStringExtra(Constant.KEY_TITLE);
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setCenterTv(title);
    }

    protected void initView() {
        super.initView();
        mBottomFl = findViewById(R.id.fl_bottom);
        mFixedBottomFl = findViewById(R.id.fl_fixed_bottom);
        mTopFl = findViewById(R.id.fl_top);
        mBackTopIv = findViewById(R.id.iv_backToTop);
        mWebView = findViewById(R.id.webView);
        mUEView = findViewById(R.id.UEView);
        mUEView.showLoading();
    }


    protected void initListener() {
        mUEView.setOnReloadListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUEView.hideError(); //隐藏404页面
                reLoad();//重新加载数据
            }
        });
        mBackTopIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simulateClick(new Callback() {
                    @Override
                    public void finish() {
                        mWebView.scrollTo(0, 0);
                    }
                });

            }
        });
        mWebView.setOnScrollChangedCallBack(new BaseWebView.OnScrollChangedCallback() {
            @Override
            public void onScroll(int dx, int dy) {
                if (mWebView.getScrollY() >= mWebView.getHeight() * mWebView.getScaleY() * 2) {
                    mBackTopIv.setVisibility(View.VISIBLE);
                } else {
                    mBackTopIv.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 设置返回顶部按钮
     *
     * @param resId null表示不显示,默认不显示
     */
    protected void enableBackToTop(Integer resId) {
        mBackTopIv.setVisibility(null != resId ? View.VISIBLE : View.GONE);
        mBackTopIv.setBackgroundResource(resId);
    }

    public interface Callback {
        void finish();
    }

    public void simulateClick(final Callback callback) {
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, 0, mWebView.getScrollY(), 0);
        downTime += 10;
        final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, 0, mWebView.getScrollY(), 0);
        mWebView.onTouchEvent(downEvent);
        mWebView.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
        mWebView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != callback) callback.finish();
            }
        }, 10);
    }

    protected void loadData() {
        setWebViewClientEnable(true);
        //提高渲染等级
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (StringUtils.isEmpty(BaseTerminalActivity.this.title)) { //如果没有传title,则用页面title
                    BaseTerminalActivity.this.title = title;
                }
            }
        });
        loadUrl(url);
    }


    /**
     * 设置WebClient，并实现其中用到的方法
     *
     * @param webClient
     */
    public void setWebViewClient(final BaseWebViewClient webClient) {
        this.mBaseWebViewClient = webClient;
        if (null != webClient) {
            setWebViewClientEnable(true);
        }
    }

    public void setWebViewClientEnable(boolean clientEnable) {
        if (clientEnable) {
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                    if (URLUtil.isNetworkUrl(url)) {
                        view.loadUrl(url);//内跳
                        return true;
                    } else if (paserProtocol(url)) {
                        return true;
                    } else if (null != mBaseWebViewClient && mBaseWebViewClient.shouldOverrideUrlLoading(view, url)) {
                        return true;
                    }
                    return false;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {

                    if (null != mBaseWebViewClient) {
                        mBaseWebViewClient.onPageStarted(view, originalUrl, favicon);
                    }
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(final WebView view, String url) {
                    view.loadUrl("javascript:window.PCJSKit.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
                    if (!mWebView.getSettings().getLoadsImagesAutomatically()) {
                        mWebView.getSettings().setLoadsImagesAutomatically(true);//网页加载完后再显示图片
                    }
                    if (onReceivedError) {
                        mUEView.showError();
                    } else {
                        mUEView.hideLoading();
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isLoadComplete = true;
                            if (null != mBaseWebViewClient) {
                                //这里使用originalUrl,是解决loadJs传入的代码
                                mBaseWebViewClient.onPageFinished(view, originalUrl);
                            }
                        }
                    }, 400);
                }

                @Override
                public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
                    onReceivedError = true;
                    if (null != mBaseWebViewClient) {
                        mBaseWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
                    }
                    super.onReceivedError(view, errorCode, description, failingUrl);
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                    super.onReceivedSslError(view, handler, error);
                    handler.proceed();//支持https响应
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    mWebView.syncCookie(url);
                    return super.shouldInterceptRequest(view, url);
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mWebView.syncCookie(request.getUrl().toString());
                    }
                    return super.shouldInterceptRequest(view, request);
                }
            });

        } else {
            mWebView.setWebViewClient(null);
        }
    }

    /**
     * 解析公共的协议
     *
     * @param url
     * @return
     */
    private boolean paserProtocol(String url) {
        if (mWebView.shouldOverrideUrlLoading(mWebView, url)) {
            return true;
        } else {
            return URIUtils.dispatch(mContext, url);
        }
    }


    /**
     * 处理WebClient中对应名称的方法
     */
    public static abstract class BaseWebViewClient {

        public abstract boolean shouldOverrideUrlLoading(WebView view, String url);

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        }

        public void onPageFinished(WebView view, String url) {
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        }
    }

    /**
     * 加载url
     */
    public void loadUrl(final String url) {
        onReceivedError = false;
        if (!StringUtils.isEmpty(url)) {
            HashMap<String, String> header = new HashMap<>();
            header.put("Referer", url);
            this.originalUrl = url;
            if (loadType == TYPE_URL) {
                mWebView.loadUrl(url, header);
            } else {
                header.put("Cookie", Urls.COMMON_SESSION_ID + AccountUtils.getSessionId());
                HttpUtils.getJSON(true, url, header, null, new HttpUtils.JSONCallback() {
                    @Override
                    public void onFailure(Exception e) {
                        mUEView.showError();
                    }

                    @Override
                    public void onSuccess(JSONObject jsonObject, OkResponse okResponse) {
                        int status = jsonObject.optInt("status");
                        if (status < 0) {
                            mUEView.showError();
                            return;
                        }
                        mWebView.loadDataWithBaseURL(url, okResponse.getResult(), "text/html", "UTF-8", null);
                        mUEView.hideLoading();

                    }
                });
            }
        } else {
            mUEView.showError();
        }
    }

    /**
     * 重新加载
     */
    public void reLoad() {
        onReceivedError = false;
        mUEView.showLoading();
        loadUrl(originalUrl);
    }

    /**
     * 加载js函数
     *
     * @param js
     */
    public void loadJs(String js) {
        onReceivedError = false;
        mWebView.loadUrl(js);
    }

    /**
     * 用于获取页面的meta元数据,不包含起始位置
     *
     * @return
     */
    public String getMetaInfo() {
        if (StringUtils.isEmpty(mWebView.getHtmlContent())) {//第一次有时候会失败
            mWebView.loadUrl("javascript:window.PCJSKit.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
        }
        return parseMetaString(mWebView.getHtmlContent());
    }

    private String parseMetaString(String metaData) {
        if (!StringUtils.isEmpty(metaData) && metaData.indexOf("/*@_HTML_META_START_") >= 0 && metaData.indexOf("_HTML_META_END_@*/") > 0) {
            String metaInfo = metaData.substring(metaData.indexOf("/*@_HTML_META_START_") + 20, metaData.indexOf("_HTML_META_END_@*/")).trim();
            return StringUtils.replaceSpecialchar(metaInfo);
        }
        return null;
    }

    public JSONObject getMetaData() {
        return getMetaData(getMetaInfo(), false);
    }

    public JSONObject getMetaData(String metaData, boolean needDecode) {
        try {
            if (needDecode) {
                metaData = URLDecoder.decode(metaData, "utf-8");
            }
            if (!StringUtils.isEmpty(metaData)) {
                return new JSONObject(metaData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 添加固定在内容区域下的view
     *
     * @param view
     */
    protected void addFixedBottom(View view) {
        mFixedBottomFl.addView(view);
    }

    /**
     * 添加浮动的底部视图
     *
     * @param view
     */
    protected void addBottomView(View view) {
        mBottomFl.addView(view);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isLoadComplete = false;
        try {
            if (null != mWebView) {
                mWebView.clearCache(false);
                mWebView.clearHistory();
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                if (null != parent) {
                    parent.removeView(mWebView);
                    mWebView.destroy();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}