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
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import blog.csdn.net.mchenys.common.widget.view.VideoView;

/**
 * Created by mChenys on 2019/4/1.
 */

public class VideoListActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private List<String> urlList = new ArrayList<>();

    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        urlList.add("http://192.168.30.217:8080/shopping/video/4d4d2dd47e272003248c377861d4c352.mp4");
        urlList.add("http://192.168.30.217:8080/shopping/video/56f65bee7a5a68ddd7a95c515261107e.mp4");
        urlList.add("http://192.168.30.217:8080/shopping/video/7cb885ad3f55c8be32448568465c6d35.mp4");
        urlList.add("http://192.168.30.217:8080/shopping/video/d2e98883bb430a63be819dfea7dd3dc4.mp4");
        urlList.add("http://192.168.30.217:8080/shopping/video/d8c2dcfd6971e374e4158c83145e2deb.mp4");

    }

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_video_list;
    }

    @Override
    protected void initView() {
        super.initView();
        mRecyclerView = findViewById(R.id.rv_list);
        PagerSnapHelper snapHelper = new PagerSnapHelper() {
            @Override
            public View findSnapView(RecyclerView.LayoutManager layoutManager) {
                View view = super.findSnapView(layoutManager);
                if (null != view) {
                    final VideoView videoView = view.findViewById(R.id.video_view);
                    final ImageView coverImage = view.findViewById(R.id.iv_cover);
                    coverImage.setVisibility(view.getVisibility());
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            coverImage.setVisibility(View.GONE);
                            mp.setLooping(true);
                            mp.start();
                            LogUtils.e("cys", videoView.hashCode() + "->start");
                        }
                    });
                }


                return view;
            }
        };
        snapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new VideoListAdapter(this, urlList));
    }

    private class VideoListAdapter extends BaseRecycleViewAdapter<String> {

        public VideoListAdapter(Context ctx, List<String> data) {
            super(ctx, data, R.layout.item_video_list);
        }

        @Override
        protected void bindView(BaseRecycleViewHolder holder, int position, String data) {
            final VideoView videoView = holder.getView(R.id.video_view);
            final ImageView coverImage = holder.getView(R.id.iv_cover);
            coverImage.setVisibility(View.VISIBLE);
            MediaUtils.loadVideoThumbnail(coverImage, data,R.mipmap.ic_launcher,null);
            videoView.setVideoPath(data);
            if (position == 0) {
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        coverImage.setVisibility(View.GONE);
                        mp.setLooping(true);
                        mp.start();
                        LogUtils.e("cys", videoView.hashCode() + "->start");
                    }
                });
            }


            // Glide.with(mContext).load(Uri.parse(data)).crossFade().into(imageView);
           /* Bitmap videoThumbnail = BitmapUtils.loadVideoThumbnail(data, imageView.getWidth(), imageView.getHeight());
            imageView.setImageBitmap(videoThumbnail);*/


        }
    }
}
