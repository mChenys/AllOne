package blog.csdn.net.mchenys.module.demo.test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import blog.csdn.net.mchenys.R;

//设配器
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<String> mData = new ArrayList<>();
    private Context mContext;

    public MyAdapter(Context context) {
        for (int i = 0; i < 20; i++) {
            mData.add("数据:" + i);
        }
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_text_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.infoTv.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView infoTv;

        public ViewHolder(View itemView) {
            super(itemView);
            infoTv = itemView.findViewById(R.id.tv_info);
        }
    }
}