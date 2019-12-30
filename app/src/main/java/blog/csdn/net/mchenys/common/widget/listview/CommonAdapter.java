package blog.csdn.net.mchenys.common.widget.listview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 通用的ListView Adapter,支持多布局类型
 * Created by chenys on 2015/5/27.
 * 对于有抢占焦点控件需要处理设置focusable属性;对于有CheckBox的情况需要处理View 被重用后CheckBox的勾选状态也重用的情况.
 * 对于View被复用造成的状态错乱的问题解决方式:
 * 1.通过在目标bean中加入标记,然后目标View状态发生变化时更新标记.类似:
 * final CheckBox cb = holder.getView(R.id.cb);
 * cb.setChecked(bean.isChecked());
 * cb.setOnClickListener(new OnClickListener(){
 *
 * @Override public void onClick(View v){
 * bean.setChecked(cb.isChecked());
 * }
 * });
 * 2.通过定义集合记录当目标View状态发生变化时的位置,类似:
 * List<Integer> mPos = new arrayList<>();//这句定义在成员位置,而非setData方法内
 * final CheckBox cb = holder.getView(R.id.cb);
 * cb.setChecked(false);
 * if(mPos.contains(holder.getPosition)){
 * cb.setChecked(true);
 * }
 * cb.setOnClickListener(new OnClickListener(){
 * @Override public void onClick(View v){
 * if(cb.isChecked()){
 * mPos.add(holder.getPosition());
 * }else{
 * mPost.remove((Integer)holder.getPosition());
 * }
 * }
 * });
 * 3.通过SparseBooleanArray来记录状态
 * SparseBooleanArray mCheckStates=new SparseBooleanArray();//这句定义在成员位置,而非setData方法内
 * CheckBox cb = holder.getView(R.id.cb);
 * cb.setTag(position);
 * cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
 * @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
 * int pos =(int)buttonView.getTag();
 * if(isChecked){
 * mCheckStates.put(pos,true);
 * //do something
 * }else{
 * mCheckStates.delete(pos);
 * //do something else
 * }
 * }
 * });
 * cb.setChecked(mCheckStates.get(position,false));
 * <p>
 * 以上3种方式都是写在子类实现的setData(ViewHolder holder, int position,List<T> mData)方法里面
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    private Context mContext;
    private int[] layoutIds;
    private List<T> mData;

    public CommonAdapter(Context context, int layoutId, List<T> mData) {
        this(context, new int[]{layoutId}, mData);
    }

    public CommonAdapter(Context context, int[] layoutIds, List<T> mData) {
        this.mContext = context;
        this.layoutIds = layoutIds;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       /* ListViewHolder[] holders = new ListViewHolder[getViewTypeCount()];
        int i = getItemViewType(position);
        holders[i] = ListViewHolder.getViewHolder(mContext, convertView, layoutIds[i], position);
        setData(holders[i], position, getItem(position));
        return holders[i].getConvertView();*/

        int i = getItemViewType(position);
        ListViewHolder viewHolder = ListViewHolder.getViewHolder(mContext, convertView, layoutIds[i], position);
        setData(viewHolder, position, getItem(position));
        return viewHolder.getConvertView();
    }

    @Override
    public int getViewTypeCount() {
        return layoutIds.length;
    }

    @Override
    public int getItemViewType(int position) {
        T t;
        if (null != mData && null != (t = mData.get(position))) {
            return getItemViewType(t);
        }
        return super.getItemViewType(position);
    }

    protected int getItemViewType(T t) {
        return 0;
    }

    public abstract void setData(ListViewHolder holder, int position, T data);
}
