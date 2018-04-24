package blog.csdn.net.mchenys.module.main;


import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseFragment;
import blog.csdn.net.mchenys.common.utils.ToastUtils;
import blog.csdn.net.mchenys.common.widget.dialog.SelectionPopWindow;
import blog.csdn.net.mchenys.common.widget.pulltopage.PullToPageBase;
import blog.csdn.net.mchenys.common.widget.webview.BaseWebView;
import blog.csdn.net.mchenys.common.widget.webview.PullToPageWebView;


/**
 * 发现页
 * Created by mChenys on 2017/12/28.
 */

public class HomeFragment extends BaseFragment {
    private ViewFlipper webviewLayout;  // 翻页功能容器，内部2个页面切换加载数据实现
    private int currentPage = 1;
    private int totalPage = 5;

    @Override
    protected void initView() {
        super.initView();
        webviewLayout = findViewById(R.id.information_article_layout);
        PullToPageWebView pagedWebview1 = new PullToPageWebView(mContext);
        PullToPageWebView pagedWebview2 = new PullToPageWebView(mContext);
        webviewLayout.addView(pagedWebview1);
        webviewLayout.addView(pagedWebview2);
        pagedWebview1.getLoadableView().loadUrl(urls[currentPage]);
        pagedWebview1.setOnRefreshListener(new MyOnPageListener(pagedWebview1, pagedWebview2));
        pagedWebview2.setOnRefreshListener(new MyOnPageListener(pagedWebview2, pagedWebview1));

    }

    @Override
    protected void initListener() {
        super.initListener();
        findViewById(R.id.iv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectionPopWindow window = new SelectionPopWindow(mContext, R.layout.item_selection_popwin);
                window.setData(new String[]{"自动刷新", "正序观看"});
                window.show(new SelectionPopWindow.CallBack() {
                    @Override
                    public void onSelect(int position, String content) {
                        ToastUtils.showShort(content);
                    }
                });
            }
        });
    }

    @Override
    protected Integer getLayoutResID() {
        return R.layout.fragment_home;
    }

    private class MyOnPageListener implements PullToPageBase.OnPageListener {
        PullToPageWebView currentWebview, nextWebview;

        public MyOnPageListener(PullToPageWebView currentWebview, PullToPageWebView nextWebview) {
            this.currentWebview = currentWebview;
            this.nextWebview = nextWebview;
        }

        @Override
        public void initTitle(String type) {
            setArticleTitle(currentWebview, type);
        }

        @Override
        public void onPage(String type) {
            if (type.equals(PullToPageBase.PULL_DOWN)) {// 上一页
                pullDownPage(currentWebview, nextWebview);
            } else if (type.equals(PullToPageBase.PULL_UP)) {// 下一页
                pullUpPage(currentWebview, nextWebview);
            } else {
                currentWebview.onRefreshComplete();
                nextWebview.onRefreshComplete();
            }
        }
    }

    // 上下拉分页前初始化文章标题
    private void setArticleTitle(PullToPageWebView currentWebview, String type) {
        // 下拉翻上一页时
        if (type.equals(PullToPageBase.PULL_DOWN)) {

            if (currentPage == 1) {
                currentWebview.setDownTitle("没有了");
            } else {
                currentWebview.setDownTitle("初始化中...");
            }
        }
        // 上拉翻到下一页时
        else if (type.equals(PullToPageBase.PULL_UP)) {
            if (currentPage == totalPage) {
                currentWebview.setUpTitle("没有了");
            } else {
                currentWebview.setUpTitle("初始化中...");
            }
        }
    }

    private String urls[] = {"",
            "http://www.baidu.com",
            "http://www.163.com",
            "http://www.shouhu.com",
            "http://www.sina.com",
            "http://www.360.com"};


    /**
     * 上拉翻下一页
     *
     * @param nextWebview    下一页的webview
     * @param currentWebview 当前页的webview (翻页成功将被移除)
     */
    private void pullUpPage(PullToPageWebView currentWebview, PullToPageWebView nextWebview) {


        if (currentPage < totalPage) {
            currentPage++;
            BaseWebView webView = nextWebview.getLoadableView();
            webView.loadUrl(urls[currentPage]);
            nextWebview.onRefreshComplete();
            gotoNext();
        } else {
            currentWebview.onRefreshComplete();
        }
    }

    /**
     * 下拉翻到上一页
     *
     * @param currentWebview  当前view
     * @param previousWebview 上一页view
     */
    private void pullDownPage(PullToPageWebView currentWebview, PullToPageWebView previousWebview) {

        // 大于1的时候才执行分页功能
        if (currentPage > 1) {
            currentPage--;
            BaseWebView webView = previousWebview.getLoadableView();
            webView.loadUrl(urls[currentPage]);
            previousWebview.onRefreshComplete();
            gotoUp();
        } else {
            currentWebview.onRefreshComplete();
            return;
        }
    }


    /**
     * 切换页至下一页
     */
    private void gotoNext() {
        webviewLayout.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_up_in));
        webviewLayout.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_up_out));
        webviewLayout.showNext();
    }

    /**
     * 切换页至上一页
     */
    private void gotoUp() {
        webviewLayout.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_down_in));
        webviewLayout.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_down_out));
        webviewLayout.showPrevious();
    }
}
