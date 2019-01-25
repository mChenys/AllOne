package blog.csdn.net.mchenys.common.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import blog.csdn.net.mchenys.R;


/**
 * 加载页面
 * Created by mChenys on 2017/12/27.
 */

public class LoadingView extends RelativeLayout {
    private TextView infoTv;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.layout_loading_view, this);
        infoTv = findViewById(R.id.tv_nickname);
        setClickable(true);
    }

    public void show(String text) {
        infoTv.setText(text);
        setVisibility(VISIBLE);

    }

    public void hide() {
        setVisibility(GONE);
    }


}
