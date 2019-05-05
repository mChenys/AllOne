package blog.csdn.net.mchenys.common.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.okhttp2.x.HttpManager;
import blog.csdn.net.mchenys.common.okhttp2.x.listener.RequestCallBackHandler;
import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;
import blog.csdn.net.mchenys.common.utils.RSAUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;


/**
 * 滑动验证码
 * Created by lcn on 2018/1/9.
 */

public class SliderCaptchaView extends FrameLayout implements View.OnTouchListener {

    private Context mContext;

    private LoadingView mLoadingView;
    private ImageView mCaptchaThumbIv; //参考小图
    private ImageView mCaptchaImgIv; //参考大图
    private Button mSliderBtn; //滑块
    private ImageView flushBtn; //刷新按钮
    private TextView sliderTipTv; //滑动条提示
    private CheckResult mCheckResult;

    private boolean isBigLoadSuccess; //大图是否加载完成
    private boolean isThumbLoadSuccess; // 小图是否加载完成


    private Random mRandom;
    private DecimalFormat mDecimalFormat = new DecimalFormat(".00");
    private String cookie;
    private long pressTime; //手指按下的时间
    private int leftBorder; //左边界
    private int rightBorder; //右边界
    private float scale; //图片缩放倍数
    private int lastX;
    private List<String> pointsList = new ArrayList<>(); //滑动轨迹点集(x,y)

    public SliderCaptchaView(@NonNull Context context) {
        this(context, null);
    }

    public SliderCaptchaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SliderCaptchaView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mRandom = new Random(1);
        init();
    }

    private void init() {
        initView();
        setListener();
        flushCaptcha();
    }

    private void initView() {
        View.inflate(mContext, R.layout.slider_captcha_view, this);
        mLoadingView = findViewById(R.id.loading);
        mCaptchaImgIv = findViewById(R.id.big_image);
        mCaptchaThumbIv = findViewById(R.id.small_image);
        mSliderBtn = findViewById(R.id.slider_btn);
        flushBtn = findViewById(R.id.flesh_button);
        sliderTipTv = findViewById(R.id.slider_bg);
        mLoadingView.show("图片加载中");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        leftBorder = mCaptchaImgIv.getLeft();
        rightBorder = mCaptchaImgIv.getRight();
        scale = mCaptchaImgIv.getMeasuredWidth() / 230f;
    }

    private void setListener() {
        mSliderBtn.setOnTouchListener(this);
        flushBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCaptchaThumbIv.layout(leftBorder, mCaptchaThumbIv.getTop(), mCaptchaThumbIv.getMeasuredWidth() + leftBorder, mCaptchaThumbIv.getBottom());
                mSliderBtn.layout(leftBorder, mSliderBtn.getTop(), mSliderBtn.getMeasuredWidth() + leftBorder, mSliderBtn.getBottom());
                flushCaptcha();
            }
        });
    }

    /**
     * 刷新验证码
     */
    public void flushCaptcha() {
        mLoadingView.show("图片加载中");
        final float randomNum = mRandom.nextFloat();
        String url = Urls.SLIDE_CAPTCHA_FLUSH + "?" + randomNum;
        HashMap<String, String> header = new HashMap<>();
        header.put("Referer", "http://dev2.pconline.com.cn:81/captcha/slidecaptcha_test.jsp");
        HttpManager.getInstance().asyncRequest(url, new RequestCallBackHandler() {
            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public Object doInBackground(OkResponse response) {
                return null;
            }

            @Override
            public void onResponse(Object obj, OkResponse response) {
                if (response.getCode() == 200) {
                    cookie = response.getHeaders().get("Set-Cookie").get(0);
                    flushCaptchaImg(randomNum);
                    flushCaptchaThumb(randomNum);
                }
            }

        }, HttpManager.RequestType.FORCE_NETWORK, HttpManager.RequestMode.GET, "", header, null);
    }

    /**
     * 刷新大图
     *
     * @param randomNum
     */
    private void flushCaptchaImg(float randomNum) {
        isBigLoadSuccess = false;
        String url = Urls.SLIDE_CAPTCHA_IMG + "?" + randomNum;
        HashMap<String, String> header = new HashMap<>();
        header.put("Cookie", cookie);
        header.put("Referer", "http://dev2.pconline.com.cn:81/captcha/slidecaptcha_test.jsp");
        HttpManager.getInstance().asyncRequestForInputStream(url, new RequestCallBackHandler() {
            @Override
            public void onFailure(Exception e) {
                isBigLoadSuccess = false;
            }

            @Override
            public Object doInBackground(OkResponse response) {
                if (response.getCode() == 200)
                    return BitmapFactory.decodeStream(response.getInputStream());
                return null;
            }

            @Override
            public void onResponse(Object o, OkResponse response) {
                isBigLoadSuccess = true;
                if (isThumbLoadSuccess) mLoadingView.hide();
                mCaptchaImgIv.setImageBitmap((Bitmap) o);
            }



        }, HttpManager.RequestMode.GET, "", header, null);
    }

    /**
     * 刷新小图
     *
     * @param randomNum
     */
    private void flushCaptchaThumb(float randomNum) {
        isThumbLoadSuccess = false;
        String url = Urls.SLIDE_CAPTCHA_THUMB + "?" + randomNum;
        HashMap<String, String> header = new HashMap<>();
        header.put("Cookie", cookie);
        header.put("Referer", "http://dev2.pconline.com.cn:81/captcha/slidecaptcha_test.jsp");
        HttpManager.getInstance().asyncRequestForInputStream(url, new RequestCallBackHandler() {
            @Override
            public void onFailure(Exception e) {
                isThumbLoadSuccess = false;
            }

            @Override
            public Object doInBackground(OkResponse response) {
                if (response.getCode() == 200) {
                    return BitmapFactory.decodeStream(response.getInputStream());
                }
                return null;
            }

            @Override
            public void onResponse(Object o, OkResponse response) {
                isThumbLoadSuccess = true ;
                if (isBigLoadSuccess) mLoadingView.hide();
                mCaptchaThumbIv.setImageBitmap((Bitmap) o);
            }


        }, HttpManager.RequestMode.GET, "", header, null);
    }


    /**
     * 校验验证码
     *
     * @param time
     * @param point
     */
    private void checkCaptcha(final long time, String point) {
        String url = generateUrl(time, point, transPointList2Str());
        HashMap<String, String> header = new HashMap<>();
        header.put("Cookie", cookie);
        header.put("Referer", "http://dev2.pconline.com.cn:81/captcha/slidecaptcha_test.jsp");
        HttpManager.getInstance().asyncRequest(url, new RequestCallBackHandler() {
            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public Object doInBackground(OkResponse response) {
                return null;
            }

            @Override
            public void onResponse(Object obj, OkResponse response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.getResult());
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("message");

                    if (code == 0) {
                        sliderTipTv.setText("验证通过");
                        sliderTipTv.setTextColor(Color.parseColor("#3eb45a"));
                        mSliderBtn.setVisibility(INVISIBLE);
                    } else {
                        ToastUtils.show(mContext, "用时" + time + "ms\n" + message, 0);
                    }

                    if (mCheckResult != null) {
                        mCheckResult.onResponse(code == 0, cookie);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, HttpManager.RequestType.FORCE_NETWORK, HttpManager.RequestMode.GET, "", header, null);
    }


    /**
     * 拼接Url参数
     *
     * @param time
     * @param point
     * @param trajectory
     * @return
     */
    private String generateUrl(long time, String point, String trajectory) {
        String url = Urls.SLIDE_CAPTCHA_CHECK;
        url += "?a=" + encrypt(String.valueOf(time));
        int sum = trajectory.length() / 100 + (trajectory.length() % 100 == 0 ? 0 : 1);
        for (int i = 0; i < sum; i++) {
            if (i == sum - 1) {
                url += "&b[]=" + encrypt(trajectory.substring(i * 100, trajectory.length()));
            } else {
                url += "&b[]=" + encrypt(trajectory.substring(i * 100, 100 * (i + 1)));
            }
        }
        url += "&c=" + encrypt(point);
        return url;
    }

    /***
     * 轨迹点集合转字符串
     * @return
     */
    private String transPointList2Str() {
        String str = "";
        for (int i = 0; i < pointsList.size(); i++) {
            str += pointsList.get(i) + "-";
        }
        str = str.substring(0, str.length() - 1);
        return str;
    }

    /***
     * 加密数据
     * @param data 数据源
     * @return 加密字符串
     */
    private String encrypt(String data) {
        return URLEncoder.encode(Base64.encodeToString(RSAUtils.processData(data.getBytes(), RSAUtils.keyStrToPublicKey(Constant.PUBLIC_KEY), Cipher.ENCRYPT_MODE), Base64.DEFAULT));
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressTime = System.currentTimeMillis();
                lastX = (int) event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                //边界判断
                if ((mCaptchaThumbIv.getLeft() < leftBorder && dx < 0) || (mCaptchaThumbIv.getRight() > rightBorder && dx > 0))
                    dx = 0;
                //移动滑块
                view.layout(view.getLeft() + dx, view.getTop(), view.getRight() + dx, view.getBottom());
                //移动小图
                mCaptchaThumbIv.layout(mCaptchaThumbIv.getLeft() + dx, mCaptchaThumbIv.getTop(), mCaptchaThumbIv.getRight() + dx, mCaptchaThumbIv.getBottom());

                float x = (mCaptchaThumbIv.getLeft() - leftBorder) / scale;
                float y = event.getRawY();
                String point = mDecimalFormat.format(x) + "," + mDecimalFormat.format(y);
                pointsList.add(point);

                lastX = (int) event.getRawX();
                break;
            case MotionEvent.ACTION_UP:
                long payTime = System.currentTimeMillis() - pressTime;
                point = mCaptchaImgIv.getX() + "," + mCaptchaImgIv.getY();
                checkCaptcha(payTime, point);
                pointsList.clear();
                break;
            default:
                break;
        }
        return false;
    }

    public void setCheckResult(CheckResult mCheckResult) {
        this.mCheckResult = mCheckResult;
    }

    public interface CheckResult {
        void onResponse(boolean isSuccess, String cookie);
    }
}
