package blog.csdn.net.mchenys.module.demo.test;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.RefreshRecyclerView;

/**
 * Created by mChenys on 2018/12/13.
 */

public class TestNestScrollRefreshActivity extends AppCompatActivity {
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_nest_scroll_refresh);
        final RefreshRecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new MyAdapter(this));
        recyclerView.setLoadingListener(new RefreshRecyclerView.LoadingListener() {
            @Override
            public void onRefresh(Bundle args) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.stopRefresh(false);
                    }
                },500);

            }

            @Override
            public void onLoadMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.stopRefresh(true);
                    }
                },500);
            }
        });
    }
}
