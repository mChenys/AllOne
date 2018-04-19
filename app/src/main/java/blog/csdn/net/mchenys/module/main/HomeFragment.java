package blog.csdn.net.mchenys.module.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseFragment;
import blog.csdn.net.mchenys.common.config.Constant;


/**
 * 发现页
 * Created by mChenys on 2017/12/28.
 */

public class HomeFragment extends BaseFragment {
    private String title;
    private int position;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.title = getArguments().getString(Constant.KEY_TITLE);
        this.position = getArguments().getInt(Constant.KEY_POSITION);
    }


    @Override
    protected void initView() {
        TextView infoTv = findViewById(R.id.tv_info);
        infoTv.setText(position + ":" + title);
    }

    @Override
    protected Integer getLayoutResID() {
        return R.layout.fragment_home;
    }
}
