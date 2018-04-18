package blog.csdn.net.mchenys.common.photo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.utils.DisplayUtils;
import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;
import blog.csdn.net.mchenys.common.utils.LogUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;


/**
 * Created by dell on 2016/11/3.
 */
public class PhotoAdapter extends BaseAdapter {
    private static final String TAG = "PhotoAdapter";
    private List<String> mSelectedPathList;//已选中的相片
    private List<PhotoAlbum.Photo> mPhotoList;//所有相片
    private Context mContext;
    private int mCurrCount;
    private int mMaxCount;//最大可选数

    public PhotoAdapter(Context ctx, List<PhotoAlbum.Photo> photoList, List<String> selectedPathList, int currCount, int maxCount) {
        this.mContext = ctx;
        this.mPhotoList = photoList;
        this.mMaxCount = maxCount;
        this.mCurrCount = currCount;
        this.mSelectedPathList = selectedPathList;
    }

    @Override
    public int getCount() {
        return null == mPhotoList ? 0 : mPhotoList.size();
    }

    @Override
    public String getItem(int position) {
        return null == mPhotoList ? null : mPhotoList.get(position).imagePath;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            //动态调整gridView的相册间的间距
            int margin10 = DisplayUtils.convertDIP2PX(mContext, 10);
            int parentWidth = DisplayUtils.getScreenWidth(mContext) - margin10;
            int hSpace = margin10 * 3;
            int width = (int) ((parentWidth - hSpace) / 3f); //(item的宽度等于屏幕宽度-间隙)/3
            int height = (int) (width * 3f / 4f); //item的高度是宽度的3/4
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_photo_list, null); //item布局文件
            holder.imageView = (ImageView) convertView.findViewById(R.id.photoImg);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            //设置item中imageView的宽高
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
            lp.width = width;
            lp.height = height;
            holder.imageView.setLayoutParams(lp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LogUtils.d(TAG,mSelectedPathList.toString());
        //更新checkBox的勾选和未勾选的状态
        if (mSelectedPathList.contains(getItem(position))) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        //在ImageView中显示相册图片
        ImageLoadUtils.disPlay(getItem(position),holder.imageView);
        //相册图片的点击事件,点击相片同样要更新checkBox的状态,同时会触发checkBox的点击事件
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(false);
                    //如果取消了选择则需要从集合中删除
                    mSelectedPathList.remove(getItem(position));
                } else {
                    if (mSelectedPathList.size() + mCurrCount == mMaxCount) {
                        ToastUtils.showShort(mContext, "最多只能上传" + mMaxCount + "张照片哦～");
                        return;
                    }
                    holder.checkBox.setChecked(true);
                    if (!mSelectedPathList.contains(getItem(position))) {
                        //如果选择的图片不在集合中,才允许添加,避免重复添加
                        mSelectedPathList.add(getItem(position));
                    }
                }
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        CheckBox checkBox;
    }
}