package blog.csdn.net.mchenys.common.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import blog.csdn.net.mchenys.R;


public class CoverImageView extends android.support.v7.widget.AppCompatImageView {
    private int coverColor;
    private Paint mBitmapPaint;

    public CoverImageView(Context context) {
        super(context);
    }

    public CoverImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public CoverImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CoverImageView);
        coverColor = a.getColor(R.styleable.CoverImageView_coverColor, -1);
        a.recycle();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setColor(coverColor);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (coverColor != -1) {
            canvas.drawColor(coverColor);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
