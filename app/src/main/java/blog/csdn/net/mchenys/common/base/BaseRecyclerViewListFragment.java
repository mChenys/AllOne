package blog.csdn.net.mchenys.common.base;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;
import blog.csdn.net.mchenys.common.utils.ExceptionUtils;
import blog.csdn.net.mchenys.common.utils.GetPageTotalUtils;
import blog.csdn.net.mchenys.common.utils.HttpUtils;
import blog.csdn.net.mchenys.common.utils.NetworkUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.RefreshRecyclerView;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import blog.csdn.net.mchenys.common.widget.view.UEView;


/**
 * fragment列表基类
 * Created by mChenys on 2017/12/27.
 */
public abstract class BaseRecyclerViewListFragment<T> extends BaseFragment {

    protected RefreshRecyclerView mRecyclerView;
    private FrameLayout mTopLayout; //悬浮在顶部布局,可添加额外的布局
    private FrameLayout mBottomLayout; //悬浮在底部布局,可添加额外的布局
    protected UEView mUEView;
    private TitleBar mTitleBar;

    private int pageNo = 1;//页码
    protected int pageTotal;//总页数
    protected int pageSize = 10;
    private int total;

    protected List<T> mData = new ArrayList<>();
    private Set<T> set = new HashSet<>(); //用于去重数据,记得重写T类型的equals和hasCode方法

    private boolean isLoadMore; //是否加载更多
    private boolean isShowNoMore = true;//是否显示没有更多,有些终端页不需要显示的.
    protected boolean isforceRefresh;//初始化时是否强制刷新
    private boolean isLazyLoad;//是否是懒加载

    public void setShowNoMore(boolean showNoMore) {
        isShowNoMore = showNoMore;
    }

    public void setForceRefresh(boolean isforceRefresh) {
        this.isforceRefresh = isforceRefresh;
    }

    public void setLazyLoad(boolean lazyLoad) {
        isLazyLoad = lazyLoad;
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
        return R.layout.fragment_base_refresh_recycle_view_list;
    }


    @Override
    protected void initView() {
        super.initView();
        mTitleBar = findViewById(R.id.title_bar);
        mRecyclerView = findViewById(R.id.refresh);
        mTopLayout = findViewById(R.id.fl_flow_top);
        mBottomLayout = findViewById(R.id.fl_flow_bottom);
        mUEView = findViewById(R.id.UEView);
        mUEView.showLoading();
        setTitleBar(mTitleBar);
    }


    @Override
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
                if (NetworkUtils.isNetworkAvailable(getActivity())) {
                    if (null != args && args.size() > 0) {
                        pageNo = 1;
                        isLoadMore = false;
                        boolean isRefresh = args.getBoolean(Constant.KEY_REFRESH, true);
                        loadData(isRefresh);
                    } else {
                        startRefresh();
                    }

                } else {
                    ToastUtils.showShort(getActivity(), getString(R.string.notify_no_network));
                    mRecyclerView.refreshComplete();
                }

            }

            @Override
            public void onLoadMore() {
                if (NetworkUtils.isNetworkAvailable(getActivity())) {
                    pageNo++;
                    isLoadMore = true;
                    loadData(true);
                } else {
                    ToastUtils.showShort(getActivity(), getActivity().getString(R.string.notify_no_network));
                    mRecyclerView.loadMoreComplete();
                }

            }
        });
    }

    @Override
    protected void loadData() {
        super.loadData();
        if (!isLazyLoad) loadData(isforceRefresh);
    }

    /**
     * 外部调用刷新,如其他页面返回需要刷新的时候可以调用
     */
    public void startRefresh() {
        pageNo = 1;
        isLoadMore = false;
        loadData(true);
    }

    @Override
    public void autoRefresh(Bundle bundle) {
        if (null == bundle) bundle = new Bundle();
        super.autoRefresh(bundle);
        bundle.putBoolean(Constant.KEY_REFRESH, true);
        mUEView.hideAll();
        mRecyclerView.autoRefresh(bundle);

    }

    protected void loadData(boolean isRefresh) {
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            if (isLoadMore) {//加载更多
                if (pageTotal < 1 || total > 0 && total < 5) {
                    mRecyclerView.hideLoaderFooterView();
                    return;
                } else if (pageTotal < pageNo && isShowNoMore) {
                    mRecyclerView.setNoMore(true);
                    return;
                }
            }
        }
        Req req = onCreateReq();
        if (null == req) {
            mUEView.hideAll();
            if (isLoadMore) {
                mRecyclerView.loadMoreComplete();
            } else {
                mRecyclerView.refreshComplete();
            }
            return;
        }
        String url = req.url + "?pageSize=" + pageSize + "&pageNo=" + pageNo;
        if (null != req.bodyMap) {
            for (String key : req.bodyMap.keySet()) {
                url = url.concat("&" + key + "=" + req.bodyMap.get(key));
            }
        }
        HttpUtils.getJSON(isRefresh, url, req.headersMap, null, new HttpUtils.JSONCallback() {
            @Override
            public void onFailure(Exception e) {
                ExceptionUtils.exceptionHandler(e);
                if (isLoadMore) {
                    mRecyclerView.loadMoreComplete();
                } else {
                    mRecyclerView.refreshComplete();
                }
                if (mData.isEmpty()) {
                    if (!NetworkUtils.isNetworkAvailable(mContext)) {
                        mUEView.showError();
                    } else {
                        if (mRecyclerView.getHeadersCount() > 1) {
                            //有头部
                            mUEView.hideAll();
                        } else {
                            mUEView.showNoData();
                        }
                    }
                }

                onReqComplete(isLoadMore);
            }

            @Override
            public void onSuccess(JSONObject jsonObject, OkResponse pcResponse) {
                int status = jsonObject.optInt("status");
                if (status >= 0) {
                    total = jsonObject.optInt("total");
                    pageNo = jsonObject.optInt("pageNo");
                    //获取总页数
                    pageTotal = GetPageTotalUtils.getPageTotal(total, 10);
                    try {
                        //刷新数据
                        beforeDataSet(jsonObject, isLoadMore);
                        List<T> temp = parseList(jsonObject);
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
                        afterDataSet(mData, isLoadMore);
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
                        if (isLoadMore) {
                            if (pageTotal < pageNo && isShowNoMore) {
                                mRecyclerView.setNoMore(true);
                            } else {
                                mRecyclerView.loadMoreComplete();
                            }
                        } else {
                            mRecyclerView.refreshComplete();
                        }
                        onReqComplete(isLoadMore);
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
     * 添加头部布局
     *
     * @param resId
     */
    protected void addCoverTopLayout(int resId) {
        addCoverTopLayout(resId, new LinearLayout.LayoutParams(-1, -2));
    }

    protected void addCoverTopLayout(int resId, LinearLayout.LayoutParams lp) {
        View view = View.inflate(getActivity(), resId, null);
        if (null != view) {
            mTopLayout.addView(view, lp);
        }
    }
    /**
     * 添加顶部布局
     *
     * @param topLayout
     * @param params
     */
    public void addCoverTopLayout(View topLayout, FrameLayout.LayoutParams params) {
        mTopLayout.addView(topLayout, params);
    }

    /**
     * 添加低部布局
     *
     * @param resId
     */
    protected void addBottomLayout(int resId) {
        View view = View.inflate(getActivity(), resId, null);
        if (null != view) {
            mBottomLayout.addView(view);
        }
    }

    /**
     * 设置标题
     *
     * @param titleBar
     */
    protected abstract void setTitleBar(TitleBar titleBar);

    /**
     * 构建请求对象
     *
     * @return
     */
    protected abstract Req onCreateReq();


    /**
     * 解析列表数据
     */
    protected abstract List<T> parseList(JSONObject jsonObject) throws Exception;

    /**
     * 列表数据设置之前返回接口json,可在这里处理头部数据或者其他数据
     *
     * @param jsonObject
     * @param isLoadMore
     */
    protected abstract void beforeDataSet(JSONObject jsonObject, boolean isLoadMore);

    /**
     * 列表数据设置完之后,可以处理广告或者其他
     *
     * @param data
     * @param isLoadMore
     */
    protected abstract void afterDataSet(List<T> data, boolean isLoadMore);


    public void onReqComplete(boolean isLoadMore) {
        //empty
    }


}
