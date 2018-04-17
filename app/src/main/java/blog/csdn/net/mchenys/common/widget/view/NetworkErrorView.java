package blog.csdn.net.mchenys.common.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 异常页面
 * Created by mChenys on 2017/12/27.
 */

public class NetworkErrorView extends RelativeLayout {


    public NetworkErrorView(Context context) {
        this(context, null);
    }

    public NetworkErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextView infoTv = new TextView(context);
        infoTv.setText("出现异常了");
        infoTv.setGravity(Gravity.CENTER);
        addView(infoTv);
    }
}
