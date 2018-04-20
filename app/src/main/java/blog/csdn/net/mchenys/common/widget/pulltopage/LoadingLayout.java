package blog.csdn.net.mchenys.common.widget.pulltopage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import blog.csdn.net.mchenys.R;

/**
 * 公用下载刷新/加载更多
 */
public class LoadingLayout extends FrameLayout implements ILoadingLayout{

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;
	
	private final ImageView mRefreshIv;
	private final ProgressBar mRefreshPb;
	/**一级提示*/
	private final TextView mFirstTipTv;
	/**二级提示*/
	private TextView mSecondTipTv;

	//各种文案
	private String pullTip;//下拉翻页
	private String refreshTip;//正在加载
	private String releaseTip;//释放翻页
	private String secondTip;//二级提示

	public void setPullTip(String tile) {
		this.pullTip = tile;
	}
	public void setRefreshTip(String refreshTip){
		this.refreshTip = refreshTip;
	}

	public void setReleaseTip(String releaseTip) {
		this.releaseTip = releaseTip;
	}

	public void setSecondTip(String secondTip) {
		this.secondTip = secondTip;
	}
	private final Animation mRotateUpAnim, mRotateDownAnim;

	public LoadingLayout(Context context, final int mode, String releaseTip, String pullTip, String refreshTip) {
		super(context);
		this.releaseTip = releaseTip;
		this.pullTip = pullTip;
		this.refreshTip = refreshTip;

		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.lib_app_pulltorefresh_head, this);
		mFirstTipTv = header.findViewById(R.id.tv_first_tip);
		mSecondTipTv =  header.findViewById(R.id.tv_second_tip);
		mRefreshIv =  header.findViewById(R.id.iv_refresh);
		mRefreshPb =  header.findViewById(R.id.pb_refresh);

		//逆时针
		final Interpolator interpolator = new LinearInterpolator();
		mRotateUpAnim = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF,
		        0.5f);
		mRotateUpAnim.setInterpolator(interpolator);
		mRotateUpAnim.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		mRotateUpAnim.setFillAfter(true);
		//顺时针
		mRotateDownAnim = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f,
		        Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateDownAnim.setInterpolator(interpolator);
		mRotateDownAnim.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		mRotateDownAnim.setFillAfter(true);

		switch (mode) {
			case PullToPageBase.MODE_PULL_UP_TO_REFRESH:
			    //设置向上箭头
				mRefreshIv.setImageResource(R.drawable.app_pull_up_arrow);
				break;
			case PullToPageBase.MODE_PULL_DOWN_TO_REFRESH:
			default:
			    //设置向下箭头
				mRefreshIv.setImageResource(R.drawable.app_pull_down_arrow);
				break;
		}
	}
	//重置
	public void reset() {
		mFirstTipTv.setText(pullTip);
		mRefreshIv.setVisibility(View.VISIBLE);
		mRefreshPb.setVisibility(View.GONE);
	}

	//释放加载
	public void releaseToRefresh() {
        mSecondTipTv.setText(secondTip);
        mRefreshPb.setVisibility(View.INVISIBLE);
		mFirstTipTv.setText(releaseTip);
		mRefreshIv.clearAnimation();
		mRefreshIv.startAnimation(mRotateUpAnim);
		
	}
	
	//开始下拉时执行
	public void startPull() {
        mSecondTipTv.setText(secondTip);
        mRefreshPb.setVisibility(View.INVISIBLE);
        mFirstTipTv.setText(pullTip);
	}

	//正在加载
	public void loading() {
        mSecondTipTv.setText(secondTip);
		mFirstTipTv.setText(refreshTip);
		mRefreshIv.clearAnimation();
		mRefreshPb.setVisibility(View.INVISIBLE);
	}

	//下拉刷新
	public void pullToRefresh() {
	    mRefreshPb.setVisibility(View.INVISIBLE);
		mFirstTipTv.setText(pullTip);
		mRefreshIv.clearAnimation();
		mRefreshIv.startAnimation(mRotateDownAnim);
	}

	@Override
	public View getView() {
		return this;
	}

	public void setTextColor(int color) {
		mFirstTipTv.setTextColor(color);
	}

}
