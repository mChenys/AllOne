package blog.csdn.net.mchenys.common.widget.layout;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by mChenys on 2018/8/9.
 */

public class MyNestedScrollView extends NestedScrollView {
    public MyNestedScrollView(Context context) {
        super(context);
    }

    public MyNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private int lastX, lastY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = (int) ev.getRawX();
            lastY = (int) ev.getRawY();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            int dx = (int) (ev.getRawX() - lastX);
            int dy = (int) (ev.getRawY() - lastY);
            lastX = (int) ev.getRawX();
            lastY = (int) ev.getRawY();
            //避免ViewPager拦截了斜方向的滑动事件
            boolean isVerticalScroll = Math.abs(dy) > Math.abs(dx);
            if (isVerticalScroll && !canScrollVertically(-1)) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}
