package blog.csdn.net.mchenys.module.demo.pindheader;

import android.content.Context;
import android.view.View;

import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.utils.LogUtils;
import blog.csdn.net.mchenys.common.widget.recycleview.BasePinnedHeaderAdapter;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewHolder;

/**
 * Created by mChenys on 2019/3/12.
 */

public class MyPinnedAdapter extends BasePinnedHeaderAdapter<String> {


    public MyPinnedAdapter(Context ctx, List data) {
        super(ctx, data, new int[]{R.layout.item_linear_title,
                R.layout.item_linear_content});
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 5 == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public boolean isPinnedPosition(int position) {
        return getItemViewType(position) == 0;
    }


    @Override
    protected void bindView(BaseRecycleViewHolder holder, int position, final String data) {
        if (getItemViewType(position) == 0) {
            holder.setTextView(R.id.text_adapter_title_name, data);
        }else{
            holder.setTextView(R.id.text_adapter_content_name, data);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("cys", data);
            }
        });
    }
}
