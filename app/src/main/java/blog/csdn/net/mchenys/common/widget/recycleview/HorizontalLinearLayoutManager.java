package blog.csdn.net.mchenys.common.widget.recycleview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class HorizontalLinearLayoutManager extends LinearLayoutManager {

    public HorizontalLinearLayoutManager(Context context) {
        super(context,HORIZONTAL,false);
    }

    public HorizontalLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }
}