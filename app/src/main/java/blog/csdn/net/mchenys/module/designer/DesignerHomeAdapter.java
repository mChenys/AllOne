package blog.csdn.net.mchenys.module.designer;

import android.content.Context;
import android.view.View;

import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewAdapter;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewHolder;
import blog.csdn.net.mchenys.model.DesignerPojo;
import blog.csdn.net.mchenys.module.personal.PersonPageActivity;


/**
 * Created by mChenys on 2018/6/8.
 */

public class DesignerHomeAdapter extends BaseRecycleViewAdapter<DesignerPojo> {

    public DesignerHomeAdapter(Context mContext, List<DesignerPojo> mDatas) {
        super(mContext, mDatas, R.layout.item_layout_designer_index);
    }

    @Override
    protected void bindView(BaseRecycleViewHolder holder, int position, final DesignerPojo data) {
        holder.setHeaderImageView(R.id.iv_header, data.userImage)
                .setTextView(R.id.tv_nickname, data.userName)
                .setTextView(R.id.tv_city, data.cityName)
                .setTextView(R.id.tv_price, data.price);

        if (data.type == 1) {
            holder.show(R.id.iv_designer_tag);
        } else {
            holder.hide(R.id.iv_designer_tag);
        }
        if (null != data.image) {
            if (data.image.size() >= 2) {
                holder.displayWithRound(R.id.iv_housing_case_left, data.image.get(0), 4)
                        .displayWithRound(R.id.iv_housing_case_right, data.image.get(1), 4);

            } else {
                holder.displayWithRound(R.id.iv_housing_case_left, data.image.get(0), 4);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonPageActivity.start(mContext, data.userId);
            }
        });
    }
}
