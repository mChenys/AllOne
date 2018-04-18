package blog.csdn.net.mchenys.common.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 圆形百分比进度条
 * Created by mChenys on 2016/11/8.
 */
public class CircleProgressView extends View {
    private int width;//控件宽度
    private int height;//控件高
    private float radius;//半径
    private float pointX;//圆心x坐标
    private float pointY;//圆心y坐标
    private int circleBackgroundColor;//背景颜色
    private int circleBackgroundAlpha; //背景透明度
    private int circleRingColor;//进度颜色
    private int progressTextColor;//进度文本的颜色
    private int progressTextSize;//进度文本的大小
    private int circleRingWidth; //进度的宽度
    private int progress;//进度值
    private boolean isRingStyle;//是否是空心的圆环进度样式

    private Paint mPaint;

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        circleBackgroundColor = Color.WHITE;
        circleRingColor = Color.parseColor("#3A91FF");
        progressTextColor = Color.BLACK;
        circleBackgroundAlpha = 128;
        circleRingWidth = 10;
        progressTextSize = 32;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        radius = Math.min(width, height) / 2.0f;
        pointX = width / 2.0f;
        pointY = height / 2.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawCircleRing(canvas);
        drawProgressText(canvas);
    }

    /**
     * 绘制背景色
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        mPaint.setColor(circleBackgroundColor);
        mPaint.setAlpha(circleBackgroundAlpha);
        canvas.drawCircle(pointX, pointY, radius, mPaint);
    }

    /**
     * 绘制圆环
     *
     * @param canvas
     */
    private void drawCircleRing(Canvas canvas) {
        mPaint.setColor(circleRingColor);
        RectF oval = new RectF();
        if (isRingStyle) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(circleRingWidth);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            //注意:如果是空心圆环的矩形区域是需要考虑圆环的宽度
            oval.left = circleRingWidth / 2.0f;
            oval.top = height / 2.0f - radius + circleRingWidth / 2.0f;
            oval.right = 2 * radius - circleRingWidth / 2.0f;
            oval.bottom = height / 2.0f + radius - circleRingWidth / 2.0f;
            canvas.drawArc(oval, 0, 360 * progress / 100, false, mPaint);
        } else {
            mPaint.setStyle(Paint.Style.FILL);
            oval.left = 0;
            oval.top = height / 2.0f - radius;
            oval.right = 2 * radius;
            oval.bottom = height / 2.0f + radius;
            canvas.drawArc(oval, 0, 360 * progress / 100, true, mPaint);
        }

    }

    /**
     * 绘制进度文本
     *
     * @param canvas
     */
    private void drawProgressText(Canvas canvas) {
        mPaint.setColor(progressTextColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(progressTextSize);
        Rect textBound = new Rect();
        String text = progress + "%";
        mPaint.getTextBounds(text, 0, text.length(), textBound);
        int textWidth = textBound.width();
        int textHeight = textBound.height();
        float x = pointX - textWidth / 2.0f;
        float y = pointY + textHeight / 2.0f;
        canvas.drawText(text, x, y, mPaint);
    }

    /**
     * 更新进度
     *
     * @param progress
     */
    public void setValue(int progress) {
        this.progress = progress;
        invalidate();
    }

    /**
     * 设置进度圆环的样式
     *
     * @param isRing true是空心的圆环,false表示实心的圆环
     */
    public void setCircleRingStyle(boolean isRing) {
        this.isRingStyle = isRing;
    }

    /**
     * 设置背景透明度
     *
     * @param alpha 0~255
     */
    public void setCircleBackgroundAlpha(int alpha) {
        this.circleBackgroundAlpha = alpha;
    }

    /**
     * 设置背景颜色
     *
     * @param circleBackgroundColor
     */
    public void setCircleBackgroundColor(int circleBackgroundColor) {
        this.circleBackgroundColor = circleBackgroundColor;
    }

    /**
     * 设置圆环的颜色
     *
     * @param circleRingColor
     */
    public void setCircleRingColor(int circleRingColor) {
        this.circleRingColor = circleRingColor;
    }

    /**
     * 设置进度文本的颜色
     *
     * @param progressTextColor
     */
    public void setProgressTextColor(int progressTextColor) {
        this.progressTextColor = progressTextColor;
    }

    /**
     * 设置圆环的宽度
     *
     * @param circleRingWidth
     */
    public void setCircleRingWidth(int circleRingWidth) {
        this.circleRingWidth = circleRingWidth;
    }

    /**
     * 设置进度文本的字体大小
     *
     * @param progressTextSize
     */
    public void setProgressTextSize(int progressTextSize) {
        this.progressTextSize = progressTextSize;
    }
}
