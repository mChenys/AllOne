package blog.csdn.net.mchenys.module.demo.pagersnaphelper;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.utils.LogUtils;
import blog.csdn.net.mchenys.common.utils.MediaUtils;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewAdapter;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewHolder;
import blog.csdn.net.mchenys.common.widget.view.TexturePlayer;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;

/**
 * Created by mChenys on 2019/4/1.
 */

public class TextureListActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private List<String> urlList = new ArrayList<>();
    private PagerSnapHelper mPagerSnapHelper;
    private TexturePlayer mCurrPlayer;
    private boolean isResume; //当前是否可见

    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
       /* urlList.add("http://192.168.30.217:8080/shopping/video/4d4d2dd47e272003248c377861d4c352.mp4");
        urlList.add("http://192.168.30.217:8080/shopping/video/56f65bee7a5a68ddd7a95c515261107e.mp4");
        urlList.add("http://192.168.30.217:8080/shopping/video/7cb885ad3f55c8be32448568465c6d35.mp4");
        urlList.add("http://192.168.30.217:8080/shopping/video/d2e98883bb430a63be819dfea7dd3dc4.mp4");
        urlList.add("http://192.168.30.217:8080/shopping/video/d8c2dcfd6971e374e4158c83145e2deb.mp4");*/


        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201805/100651/201805181532123423.mp4");
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803151735198462.mp4");
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803150923220770.mp4");
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803150922255785.mp4");
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803150920130302.mp4");
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803141625005241.mp4");
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803141624378522.mp4");
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803131546119319.mp4");
    }

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_video_list;
    }

    @Override
    protected void initView() {
        super.initView();
        mRecyclerView = findViewById(R.id.rv_list);
        mPagerSnapHelper = new PagerSnapHelper() {
            @Override
            public View findSnapView(RecyclerView.LayoutManager layoutManager) {
                // 在 Adapter的 onBindViewHolder 之后执行
                LogUtils.e("cys", "===========findSnapView=============");
                View view = super.findSnapView(layoutManager);

                String url = (String) view.getTag();
                final TexturePlayer nextPlayer = view.findViewById(R.id.texture_player);
                final ImageView coverImage = view.findViewById(R.id.iv_cover);
                coverImage.setVisibility(View.VISIBLE);
                nextPlayer.setVideoPath(url); //重新设置播放地址

                return view;
            }
        };
        mPagerSnapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new VideoListAdapter(this, urlList));
    }

    private class VideoListAdapter extends BaseRecycleViewAdapter<String> {

        public VideoListAdapter(Context ctx, List<String> data) {
            super(ctx, data, R.layout.item_texture_list);
        }

        @Override
        protected void bindView(BaseRecycleViewHolder holder, int position, String data) {
            holder.itemView.setTag(data);
            final TexturePlayer texturePlayer = holder.getView(R.id.texture_player);
            texturePlayer.setVideoMode(TexturePlayer.CENTER_MODE);
            final ImageView coverImage = holder.getView(R.id.iv_cover);
            coverImage.setVisibility(View.VISIBLE);
            if (position == 0) { //首个视频自动播放,因此提前设置播放地址
                texturePlayer.setVideoPath(data);
            }
            //加载缩略图
            MediaUtils.loadVideoThumbnail(coverImage, data);

            //监听SurfaceTexture的创建和销毁
            texturePlayer.setOnTextureChangeListener(new TexturePlayer.OnTextureChangeListener() {
                @Override
                public void onCreate(TexturePlayer texturePlayer) {
                    mCurrPlayer = texturePlayer; //记录当前的播放器
                    mCurrPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setLooping(true); //设置循环
                            if (isResume) {
                                mp.start(); //播放视频
                            }

                            LogUtils.e("cys", mCurrPlayer.hashCode() + "->start");
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
