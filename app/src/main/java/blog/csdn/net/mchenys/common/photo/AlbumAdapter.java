package blog.csdn.net.mchenys.common.photo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;


/**
 * 相册适配器
 * Created by mChenys on 2016/11/3.
 */
public class AlbumAdapter extends BaseAdapter {
    private List<PhotoAlbum> mPhotoAlbumList;
    private Context mContext;

    public AlbumAdapter(Context ctx, List<PhotoAlbum> list) {
        this.mContext = ctx;
        this.mPhotoAlbumList = list;
    }

    @Override
    public int getCount() {
        return null == mPhotoAlbumList ? 0 : mPhotoAlbumList.size();
    }

    @Override
    public PhotoAlbum getItem(int position) {
        return null == mPhotoAlbumList ? null : mPhotoAlbumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_album_list, null);
            viewHolder.iconImage = (ImageView) convertView.findViewById(R.id.headicon);
            viewHolder.enterImage = (ImageView) convertView.findViewById(R.id.enterIcon);
            viewHolder.titleText = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //显示相册
        PhotoAlbum album = getItem(position);
        PhotoAlbum.Photo firstItem = album.photoList.get(0);
        //第一张相片作为封面图
        ImageLoadUtils.disPlay(firstItem.imagePath,viewHolder.iconImage);
        viewHolder.titleText.setText(album.albumName + "  (" + album.photoCount + ")");
        return convertView;
    }

    private final class ViewHolder {
        ImageView iconImage;
        ImageView enterImage;
        TextView titleText;
    }
}
