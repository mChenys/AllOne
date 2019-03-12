package blog.csdn.net.mchenys.module.demo.pindheader;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.widget.recycleview.PinnedHeaderItemDecoration;

/**
 * 分组固定头部列表
 * Created by mChenys on 2019/3/12.
 */

public class TestPinnedHeaderActivity extends Activity {
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_pind_header);
        mRecyclerView = findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new MyPinnedAdapter(this, obtainData()));
        mRecyclerView.addItemDecoration(new PinnedHeaderItemDecoration());
    }

    private List<String> obtainData() {
        List<String> list = new ArrayList<>();
        list.add("2016-07-20");
        list.add("萍乡");
        list.add("高安");
        list.add("江西");
        list.add("南昌");
        list.add("2016-07-21");
        list.add("江西");
        list.add("南昌");
        list.add("江西");
        list.add("南昌");
        list.add("2016-07-22");
        list.add("中国");
        list.add("北京");
        list.add("江西");
        list.add("南昌");
        list.add("2016-07-23");
        list.add("辽宁");
        list.add("沈阳");
        list.add("江西");
        list.add("南昌");
        list.add("2016-07-24");
        list.add("辽宁");
        list.add("沈阳");
        list.add("江西");
        list.add("南昌");
        list.add("2016-07-25");
        list.add("辽宁");
        list.add("沈阳");
        list.add("江西");
        list.add("南昌");
        list.add("2016-07-26");
        list.add("辽宁");
        list.add("沈阳");
        list.add("江西");
        list.add("南昌");
        list.add("2016-07-27");
        list.add("辽宁");
        list.add("沈阳");
        list.add("江西");
        list.add("南昌");
        list.add("2016-07-28");
        list.add("辽宁");
        list.add("沈阳");
        list.add("江西");
        list.add("南昌");
        list.add("2016-07-29");
        list.add("辽宁");
        list.add("沈阳");
        list.add("江西");
        list.add("南昌");
        list.add("2016-07-30");
        list.add("辽宁");
        list.add("沈阳");
        list.add("江西");
        list.add("南昌");
        list.add("2016-07-21");
        list.add("辽宁");
        list.add("沈阳");
        list.add("江西");
        list.add("南昌");
        return list;
    }
}
