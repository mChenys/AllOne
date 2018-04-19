package blog.csdn.net.mchenys.module.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.utils.DisplayUtils;
import blog.csdn.net.mchenys.common.utils.JumpUtils;
import blog.csdn.net.mchenys.common.utils.StringUtils;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewAdapter;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewHolder;
import blog.csdn.net.mchenys.model.CircleTopics;
import blog.csdn.net.mchenys.module.terminal.WapTopicsTerminalActivity;

public class CircleTopicsAdapter extends BaseRecycleViewAdapter<CircleTopics> {
    private static final String TAG = "CircleTopicsAdapter";
    private int minHeight;
    private int maxHeight;
    private int itemWidth;

    public CircleTopicsAdapter(Context ctx, int space, List<CircleTopics> data) {
        this(ctx, data, new int[]{R.layout.item_home_topic_list}, space);
    }

    public CircleTopicsAdapter(Context ctx, List<CircleTopics> data, int[] layoutIds, int space) {
        super(ctx, data, layoutIds);
        int screenWidth = ctx.getResources().getDisplayMetrics().widthPixels;
        this.itemWidth = (int) (0.5 * (screenWidth - 4 * DisplayUtils.convertDIP2PX(ctx, space)));
        this.minHeight = (int) (itemWidth * 3 / 5.0f);
        this.maxHeight = (int) (itemWidth * 5 / 3.0f);
    }


    @Override
    protected void bindView(BaseRecycleViewHolder holder, final int position, final CircleTopics data) {
        if ("wap".equals(data.type))
            holder.setTextView(R.id.tv_name, StringUtils.maxEms(data.user.nickName, 5))
                    .setTextView(R.id.tv_like, StringUtils.formatNum(data.likes))
                    .setTextView(R.id.tv_title, (data.title))
                    .setTextView(R.id.tv_desc, data.desc);
        int imageW = data.image.width;
        int imageH = data.image.height;
        int itemHeight = itemWidth;//默认是1:1
        if (imageW != 0 && imageH != 0) {
            itemHeight = (int) (this.itemWidth * (imageH / (float) imageW));
        }
        if (itemHeight < minHeight) {
            itemHeight = minHeight;
        } else if (itemHeight > maxHeight) {
            itemHeight = maxHeight;
        }
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.getLayoutParams(R.id.iv_big_image);
        lp.width = itemWidth;
        lp.height = itemHeight;
        Log.d(TAG, "data.image.imageUrl:" + data.image.imageUrl + " position:" + position);
        holder.setLayoutParams(R.id.iv_big_image, lp);
        holder.setImageUrl(R.id.iv_big_image, data.image.imageUrl, itemHeight, itemWidth)
                .setCircleImageUrl(R.id.iv_header, data.user.imageUrl);
        //头像小图标的显示和隐藏
        if (!StringUtils.isEmpty(data.user.techIconUrl)) {
            holder.showView(R.id.iv_header_tag)
                    .setCircleImageUrl(R.id.iv_header_tag, data.user.techIconUrl);
        } else {
            holder.hideView(R.id.iv_header_tag);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("wap".equals(data.type)) {
                    Bundle b = new Bundle();
                    b.putString("topicId", data.topicId);
                    JumpUtils.startActivity((Activity) mContext, WapTopicsTerminalActivity.class, b);
                }
            }
        });

    }

}