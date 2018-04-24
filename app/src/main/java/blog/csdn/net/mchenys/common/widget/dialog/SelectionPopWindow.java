package blog.csdn.net.mchenys.common.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import blog.csdn.net.mchenys.R;

/**
 * 列表选项对话框
 * 可自定义布局,主视图必须存在2个id
 * R.id.lv_data
 * R.id.v_cancel
 * Created by mChenys on 2018/4/24.
 */

public class SelectionPopWindow extends PopupWindow {
    private Activity mContext;
    private ListView mListView;
    private View mCancelView;
    private List<String> mData = new ArrayList<>();
    private ArrayAdapter mAdapter;
    private CallBack mCallBack;

    public interface CallBack {
        void onSelect(int position, String content);
    }

    /**
     * 创建自定义popWin
     *
     * @param context
     * @param layoutId        window的contentView 的布局文件
     * @param optionId        每个选项的布局文件
     * @param canTouchDismiss 是否点击外部隐藏
     */
    public SelectionPopWindow(Context context, int layoutId, int optionId, boolean canTouchDismiss) {
        super(context);
        this.mContext = (Activity) context;
        if (layoutId == 0) {
            layoutId = R.layout.layout_popwin_listview;
        }
        if (optionId == 0) {
            optionId = android.R.layout.simple_list_item_1;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        onCreateWindow(view, canTouchDismiss);
        initView(view, optionId);
    }

    public SelectionPopWindow(Context context) {
        this(context, 0);
    }

    public SelectionPopWindow(Context context, int optionId) {
        this(context, 0, optionId, true);
    }

    private void onCreateWindow(View view, boolean canTouchDismiss) {
        setContentView(view);
        setTouchable(true);
        setOutsideTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.style.FROM_BOTTOM_IN);
        setParentViewAlpha(0.5f);
        if (canTouchDismiss) {
            setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), (Bitmap) null));
        }
        setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setParentViewAlpha(1.0f);
                dismiss();
            }
        });
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initView(View view, int optionId) {
        mListView = view.findViewById(R.id.lv_data);
        mCancelView = view.findViewById(R.id.v_cancel);
        mListView.setAdapter(mAdapter = new ArrayAdapter<String>(mContext, optionId, mData) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setGravity(Gravity.CENTER);
                return textView;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCallBack != null) {
                    String content = mData.get(position);
                    mCallBack.onSelect(position, content);
                }
                dismiss();
            }
        });
        mCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 设置父窗口的透明度
     *
     * @param alpha 透明度
     */
    private void setParentViewAlpha(float alpha) {
        WindowManager.LayoutParams params = mContext.getWindow().getAttributes();
        params.alpha = alpha;
        mContext.getWindow().setAttributes(params);
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            setParentViewAlpha(1.0f);
            super.dismiss();
        }
    }

    public SelectionPopWindow setData(String[] array) {
        List<String> datas = Arrays.asList(array);
        mData.clear();
        mData.addAll(datas);
        mAdapter.notifyDataSetChanged();
        return this;
    }

    public void show(CallBack callBack) {
        if (isShowing()) return;
        this.mCallBack = callBack;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showAtLocation(mContext.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            }
        }, 150);
    }
}
