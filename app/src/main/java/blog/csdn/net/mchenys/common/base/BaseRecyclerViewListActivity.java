package blog.csdn.net.mchenys.common.base;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.config.Env;
import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;
import blog.csdn.net.mchenys.common.utils.ExceptionUtils;
import blog.csdn.net.mchenys.common.utils.GetPageTotalUtils;
import blog.csdn.net.mchenys.common.utils.HttpUtils;
import blog.csdn.net.mchenys.common.utils.NetworkUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.RefreshRecyclerView;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.footer.ShoppingLoadingFooter;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.header.ShoppingRefreshHeader;
import blog.csdn.net.mchenys.common.widget.view.UEView;


/**
 * 列表基类Activity
 * Created by mChenys on 2017/12/27.
 */
public abstract class BaseRecyclerViewListActivity<T> extends BaseActivity {
    protected RefreshRecyclerView mRecyclerView;
    protected UEView mUEView;
    private int pageNo = 1;//页码
    private int pageTotal;//总页数
    protected int pageSize = 10;//每页大小
    private int total;
    private FrameLayout mTopFl;

    protected List<T> mData = new ArrayList<>();
    private Set<T> set = new HashSet<>(); //用于去重数据,记得重写T类型的equals和hasCode方法

    private boolean isLoadMore; //是否加载更多
    private boolean isShowNoMore = true;//是否显示没有更多,有些终端页不需要显示的.
    private boolean isforceRefresh;//初始化时是否强制刷新

    public void setShowNoMore(boolean showNoMore) {
        isShowNoMore = showNoMore;
    }

    public void setForceRefresh(boolean isforceRefresh) {
        this.isforceRefresh = isforceRefresh;
    }

    public class Req {
        public String url;
        public Map<String, String> headersMap;
        public Map<String, String> bodyMap;

        public Req() {
        }

        public Req(String url, Map<String, String> headersMap, Map<String, String> bodyMap) {
            this.url = url;
            this.headersMap = headersMap;
            this.bodyMap = bodyMap;
        }
    }

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_base_refresh_recycle_view_list;
    }

    protected void initView() {
        super.initView();
        mRecyclerView = findViewById(R.id.refresh);
        mRecyclerView.setRefreshHeader(new ShoppingRefreshHeader(this));
        mRecyclerView.setLoaderFooter(new ShoppingLoadingFooter(this));
        mUEView = findViewById(R.id.UEView);
        mUEView.showLoading();
        mTopFl = findViewById(R.id.fl_top);
    }


    protected void initListener() {
        super.initListener();
        mUEView.setOnReloadListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUEView.showLoading();
                loadData(true);
            }
        });
        mRecyclerView.setLoadingListener(new RefreshRecyclerView.LoadingListener() {
            @Override
            public void onRefresh(Bundle args) {
                if (NetworkUtils.isNetworkAvailable(mContext)) {
                    if (null != args && args.size() > 0) {
                        boolean isRefresh = args.getBoolean(Constant.KEY_REFRESH, true);
                        pageNo = 1;
                        isLoadMore = false;
                        loadData(isRefresh);
                    } else {
                        startRefresh();
                    }
                } else {
                    ToastUtils.showShort(mContext, getString(R.string.notify_no_network));
                    if (mData.isEmpty()) {
                        mUEView.showError();
                    }
                    mRecyclerView.refreshComplete();
                }
            }

            @Override
            public void onLoadMore() {
                if (NetworkUtils.isNetworkAvailable(mContext)) {
                    pageNo++;
                    isLoadMore = true;
                    loadData(true);
                } else {
                    ToastUtils.showShort(mContext, getString(R.string.notify_no_network));
                    mRecyclerView.loadMoreComplete();
                }
            }
        });


    }

    @Override
    protected void loadData() {
        loadData(isforceRefresh);
    }

    /**
     * 外部调用刷新,如其他页面返回需要刷新的时候可以调用
     */
    public void startRefresh() {
        pageNo = 1;
        isLoadMore = false;
        loadData(true);
    }

    public void autoRefresh(Bundle bundle) {
        if (null == bundle) bundle = new Bundle();
        bundle.putBoolean(Constant.KEY_REFRESH, true);
        mUEView.hideAll();
        mRecyclerView.autoRefresh(bundle);
    }

    protected void loadData(boolean isRefresh) {
        Req req = onCreateReq();
        if (!canRequest(req)) return;
        if (null == req.bodyMap) {
            req.bodyMap = new HashMap<>();
        }
        req.bodyMap.put("pageSize", String.valueOf(pageSize));
        req.bodyMap.put("pageNo", String.valueOf(pageNo));
        req.bodyMap.put("version", String.valueOf( Env.versionCode));
        HttpUtils.getJSON(isRefresh, req.url, req.headersMap, req.bodyMap, new HttpUtils.JSONCallback() {
            @Override
            public void onFailure(Exception e) {
                requestException(e);
            }

            @Override
            public void onSuccess(JSONObject jsonObject, OkResponse pcResponse) {
                int status = jsonObject.optInt("status");
                if (status >= 0) {
                    total = jsonObject.optInt("total");
                    pageNo = jsonObject.optInt("pageNo");
                    pageTotal = GetPageTotalUtils.getPageTotal(total, 10); //获取总页数
                    try {
                        //刷新数据
                        beforeDataSet(jsonObject, isLoadMore);
                        List<T> temp = parseList(jsonObject);
                        removeDuplicateData(temp);
                        afterDataSet(mData, isLoadMore);
                        requestSuccess();
                    } catch (Exception e) {
                        onFailure( e);
                    }
                } else {
                    onFailure(new Exception(jsonObject.optString("msg")));
                }
            }
        });
    }

    /**
     * 是否可以请求
     *
     * @param req
     * @return
     */
    private boolean canRequest(Req req) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            if (isLoadMore) {//加载更多
                if (pageTotal < 1 || total > 0 && total < 5) {
                    mRecyclerView.hideLoaderFooterView();
                    return false;
                } else if (pageTotal < pageNo && isShowNoMore) {
                    mRecyclerView.setNoMore(true);
                    return false;
                }
            }
        }
        if (null == req) {
            mUEView.hideAll();
            mRecyclerView.stopRefresh(isLoadMore);
            return false;
        }
        return true;
    }
    /**
     * 去重 记得重写T类型的equals和hasCode方法
     * @param temp
     */
    private void removeDuplicateData(List<T> temp) {
        if (!isLoadMore) {
            mData.clear();
            set.clear();
        }
        List<T> newData = new ArrayList<>();
        if (temp != null && !temp.isEmpty()) {
            for (T t : temp) { //去重复
                if (set.add(t)) {
                    newData.add(t);
                }
            }
        }
        mData.addAll(newData);
    }

    /**
     * 请求异常
     */
    private void requestException(Exception e) {
        ExceptionUtils.exceptionHandler(e);
        mRecyclerView.stopRefresh(isLoadMore);
        if (mData.isEmpty()) {
            if (NetworkUtils.isNetworkAvailable(mContext)
                    && !(e instanceof SocketTimeoutException)) {
                if (mRecyclerView.getHeadersCount() > 1) {
                    //有头部
                    mUEView.hideAll();
                } else {
                    mUEView.showNoData();
                }
            } else {
                mUEView.showError();
            }
        }

    }

    /**
     * 请求成功
     */
    private void requestSuccess() {
        if (mData.isEmpty()) { //如果数据集合为空,则显示没有数据
            if (mRecyclerView.getHeadersCount() > 1) {
                //有头部
                mUEView.hideAll();
            } else {
                mUEView.showNoData();
            }
        } else {
            mUEView.hideAll();
        }
        if (pageTotal <= pageNo && isShowNoMore) {
            mRecyclerView.setNoMore(true);
        }
        mRecyclerView.stopRefresh(isLoadMore);
    }

    /**
     * 列表数据设置之前返回接口json,可在这里处理头部数据或者其他数据
     *
     * @param jsonObject
     * @param isLoadMore
     */
    protected  void beforeDataSet(JSONObject jsonObject, boolean isLoadMore){};

    /**
     * 列表数据设置完之后,可以处理广告或者其他
     *
     * @param data
     * @param isLoadMore
     */
    protected  void afterDataSet(List<T> data, boolean isLoadMore){};

    /**
     * 解析列表数据
     */
    protected abstract List<T> parseList(JSONObject jsonObject) throws Exception;

    /**
     * 构建请求对象
     *
     * @return
     */
    protected abstract Req onCreateReq();

    /**
     * 添加浮动的顶部视图
     *
     * @param view
     */
    protected void addTopView(View view) {
        mTopFl.addView(view);
    }
}
