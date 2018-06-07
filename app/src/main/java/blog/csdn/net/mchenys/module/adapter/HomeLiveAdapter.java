package blog.csdn.net.mchenys.module.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;
import blog.csdn.net.mchenys.common.utils.JumpUtils;
import blog.csdn.net.mchenys.common.utils.StringUtils;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewAdapter;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewHolder;
import blog.csdn.net.mchenys.model.SubColumnData;
import blog.csdn.net.mchenys.module.terminal.LiveColumnTerminalActivity;

/**
 * Created by mChenys on 2018/6/7.
 */

public class HomeLiveAdapter extends BaseRecycleViewAdapter<SubColumnData> {
    public HomeLiveAdapter(Context ctx, List<SubColumnData> data, int layoutId) {
        super(ctx, data, layoutId);
    }

    @Override
    protected void bindView(BaseRecycleViewHolder holder, int position, final SubColumnData data) {
        ImageView mIvCover = holder.getView(R.id.iv_cover);
        TextView mTvTitle = holder.getView(R.id.tv_title);
        ImageView mIvDesignerHeader = holder.getView(R.id.iv_header);
        TextView mTvDesignerNickname = holder.getView(R.id.tv_nickname);
        View mVTop = holder.getView(R.id.v_top);
        View mVDivider = holder.getView(R.id.v_divider);
        RelativeLayout mRlUserInfo = holder.getView(R.id.rl_uesr_info);
        if (data != null) {
            if (position == 0) {
                mVTop.setVisibility(View.VISIBLE);
            } else {
                mVTop.setVisibility(View.GONE);
            }

            if (position == mData.size() - 1) {
                mVDivider.setVisibility(View.GONE);
            } else {
                mVDivider.setVisibility(View.VISIBLE);
            }
            ImageLoadUtils.disPlay(data.imageUrl, mIvCover);
            mTvTitle.setText(data.title);
            if (!StringUtils.isEmpty(data.userImage) && !StringUtils.isEmpty(data.userName)) {
                mRlUserInfo.setVisibility(View.VISIBLE);
                ImageLoadUtils.disPlayWithCircle(data.userImage, mIvDesignerHeader, R.drawable.default_header_img);
                mTvDesignerNickname.setText(data.userName);
            } else {
                mRlUserInfo.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("columnId",data.id);
                    JumpUtils.startActivity((Activity) mContext, LiveColumnTerminalActivity.class,b);
                }
            });
        }
    }
}
