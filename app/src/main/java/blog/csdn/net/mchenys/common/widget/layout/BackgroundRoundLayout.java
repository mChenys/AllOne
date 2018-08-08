/*
 *    Copyright 2015 Kaopiz Software Co., Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package blog.csdn.net.mchenys.common.widget.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import blog.csdn.net.mchenys.R;

/**
 * 带圆角的背景view
 */
class BackgroundRoundLayout extends FrameLayout {

    private float mCornerRadius;
    private Paint mPaint;
    private RectF mRect;

    public BackgroundRoundLayout(Context context) {
        this(context,null);
    }

    public BackgroundRoundLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BackgroundRoundLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int backgroundColor = getResources().getColor(android.R.color.white);
        if (getBackground() != null && getBackground() instanceof ColorDrawable) {
            backgroundColor = ((ColorDrawable) getBackground().mutate()).getColor();
        }
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BackgroundRoundLayout);
        int baseColor = array.getColor(R.styleable.BackgroundRoundLayout_baseColor,backgroundColor);
        float  radius = array.getDimension(R.styleable.BackgroundRoundLayout_radius, 0);
        array.recycle();

        if(radius==0){
            setCornerRadiusDip(15);
        }else{
            mCornerRadius = radius;
        }

        setBaseColor(baseColor);
        // 刷新
        setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
    }


    /**
     * 设置背景的圆角
     * @param radius
     */
    public void setCornerRadiusDip(float radius) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        mCornerRadius = scale * radius;
    }


    /**
     * 设置背景颜色
     * @param color
     */
    public void setBaseColor(int color) {
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRect = new RectF(0, 0, w, h);
        Log.e("cys", "onSizeChanged");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("cys", "onDraw:"+mCornerRadius);
        canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, mPaint);
    }
}
