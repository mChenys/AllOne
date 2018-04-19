package blog.csdn.net.mchenys.module.adapter;

import android.content.Context;

import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewAdapter;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewHolder;
import blog.csdn.net.mchenys.model.Recommend;


/**
 * Created by mChenys on 2018/1/2.
 */

public class RecommendAdapter extends BaseRecycleViewAdapter<Recommend> {

    public RecommendAdapter(Context ctx, List<Recommend> data, int layoutId) {
        super(ctx, data, layoutId);
    }

    @Override
    protected void bindView(BaseRecycleViewHolder holder, int position, Recommend data) {
        holder.setTextView(R.id.tv_name, data.techNickName)
                .setTextView(R.id.tv_techJobName, data.techJobName)
                .setTextView(R.id.tv_desc, data.techDesc)
                .setTextView(R.id.tv_techCourse, data.courseTotal + "")
                .setTextView(R.id.tv_fans, data.followTotal + "")
                .setCircleImageUrl(R.id.iv_header, data.techHeadUrl);


    }
}
