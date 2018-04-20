package blog.csdn.net.mchenys.common.widget.webview;


import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import blog.csdn.net.mchenys.common.widget.pulltopage.PullToPageBase;


public class PullToPageWebView extends PullToPageBase<BaseWebView> {

    private Context context;
	
	private final WebChromeClient defaultWebChromeClient = new WebChromeClient() {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
			    //刷新已完成
				onRefreshComplete();
			}
		}
	};

	public PullToPageWebView(Context context) {
        super(context,MODE_BOTH);
        this.context = context;
        /**
         * Added so that by default, Pull-to-Refresh refreshes the page
         */
		//  refreshableView.setWebChromeClient(defaultWebChromeClient);
    }
	
	public PullToPageWebView(Context context,AttributeSet attrs) {
        super(context,MODE_BOTH,attrs);
        this.context = context;
    }
	
	public PullToPageWebView(Context context,AttributeSet attrs,int defStyle){
		super(context,attrs,defStyle);
		this.context = context;
	}
	
	@Override
	protected BaseWebView createRefreshableView(Context context, AttributeSet attrs) {
		BaseWebView webView = new BaseWebView(context, attrs);
		webView.setId(-1);
		return webView;
	}

	
	@Override
	protected boolean isReadyForPullDown() {
		return refreshableView.getScrollY() == 0;
	}

	//计算是否滑动到webview底部
	@Override
	protected boolean isReadyForPullUp() {
	    int po1   = (refreshableView.getScrollY() + refreshableView.getHeight());
	    float po2 = (int)(refreshableView.getContentHeight())*refreshableView.getScale();
//	    Logs.d("yzh","| 滑动的最大高度  = " + po1 + "| 组件的最大高度   = " + po2 + "| webview内容高度  = " + refreshableView.getContentHeight() + "| webview内容扩大尺寸  = " + refreshableView.getScale());
		//4.4系统高度计算与以前存在区别
	    return (refreshableView.getScrollY() + refreshableView.getHeight() + 10) >= (int)((refreshableView.getContentHeight())*refreshableView.getScale());
	}
}

