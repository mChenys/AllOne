package blog.csdn.net.mchenys.common.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

/**
 * Created by mChenys on 2015/11/20.
 */
public class DrawableUtils {
    /**
     * 创建Shape 圆角矩形
     *
     * @param color  背景颜色
     * @param radius 圆角半径
     * @return
     */
    public static GradientDrawable getGradientDrawable(int color, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        //设置形状为矩形
        drawable.setGradientType(GradientDrawable.RECTANGLE);
        //设置圆角半径
        drawable.setCornerRadius(radius);
        //设置颜色
        drawable.setColor(color);
        return drawable;
    }



    /**
     * 设置状态选择器
     *
     * @param before 默认时的Drawable
     * @param after  生效后的显示的Drawable
     * @param state 各种状态
     * @return
     */
    public static StateListDrawable getDrawableSelector(Drawable before, Drawable after, int... state) {
        if (null == state || (null != state && state.length == 0)) {
            throw new IllegalArgumentException("There must be a kind of state");
        }
        StateListDrawable selector = new StateListDrawable();
        selector.addState(state, after);
        //默认图片
        selector.addState(new int[]{}, before);
        return selector;
    }

    /**
     * 获取图片的背景选择器
     *
     * @param beforeColor 默认的背景颜色
     * @param afterColor  生效时时的背景颜色
     * @param radius      圆角矩形的半径
     * @param state       各种生效后的状态
     * @return
     */
    public static StateListDrawable getDrawableSelector(int beforeColor, int afterColor, float radius, int... state) {
        if (null == state || (null != state && state.length == 0)) {
            throw new IllegalArgumentException("There must be a kind of state");
        }
        Drawable before = getGradientDrawable(beforeColor, radius);
        Drawable after = getGradientDrawable(afterColor, radius);
        return getDrawableSelector(before, after, state);
    }

    /**
     * 获取图片按下和抬起的背景选择器
     * @param beforeColor 默认的背景颜色
     * @param afterColor  生效时时的背景颜色
     * @param radius      圆角矩形的半径
     * @return
     */
    public static StateListDrawable getDrawablePressSelector(int beforeColor, int afterColor, float radius) {
        return getDrawableSelector(beforeColor, afterColor, radius,android.R.attr.state_pressed);
    }
    /**
     * 设置图片渐现效果
     *
     * @param view
     * @param bitmap
     */
    public static void setFadeIn(ImageView view, Bitmap bitmap) {
            Drawable pre = new ColorDrawable(Color.TRANSPARENT);
            Drawable next = new BitmapDrawable(bitmap);
        setFadeIn(view, pre, next);
    }

    public static void setFadeIn(ImageView view, int resId) {
        Drawable pre = new ColorDrawable(Color.TRANSPARENT);
        Drawable next = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            next = view.getContext().getDrawable(resId);
        } else {
            next = view.getContext().getResources().getDrawable(resId);
        }
        setFadeIn(view, pre, next);
    }

    /**
     * 设置图片渐现效果
     *
     * @param view
     * @param pre
     * @param next
     */
    public static void setFadeIn(ImageView view, Drawable pre, Drawable next) {
        TransitionDrawable drawable = new TransitionDrawable(new Drawable[]{pre, next});
        view.setImageDrawable(null);
        view.setImageDrawable(drawable);
        drawable.startTransition(500);
    }
}
