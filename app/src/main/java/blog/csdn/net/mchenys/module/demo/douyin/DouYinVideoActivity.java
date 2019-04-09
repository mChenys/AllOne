package blog.csdn.net.mchenys.module.demo.douyin;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;
import blog.csdn.net.mchenys.common.utils.HttpUtils;
import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;
import blog.csdn.net.mchenys.common.utils.LogUtils;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewAdapter;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewHolder;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.RefreshRecyclerView;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.SimplePullScrollListener;
import blog.csdn.net.mchenys.common.widget.view.AdvanceSwipeRefreshLayout;
import blog.csdn.net.mchenys.common.widget.view.TexturePlayer;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import blog.csdn.net.mchenys.model.DouYinVideo;

/**
 * Created by mChenys on 2019/4/3.
 */

public class DouYinVideoActivity extends BaseActivity {

    private AdvanceSwipeRefreshLayout mSwipeRefreshLayout;
    private RefreshRecyclerView mRecyclerView;
    private Map<String, String> headerMap = new HashMap<>();
    private List<DouYinVideo> mData = new ArrayList<>();

    private PagerSnapHelper mPagerSnapHelper;
    private TexturePlayer mCurrPlayer;
    private boolean isResume; //当前是否可见

    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setVisibility(View.GONE);
    }

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_dou_yin_video;
    }

    @Override
    protected void initData() {
        super.initData();
        headerMap.put("Cookie", "2e7fba29aa7e18ec265f41d1e96cc3779421c7ee0da7abd01db882121e1aa3738329ba429ed8dcb8d7bfb0eba72e7b5095d0133ec652c75b5be1655e00441aa5; sid_guard=353c7f29c5cd45350b1e5e05174dc719%7C1554171736%7C5184000%7CSat%2C+01-Jun-2019+02%3A22%3A16+GMT; uid_tt=0cfa460f42767a4ac3c3603e937c2ea9; sid_tt=353c7f29c5cd45350b1e5e05174dc719; sessionid=353c7f29c5cd45350b1e5e05174dc719; install_id=68032916025; ttreq=1$39620236dc004286d0ab4cd5de5f142c3007f569; qh[360]=1");
        headerMap.put("X-SS-REQ-TICKET", "1554297991537");
        headerMap.put("X-Tt-Token", "00353c7f29c5cd45350b1e5e05174dc7194e7aae1e8a73a009f0da5c84f6172f383ee05840afec83099e53beab90948adf52");
        headerMap.put("sdk-version", "1");
        headerMap.put("User-Agent", "com.ss.android.ugc.aweme/570 (Linux; U; Android 8.1.0; zh_CN_#Hans; ALP-AL00; Build/HUAWEIALP-AL00; Cronet/58.0.2991.0)");
        headerMap.put("X-Khronos", "1554297991");
        headerMap.put("X-Gorgon", "03006cc080006fea2059270bafa522c70685da40a25bd7b78059");
        headerMap.put("X-Pods", "");

    }

    @Override
    protected void initView() {
        super.initView();
        mSwipeRefreshLayout = findViewById(R.id.advsrl);
        mRecyclerView = findViewById(R.id.rv_list);
        mRecyclerView.setPullRefreshEnabled(false);
        mPagerSnapHelper = new PagerSnapHelper() {
            @Override
            public View findSnapView(RecyclerView.LayoutManager layoutManager) {
                // 在 Adapter的 onBindViewHolder 之后执行
                LogUtils.e("cys", "===========findSnapView=============");
                View view = super.findSnapView(layoutManager);

                final TexturePlayer nextPlayer = view.findViewById(R.id.texture_player);
                if (null == nextPlayer.getUri()) {
                    DouYinVideo douYinVideo = (DouYinVideo) view.getTag();
                    nextPlayer.setVideoPath(douYinVideo.video); //重新设置播放地址
                }
                //非正在播放且非暂停下要显示缩略图,如果是pause则应该显示视频暂停时的那一帧
                if(!nextPlayer.isPlaying() && !nextPlayer.isPause()){
                    final ImageView coverImage = view.findViewById(R.id.iv_cover);
                    coverImage.setVisibility(View.VISIBLE);
                }


                return view;
            }
        };
        mPagerSnapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);


        mRecyclerView.setAdapter(new DouYinVideoAdapter(this,mData));
    }

    @Override
    protected void initListener() {
        super.initListener();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
        mRecyclerView.setOnPullScrollListener(new SimplePullScrollListener() {
            @Override
            public void onFinishRefresh() {
                mSwipeRefreshLayout.finisRefresh(1500);
            }
        });

        mRecyclerView.setLoadingListener(new RefreshRecyclerView.LoadingListener() {
            @Override
            public void onRefresh(Bundle args) {

            }

            @Override
            public void onLoadMore() {
                doLoadMore();
            }
        });
    }

    @Override
    protected void loadData() {
        super.loadData();
        doLoadMore();
    }

    private void doRefresh() {
        final String url = "https://aweme-hl.snssdk.com/aweme/v1/feed/?type=0&max_cursor=0&min_cursor=0&count=6&volume=0.4666666666666667&pull_type=1&need_relieve_aweme=0&filter_warn=0&req_from&is_cold_start=0&longitude=113.415009&latitude=23.175452&address_book_access=2&gps_access=1&ts=1554297990&js_sdk_version=1.13.5&app_type=normal&manifest_version_code=570&_rticket=1554297991540&ac=wifi&device_id=57241843363&iid=68032916025&mcc_mnc=46003&os_version=8.1.0&channel=huawei&version_code=570&device_type=ALP-AL00&language=zh&uuid=866158039332860&resolution=1080*1920&openudid=edbfdb25d051e3c3&update_version_code=5702&app_name=aweme&version_name=5.7.0&os_api=27&device_brand=HUAWEI&ssmix=a&device_platform=android&dpi=480&aid=1128&as=a135bb3a7638ac74b48577&cp=be8fc65a694ea747e1_kMo&mas=01e02df00479855b3127ee9ce502a3f7a0ececac1c2c462cecc686";

        HttpUtils.getJSON(true, url, headerMap, null, new HttpUtils.JSONCallback() {
            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onSuccess(JSONObject jsonObject, OkResponse okResponse) {

                JSONArray aweme_list = jsonObject.optJSONArray("aweme_list");
                List<DouYinVideo> list = parseList(aweme_list);
                if (null != list && !list.isEmpty()) {
                    mData.clear();
                    mData.addAll(list);
                }
                mRecyclerView.stopRefresh(false);
            }
        });
    }

    private List<DouYinVideo> parseList(JSONArray aweme_list) {
        if (null != aweme_list) {

            List<DouYinVideo> data = new ArrayList<>();

            for (int i = 0; i < aweme_list.length(); i++) {
                DouYinVideo douYinVideo = new DouYinVideo();
                data.add(douYinVideo);

                JSONObject item = aweme_list.optJSONObject(i);
                if (null != item) {
                    JSONObject video = item.optJSONObject("video");
                    JSONObject cover = video.optJSONObject("cover");
                    if (null != cover){
                        JSONArray url_list = cover.optJSONArray("url_list");
                        if (null != url_list) {
                            douYinVideo.cover = url_list.optString(0);
                        }
                    }

                    JSONObject play_addr = video.optJSONObject("play_addr");
                    if (null != play_addr) {
                        JSONArray url_list = play_addr.optJSONArray("url_list");
                        if (null != url_list) {
                            douYinVideo.video = url_list.optString(0);
                        }
                    }

                }

            }
            return data;
        }
        return null;
    }

    private void doLoadMore() {
        headerMap.put("X-SS-REQ-TICKET", "1554296913456");
        headerMap.put("X-Khronos", "1554296913");
        headerMap.put("X-Gorgon", "03006cc0800044eb139b270bafa522c70685da40a25bd797e93b");

        String url = "https://aweme-hl.snssdk.com/aweme/v1/feed/?type=0&max_cursor=0&min_cursor=-1&count=6&volume=0.4666666666666667&pull_type=2&need_relieve_aweme=0&filter_warn=0&req_from&is_cold_start=0&longitude=113.415009&latitude=23.175451&address_book_access=2&gps_access=1&ts=1554296913&js_sdk_version=1.13.5&app_type=normal&manifest_version_code=570&_rticket=1554296913458&ac=wifi&device_id=57241843363&iid=68032916025&mcc_mnc=46003&os_version=8.1.0&channel=huawei&version_code=570&device_type=ALP-AL00&language=zh&uuid=866158039332860&resolution=1080*1920&openudid=edbfdb25d051e3c3&update_version_code=5702&app_name=aweme&version_name=5.7.0&os_api=27&device_brand=HUAWEI&ssmix=a&device_platform=android&dpi=480&aid=1128&as=a1a5eb3a0135eca0a48900&cp=bc51c3591845aa01e1akOo&mas=0141da9511bc2fdf0539ce51bc0c21cc880c0c9c1c2c860c86c6a6";
        HttpUtils.getJSON(true, url, headerMap, null, new HttpUtils.JSONCallback() {
            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onSuccess(JSONObject jsonObject, OkResponse okResponse) {

                JSONArray aweme_list = jsonObject.optJSONArray("aweme_list");
                List<DouYinVideo> list = parseList(aweme_list);
                if(null !=list && !list.isEmpty()){
                    mData.addAll(list);
                }else{
                    mRecyclerView.setNoMore(true);
                }
                mRecyclerView.stopRefresh(true);
            }
        });


    }
    class DouYinVideoAdapter extends BaseRecycleViewAdapter<DouYinVideo>{

        public DouYinVideoAdapter(Context ctx, List<DouYinVideo> data) {
            super(ctx, data, R.layout.item_douyin_video_list);
        }

        @Override
        protected void bindView(BaseRecycleViewHolder holder, int position, DouYinVideo data) {
            holder.itemView.setTag(data);
            final TexturePlayer texturePlayer = holder.getView(R.id.texture_player);
            texturePlayer.setVideoMode(TexturePlayer.CENTER_MODE);
            //texturePlayer.mute();//静音
            final ImageView coverImage = holder.getView(R.id.iv_cover);
            coverImage.setVisibility(View.VISIBLE);
            if (position == 0) { //首个视频自动播放,因此提前设置播放地址
                texturePlayer.setVideoPath(data.video);
            }
            //加载缩略图
            ImageLoadUtils.disPlay(data.cover, coverImage);
            //监听SurfaceTexture的创建和销毁
            texturePlayer.setOnTextureChangeListener(new TexturePlayer.OnTextureChangeListener() {
                @Override
                public void onCreate(final TexturePlayer texturePlayer) {
                    mCurrPlayer = texturePlayer; //记录当前的播放器
                    mCurrPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setLooping(true); //设置循环
                            if (isResume) {
                                mp.start(); //播放视频
                            }

                            LogUtils.e("cys", texturePlayer.hashCode() + "->start");
                        }
                    });
                }

                @Override
                public void onDestory(TexturePlayer texturePlayer) {
                    /**
                     * texturePlayer销毁的时候记得把视频地址置null,因为滑动的时候只要texturePlayer
                     * 可见就会重新执行onSurfaceTextureAvailable回调,导致2个视频同时播放
                     */
                    texturePlayer.setUri(null);
                    //销毁后记得把封面图显示上
                    coverImage.setVisibility(View.VISIBLE);
                }
            });

            //保证封面图在视频播放之后才隐藏
            texturePlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        coverImage.setVisibility(View.GONE);
                    }
                    return false;
                }
            });

            texturePlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (texturePlayer.isPlaying()) {
                        texturePlayer.pause();
                    }else{
                        texturePlayer.start();
                        coverImage.setVisibility(View.GONE);
                    }

                }
            });

        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (null != mCurrPlayer) {
            mCurrPlayer.pause();
        }
        isResume = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mCurrPlayer && mCurrPlayer.isPrepare()) {
            mCurrPlayer.start();
        }
        isResume = true;
    }
}
