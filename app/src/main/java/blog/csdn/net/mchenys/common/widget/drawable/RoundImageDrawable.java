package blog.csdn.net.mchenys.common.widget.drawable;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

public class RoundImageDrawable extends Drawable {

    private Paint mPaint;
    private Bitmap mBitmap;

    private RectF rectF;
    private int coverColor;
    private float radius;
    //除了那几个角不需要圆角的
    private boolean exceptLeftTop, exceptRightTop, exceptLeftBottom, exceptRightBotoom;


    public RoundImageDrawable(Bitmap bitmap, int coverColor, float radius) {
        this(bitmap);
        this.coverColor = coverColor;
        this.radius = radius;
    }

    public RoundImageDrawable(Bitmap bitmap) {
        mBitmap = bitmap;
        BitmapShader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP,
                TileMode.CLAMP);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(bitmapShader);
    }

    /**
     * 除了那几个角不需要圆角的
     *
     * @param leftTop
     * @param rightTop
     * @param leftBottom
     * @param rightBottom
     */
    public void setExceptCorner(boolean leftTop, boolean rightTop, boolean leftBottom, boolean rightBottom) {
        this.exceptLeftTop = leftTop;
        this.exceptRightTop = rightTop;
        this.exceptLeftBottom = leftBottom;
        this.exceptRightBotoom = rightBottom;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        rectF = new RectF(left, top, right, bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        if (radius == 0) {
            radius = 30f;
        }
        canvas.drawRoundRect(rectF, radius, radius, mPaint);


        Paint coverPaint = new Paint();
        coverPaint.setAntiAlias(true);
        coverPaint.setColor(coverColor);
        if(coverColor > -1) {
            canvas.drawRoundRect(rectF, radius, radius, coverPaint);
        }

        if (exceptLeftTop) { //左上角不为圆角
            canvas.drawRect(0, 0, radius, radius, mPaint);
            if (coverColor > -1) {
                canvas.drawRect(0, 0, radius, radius, coverPaint);
            }
        }
        if (exceptRightTop) {//右上角不为圆角
            canvas.drawRect(canvas.getWidth() - radius, 0, canvas.getWidth(), radius, mPaint);
            if (coverColor > -1) {
                canvas.drawRect(canvas.getWidth() - radius, 0, canvas.getWidth(), radius, coverPaint);
            }
        }

        if (exceptLeftBottom) {//左下角不为圆角
            canvas.drawRect(0, canvas.getHeight() - radius, radius, canvas.getHeight(), mPaint);
            if (coverColor > -1) {
                canvas.drawRect(0, canvas.getHeight() - radius, radius, canvas.getHeight(), coverPaint);
            }
        }

        if (exceptRightBotoom) {//右下角不为圆角
            canvas.drawRect(canvas.getWidth() - radius, canvas.getHeight() - radius, canvas.getWidth(), canvas.getHeight(), mPaint);
            if (coverColor > -1) {
                canvas.drawRect(canvas.getWidth() - radius, canvas.getHeight() - radius, canvas.getWidth(), canvas.getHeight(), coverPaint);
            }
        }

    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmap.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmap.getHeight();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

}
