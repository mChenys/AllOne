package blog.csdn.net.mchenys.module.main;


import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseFragment;
import blog.csdn.net.mchenys.common.utils.AccountUtils;
import blog.csdn.net.mchenys.common.utils.EmojiInputFilter;
import blog.csdn.net.mchenys.common.utils.JumpUtils;
import blog.csdn.net.mchenys.common.utils.SoftInputUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;
import blog.csdn.net.mchenys.common.widget.dialog.SelectionPopWindow;
import blog.csdn.net.mchenys.common.widget.pulltopage.PullToPageBase;
import blog.csdn.net.mchenys.common.widget.webview.BaseWebView;
import blog.csdn.net.mchenys.common.widget.webview.PullToPageWebView;
import blog.csdn.net.mchenys.module.account.LoginActivity;


/**
 * 发现页
 * Created by mChenys on 2017/12/28.
 */

public class HomeFragment extends BaseFragment implements View.OnFocusChangeListener, View.OnClickListener {
    private ViewFlipper webviewLayout;  // 翻页功能容器，内部2个页面切换加载数据实现
    private int currentPage = 1;
    private int totalPage = 5;

    private TextView tvSendComment, tvReload;
    private WebView currentWebview;
    private EditText etWriteComment;

    @Override
    protected void initView() {
        super.initView();
        webviewLayout = findViewById(R.id.information_article_layout);
        PullToPageWebView pagedWebview1 = new PullToPageWebView(mContext);
        PullToPageWebView pagedWebview2 = new PullToPageWebView(mContext);
        currentWebview = pagedWebview1.getLoadableView();
        webviewLayout.addView(pagedWebview1);
        webviewLayout.addView(pagedWebview2);
        pagedWebview1.getLoadableView().loadUrl(urls[currentPage]);
        pagedWebview1.setOnRefreshListener(new MyOnPageListener(pagedWebview1, pagedWebview2));
        pagedWebview2.setOnRefreshListener(new MyOnPageListener(pagedWebview2, pagedWebview1));

        tvSendComment = findViewById(R.id.tv_send_comment);
        tvReload = findViewById(R.id.tv_reload);
        etWriteComment = findViewById(R.id.et_write_comment);
        etWriteComment.setFilters(new InputFilter[]{new EmojiInputFilter()});

        pagedWebview1.getLoadableView().setOnTouchListener(mCloseSoftTouchListener);
        pagedWebview2.getLoadableView().setOnTouchListener(mCloseSoftTouchListener);
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

        etWriteComment.setOnFocusChangeListener(this);
        tvSendComment.setOnClickListener(this);
        tvReload.setOnClickListener(this);

    }

    @Override
    protected Integer getLayoutResID() {
        return R.layout.fragment_home;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            if (AccountUtils.isLogin()) {
                tvReload.setVisibility(View.GONE);
                tvSendComment.setVisibility(View.VISIBLE);
            } else {
                JumpUtils.startActivity(mContext, LoginActivity.class);
            }
        } else {
            tvReload.setVisibility(etWriteComment.length() > 0 ? View.GONE : View.VISIBLE);
            tvSendComment.setVisibility(etWriteComment.length() > 0 ? View.VISIBLE : View.GONE);
            etWriteComment.setHint("写评论");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_reload:
                currentWebview.reload();
                break;
            case R.id.tv_send_comment:
                ToastUtils.showShort("发表评论");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SoftInputUtils.closedSoftInput(mContext);
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
        this.currentWebview = currentWebview.getLoadableView();
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
            "https://mrobot.pchouse.com.cn/v3/cms/articles/2183935?picRule=2&deviceType=android&articleTemplate=4.3.0.0",
            "https://mrobot.pchouse.com.cn/v3/cms/articles/2157758?picRule=2&deviceType=android&articleTemplate=4.3.0.0",
            "https://mrobot.pchouse.com.cn/v3/cms/articles/2183856?picRule=2&deviceType=android&articleTemplate=4.3.0.0",
            "https://mrobot.pchouse.com.cn/v3/cms/articles/2155318?picRule=2&deviceType=android&articleTemplate=4.3.0.0",
            "https://mrobot.pchouse.com.cn/v3/cms/articles/2150735?picRule=2&deviceType=android&articleTemplate=4.3.0.0"};


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

    private View.OnTouchListener mCloseSoftTouchListener = new View.OnTouchListener() {
        float x1, x2, y1, y2;

        @Override
        public boolean onTouch(View v, MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                x1 = ev.getX();
                y1 = ev.getY();
            } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                x2 = ev.getX();
                y2 = ev.getY();
            }
            if (Math.abs(x1 - x2) < 10 && Math.abs(y1 - y2) < 10) {
                SoftInputUtils.closedSoftInput(mContext);
            }
            return false;
        }
    };

}
