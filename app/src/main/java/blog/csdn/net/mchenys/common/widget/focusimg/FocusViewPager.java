package blog.csdn.net.mchenys.common.widget.focusimg;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

/**
 * Created by mChenys on 2016/6/2.
 * 自定义ViewPager解决与Fragment间滑动时的冲突问题.
 */
public class FocusViewPager extends ViewPager {
    private boolean noScroll;

    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }

    public FocusViewPager(Context context) {
        super(context);
    }

    public FocusViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                // 计算在X和Y方向的偏移量
                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                ViewParent parent = getParent();
                if (parent != null) {
                    // 横向滑动小于纵向滑动时不截断事件,由父类处理滑动事件
                    if (xDistance < yDistance) {
                        parent.requestDisallowInterceptTouchEvent(false);
                    } else {
                        //横向滑动大于纵向滑动时 请求父控件不拦截事件,由当前的ViewPager来处理事件
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (noScroll) {
            return false;
        } else {
            return super.onTouchEvent(e);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (noScroll) {
            return false;
        } else {
            return super.onInterceptTouchEvent(e);
        }
    }
}
