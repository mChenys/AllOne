package blog.csdn.net.mchenys.common.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import blog.csdn.net.mchenys.common.widget.recycleview.MySmoothTopScroller;


/**
 * Created by mChenys on 2018/10/30.
 */

public class UIUtils {
    public static Activity scanForActivity(Context ctx) {
        if (ctx == null)
            return null;
        else if (ctx instanceof Activity)
            return (Activity) ctx;
        else if (ctx instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) ctx).getBaseContext());

        return null;
    }

    /**
     * 获取状态栏高度/像素
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 修改状态栏为透明效果
     */
    public static void setStatusBarTrans(Activity activity) {
        //5.0及以上,修改完后,布局会延伸到状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);

        }
        //4.4到5.0
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    localLayoutParams.flags;
        }
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param activity
     * @param colorId
     */
    public static void setStatusBarColor(Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(colorId));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //使用SystemBarTint库使4.4版本状态栏变色，需要先将状态栏设置为透明
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    localLayoutParams.flags;

            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(colorId);
        }
    }


    /**
     * 修改TabLayout指示线的宽度自适应
     *
     * @param tab
     */
    public static void setTabLineWidthAuto(TabLayout tab) {
        try {
            Class<?> tabLayout = tab.getClass();
            Field mTabStrip = tabLayout.getDeclaredField("mTabStrip");
            mTabStrip.setAccessible(true);
            LinearLayout mTabStripLl = (LinearLayout) mTabStrip.get(tabLayout);
            for (int i = 0; i < mTabStripLl.getChildCount(); i++) {
                View tabView = mTabStripLl.getChildAt(i);

                //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                mTextViewField.setAccessible(true);
                TextView mTextView = (TextView) mTextViewField.get(tabView);
                tabView.setPadding(0, 0, 0, 0);

                //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                int width = mTextView.getWidth();
                if (width == 0) {
                    mTextView.measure(0, 0);
                    width = mTextView.getMeasuredWidth();
                }
                //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                params.width = width;
                params.leftMargin = DisplayUtils.dip2px(tab.getContext(), 10);
                params.rightMargin = DisplayUtils.dip2px(tab.getContext(), 10);
                ;
                tabView.setLayoutParams(params);

                tabView.invalidate();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * android 4.4及以上修改状态栏文字的颜色
     * 修改状态栏文字颜色，这里小米，魅族区别对待。
     */
    public static void setLightStatusBar(final Activity activity, final boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            switch (RomUtils.getLightStatusBarAvailableRomType()) {
                case RomUtils.AvailableRomType.MIUI:
                    setMIUIStatusBarLightMode(activity, dark);
                    break;

                case RomUtils.AvailableRomType.FLYME:
                    setFlymeLightStatusBar(activity, dark);
                    break;

                case RomUtils.AvailableRomType.ANDROID_NATIVE:
                    setAndroidNativeLightStatusBar(activity, dark);
                    break;

            }
        }
    }

    /**
     * 小米系统下状态栏文字颜色的修改
     *
     * @param activity
     * @param dark
     * @return
     */
    public static boolean setMIUIStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && RomUtils.isMiUIV7OrAbove()) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (dark) {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    }
                }
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 魅族系统状态栏文字颜色修改
     *
     * @param activity
     * @param dark
     * @return
     */
    private static boolean setFlymeLightStatusBar(Activity activity, boolean dark) {
        boolean result = false;
        if (activity != null) {
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * 安卓原生修改状态栏文字颜色
     * @param activity
     * @param dark
     */
    private static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    public static void startSelectorAnim(View view) {
        if (null != view && view instanceof ImageView) {
            Drawable drawable = ((ImageView) view).getDrawable();
            if (null != drawable && drawable instanceof StateListDrawable) {
                AnimationDrawable anim = UIUtils.getAnimDrawable((StateListDrawable) drawable);
                if (null != anim) {
                    anim.start();
                }
            }
        }
    }

    /**
     * 从xml中的selector中获取选中时的drawable
     *
     * @param userDrawable
     * @return
     */
    public static AnimationDrawable getAnimDrawable(StateListDrawable userDrawable) {
        AnimationDrawable animationDrawable = null;
        try {
            Class slDraClass = StateListDrawable.class;
            Method getStateCountMethod = slDraClass.getDeclaredMethod("getStateCount");
            Method getStateSetMethod = slDraClass.getDeclaredMethod("getStateSet", int.class);
            Method getDrawableMethod = slDraClass.getDeclaredMethod("getStateDrawable", int.class);
            int count = (Integer) getStateCountMethod.invoke(userDrawable);
            Log.e("cys", "state count =" + count);
            out:
            for (int i = 0; i < count; i++) {
                int[] stateSet = (int[]) getStateSetMethod.invoke(userDrawable, i);
                if (stateSet == null || stateSet.length == 0) {
                    Log.e("cys", "state is null");
                } else {
                    for (int j = 0; j < stateSet.length; j++) {
                        Log.e("cys", "state =" + stateSet[j]);
                        if (stateSet[j] == android.R.attr.state_selected) {
                            Drawable drawable = (Drawable) getDrawableMethod.invoke(userDrawable, i);
                            Log.e("cys", "drawable =" + drawable);
                            if (null != drawable && drawable instanceof AnimationDrawable) {
                                animationDrawable = (AnimationDrawable) drawable;
                                break out;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return animationDrawable;
    }

    /**
     * 从上面开始向下显示提示信息
     *
     * @param view
     * @param anim 执行动画的时间
     * @param stay 中途停留显示的时间
     */
    public static void showToastDownFromUp(final View view, final long anim, final long stay) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
            final int value = view.getHeight();
            final ObjectAnimator down = ObjectAnimator.ofFloat(view, "translationY", 0, value);
            final ObjectAnimator up = ObjectAnimator.ofFloat(view, "translationY", value, 0);
            up.setDuration(anim).setInterpolator(new AccelerateInterpolator());
            down.setDuration(anim).setInterpolator(new AccelerateInterpolator());

            down.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator av) {
                    float alpha = av.getAnimatedFraction();
                    view.setAlpha(alpha);
                }
            });
            down.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    up.setStartDelay(stay);
                    up.start();
                }
            });
            up.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.INVISIBLE);
                }
            });
            up.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator av) {
                    float alpha = 1 - av.getAnimatedFraction();
                    view.setAlpha(alpha);
                }
            });
            down.start();
        }


    }

    /**
     * 从上面开始向下显示提示信息
     *
     * @param view
     * @param anim 执行动画的时间
     * @param stay 中途停留显示的时间
     */
    public static void showToastUpFromDown(final View view, final long anim, final long stay) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
            final int value = view.getHeight();
            final ObjectAnimator up = ObjectAnimator.ofFloat(view, "translationY", value, 0);
            final ObjectAnimator down = ObjectAnimator.ofFloat(view, "translationY", 0, value);
            up.setDuration(anim).setInterpolator(new AccelerateInterpolator());
            down.setDuration(anim).setInterpolator(new AccelerateInterpolator());

            down.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator av) {
                    float alpha = 1 - av.getAnimatedFraction();
                    view.setAlpha(alpha);
                }
            });
            up.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    down.setStartDelay(stay);
                    down.start();
                }
            });
            down.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.INVISIBLE);
                }
            });
            up.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator av) {
                    float alpha = av.getAnimatedFraction();
                    view.setAlpha(alpha);
                }
            });
            up.start();
        }


    }


    /**
     * 给EditText的右侧drawableRight属性的图片设置点击事件
     *
     * @param editText
     */
    public static void registerEditRightDrawableClickListener(final EditText editText) {
        editText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左上右下四张图片
                Drawable drawable = editText.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > editText.getWidth() - editText.getPaddingRight() - drawable.getIntrinsicWidth()) {
                    editText.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 保留多少位小数
     *
     * @param expectPriceEdt
     * @param remainCount    保留几位小数
     */
    public static void formatNumInput(EditText expectPriceEdt, final int remainCount) {
        expectPriceEdt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > remainCount) {
                    edt.delete(posDot + 3, posDot + 4);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
        });
    }

    public static void smoothScrollToPosition(RecyclerView rv, int position, int pinkPx) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
        MySmoothTopScroller smoothTopScroller = new MySmoothTopScroller(rv.getContext(), pinkPx);
        smoothTopScroller.setTargetPosition(position);
        layoutManager.startSmoothScroll(smoothTopScroller);
    }


}
