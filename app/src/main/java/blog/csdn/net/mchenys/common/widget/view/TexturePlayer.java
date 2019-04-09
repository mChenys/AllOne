package blog.csdn.net.mchenys.common.widget.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;

import blog.csdn.net.mchenys.common.utils.LogUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;


/**
 * Created by mChenys on 2016/8/11.
 */
public class TexturePlayer extends TextureView implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener, MediaPlayer.OnVideoSizeChangedListener {
    private Uri mUri;
    private MediaPlayer mMediaPlayer;
    private Surface mSurface;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnInfoListener mOnInfoListener;
    private MediaPlayer.OnErrorListener mOnErrorListener;
    private AudioManager mAudioManager;
    private int mMaxVolume, mCurrVolume;
    private OnVoiceChangeListener mOnVoiceChangeListener;
    private boolean isPrepare; //是否已准备
    private boolean isPause; //是否已暂停
    private ConnectionChangeReceiver myReceiver;
    private OnTextureChangeListener mOnTextureChangeListener;
    private int mVideoWidth;//视频宽度
    private int mVideoHeight;//视频高度

    public static final int CENTER_CROP_MODE = 1;//中心裁剪模式
    public static final int CENTER_MODE = 2;//一边中心填充模式
    public int mVideoMode = 0;

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public Uri getUri() {
        return mUri;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setVideoMode(int mode) {
        mVideoMode = mode;
    }

    public TexturePlayer(Context context) {
        this(context, null);
    }

    public TexturePlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TexturePlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//最大音量
        setSurfaceTextureListener(this);
    }

    public void setVideoPath(String path) {
        LogUtils.e("cys", this.hashCode() + "->setVideoPath:" + path);
        if (!TextUtils.isEmpty(path)) {
            setVideoURI(Uri.parse(path));
        }

    }

    public void setVideoURI(Uri uri) {
        this.mUri = uri;
        openVideo();
    }

    private void openVideo() {
        if (mUri == null || mSurface == null) {
            return;
        }
        LogUtils.e("cys", this.hashCode() + "->openVideo");
        mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        release();
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnInfoListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setDataSource(getContext(), mUri);
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.prepareAsync();

        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        LogUtils.e("cys", this.hashCode() + "->onSurfaceTextureAvailable:width:" + width + " height:" + height);
        mSurface = new Surface(surface);//创建要展示的图形界面
        openVideo(); //创建完Surface就可以初始化mMediaPlayer了
        registerReceiver();
        if (null != mOnTextureChangeListener) {
            mOnTextureChangeListener.onCreate(this);
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        LogUtils.e("cys", this.hashCode() + "->onSurfaceTextureSizeChanged:width:" + width + " height:" + height);
        updateTextureViewSize(mVideoMode);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        LogUtils.e("cys", this.hashCode() + "->onSurfaceTextureDestroyed");
        mSurface = null;
        release();
        unregisterReceiver();
        if (null != mOnTextureChangeListener) {
            mOnTextureChangeListener.onDestory(this);
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void release() {
        if (mMediaPlayer != null) {
            LogUtils.e("cys", this.hashCode() + "->release");
            mMediaPlayer.setSurface(null);
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            isPrepare = false;
            isPause = false;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        LogUtils.e("cys", this.hashCode() + "->onPrepared");
        isPrepare = true;
        if (null != mOnVoiceChangeListener) {
            mOnVoiceChangeListener.onChange(getStreamVolume());
        }

        if (null != mOnPreparedListener) {
            mOnPreparedListener.onPrepared(mp);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (null != mOnCompletionListener) {
            mOnCompletionListener.onCompletion(mp);
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (null != mOnInfoListener) {
            return mOnInfoListener.onInfo(mp, what, extra);
        }
        LogUtils.e("cys", this.hashCode() + "->onInfo:what:" + what + " extra:" + extra);
        return false;
    }

    /**
     * @param mp
     * @param what
     * @param extra
     * @return True if the method handled the error, false if it didn't.
     * Returning false, or not having an OnErrorListener at all, will
     * cause the OnCompletionListener to be called.
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        if (null != mOnErrorListener) {
            return mOnErrorListener.onError(mp, what, extra);
        }
        LogUtils.e("cys", this.hashCode() + "->onError:what:" + what + " extra:" + extra);
        if (what == 1 && extra == -1004) {
            LogUtils.e("cys", this.hashCode() + "->onError 网络中断");
        }
        openVideo();
        return true;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) {
        this.mOnCompletionListener = l;
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        this.mOnPreparedListener = l;
    }

    public void setOnInfoListener(MediaPlayer.OnInfoListener l) {
        this.mOnInfoListener = l;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {
        this.mOnErrorListener = l;
    }

    public boolean isPlaying() {
        if (null != mMediaPlayer && isPrepare) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public void start() {
        LogUtils.e("cys", this.hashCode() + "->TexturePlayer:start1 isPrepare:" + isPrepare + " isPlaying:" + isPlaying());
        if (null != mMediaPlayer && isPrepare && !isPlaying()) {
            mMediaPlayer.start();
            isPause = false;
            LogUtils.e("cys", this.hashCode() + "->TexturePlayer:start2");
        }
    }

    public void pause() {
        if (null != mMediaPlayer && isPrepare) {
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public int getCurrentPosition() {
        if (null != mMediaPlayer && isPrepare) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (null != mMediaPlayer && isPrepare) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public void seekTo(int position) {
        if (null != mMediaPlayer && isPrepare) {
            mMediaPlayer.seekTo(position);
        }
    }

    public boolean isPrepare() {
        return this.isPrepare;
    }


    /**
     * 更新系统音量进度
     */
    private void updateVolumePosition(float volume) {
        int streamType = AudioManager.STREAM_MUSIC;
        int flags = 0; //0:不显示系统音量面板,1:显示
        mAudioManager.setStreamVolume(streamType, (int) volume, flags);
    }

    /**
     * 获取当前音量
     *
     * @return
     */
    private int getStreamVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 静音和非静音切换
     *
     * @return true 表示当前是静音
     */
    public boolean toggleVoice() {
        if (getStreamVolume() > 0) {
            //设置为静音
            mCurrVolume = getStreamVolume();
            updateVolumePosition(0);
            return true;
        } else {
            //恢复之前音量
            if (mCurrVolume == 0) {
                //如果之前也是静音,则加1格音量
                mCurrVolume += mMaxVolume / 10;
            }
            updateVolumePosition(mCurrVolume);
            return false;
        }
    }

    /**
     * 静音
     */
    public void mute() {
        mCurrVolume = 0;
        updateVolumePosition(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            // 音量减小
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mCurrVolume--;
                if (mCurrVolume < 0) {
                    mCurrVolume = 0;
                }
                updateVolumePosition(mCurrVolume);
                if (null != mOnVoiceChangeListener) {
                    mOnVoiceChangeListener.onChange(mCurrVolume);
                }
                return true;
            // 音量增大
            case KeyEvent.KEYCODE_VOLUME_UP:
                mCurrVolume++;
                if (mCurrVolume > mMaxVolume) {
                    mCurrVolume = mMaxVolume;
                }
                updateVolumePosition(mCurrVolume);
                if (null != mOnVoiceChangeListener) {
                    mOnVoiceChangeListener.onChange(mCurrVolume);
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//        LogUtils.e("cys", this.hashCode() + "->onVideoSizeChanged mVideoMode:" + mVideoMode);
//        LogUtils.e("cys", this.hashCode() + "->onVideoSizeChanged video w:" + width + " h:" + height);
//        LogUtils.e("cys", this.hashCode() + "->onVideoSizeChanged view w:" + getWidth() + " h:" + getHeight());

        mVideoHeight = mMediaPlayer.getVideoHeight();
        mVideoWidth = mMediaPlayer.getVideoWidth();

        updateTextureViewSize(mVideoMode);
//        mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
    }

    /**
     * @param mode 视频缩放模式 {@link #CENTER_CROP_MODE} or {@link #CENTER_MODE}. Default
     *             value is 0.
     */
    public void updateTextureViewSize(int mode) {
        if (mode == CENTER_MODE) {
            updateTextureViewSizeCenter();
        } else if (mode == CENTER_CROP_MODE) {
            updateTextureViewSizeCenterCrop();
        }
    }

    //重新计算video的显示位置，裁剪后全屏显示
    private void updateTextureViewSizeCenterCrop() {

        //获取View的宽高和视频的宽高比例
        float sx = (float) getWidth() / (float) mVideoWidth;
        float sy = (float) getHeight() / (float) mVideoHeight;

        Matrix matrix = new Matrix();
        float maxScale = Math.max(sx, sy);//取最大比例的来缩放,保证充满屏幕

        //第1步:把视频区移动到View区,使两者中心点重合.
        matrix.preTranslate((getWidth() - mVideoWidth) / 2, (getHeight() - mVideoHeight) / 2);

        //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(mVideoWidth / (float) getWidth(), mVideoHeight / (float) getHeight());

        //第3步,等比例放大或缩小,直到视频区的一边超过View一边, 另一边与View的另一边相等. 因为超过的部分超出了View的范围,所以是不会显示的,相当于裁剪了.
        matrix.postScale(maxScale, maxScale, getWidth() / 2, getHeight() / 2);//后两个参数坐标是以整个View的坐标系以参考的

        setTransform(matrix);
        postInvalidate();
    }

    //重新计算video的显示位置，让其全部显示并据中
    private void updateTextureViewSizeCenter() {

        float sx = (float) getWidth() / (float) mVideoWidth;
        float sy = (float) getHeight() / (float) mVideoHeight;

        Matrix matrix = new Matrix();
        float minScale = Math.min(sx, sy);//取最小的来缩放,保证最小的边和屏幕的边对齐

        //第1步:把视频区移动到View区,使两者中心点重合.
        matrix.preTranslate((getWidth() - mVideoWidth) / 2, (getHeight() - mVideoHeight) / 2);

        //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(mVideoWidth / (float) getWidth(), mVideoHeight / (float) getHeight());

        //第3步,等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
        matrix.postScale(minScale, minScale, getWidth() / 2, getHeight() / 2);
        setTransform(matrix);
        postInvalidate();
    }

    /**
     * 音量按键变化监听
     */
    public interface OnVoiceChangeListener {
        void onChange(int volume);
    }

    public void setOnKeyVoiceChangeListener(OnVoiceChangeListener l) {
        this.mOnVoiceChangeListener = l;
    }

    //监听texture的状态
    public interface OnTextureChangeListener {
        void onCreate(TexturePlayer texturePlayer);

        void onDestory(TexturePlayer texturePlayer);
    }

    public void setOnTextureChangeListener(OnTextureChangeListener l) {
        this.mOnTextureChangeListener = l;
    }

    private class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiNetInfo.isConnected()) {
                //ToastUtils.show(context, "wifi已连上");
            } else {
                if (isPlaying()) {
                    ToastUtils.show(context, "为了节省您的流量，请在Wi-Fi下观看视频", 0);
                }
            }
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver = new ConnectionChangeReceiver();
        getContext().registerReceiver(myReceiver, filter);
    }

    private void unregisterReceiver() {
        getContext().unregisterReceiver(myReceiver);
    }

}
