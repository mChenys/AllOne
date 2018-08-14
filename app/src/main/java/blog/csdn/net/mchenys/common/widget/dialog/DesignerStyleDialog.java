package blog.csdn.net.mchenys.common.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.config.Env;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;
import blog.csdn.net.mchenys.common.utils.HttpUtils;
import blog.csdn.net.mchenys.common.utils.NetworkUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;
import blog.csdn.net.mchenys.common.widget.flowlayout.FlowLayout;
import blog.csdn.net.mchenys.common.widget.flowlayout.TagAdapter;
import blog.csdn.net.mchenys.common.widget.flowlayout.TagFlowLayout;

/**
 * 擅长风格
 * Created by mChenys on 2018/8/14.
 */

public class DesignerStyleDialog extends Dialog implements View.OnClickListener, TagFlowLayout.OnSelectListener {
    private TagFlowLayout mTagFlowLayout;
    private TextView mOkTv;
    private TagAdapter mAdapter;
    private List<Style> mData = new ArrayList<>();


    public interface OnStyleSelectListener {
        void onSelected(List<Style> styleList);
    }

    private OnStyleSelectListener mOnStyleSelectListener;

    public void setOnStyleSelectListener(OnStyleSelectListener onStyleSelectListener) {
        mOnStyleSelectListener = onStyleSelectListener;
    }

    public DesignerStyleDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_designer_style);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = Env.screenWidth;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.dialogBottomInOut);

        mOkTv = findViewById(R.id.tv_ok);
        mOkTv.setEnabled(false);
        mOkTv.setOnClickListener(this);
        mTagFlowLayout = findViewById(R.id.tag_layout);
        mTagFlowLayout.setAdapter(mAdapter = new MyTagAdapter(mData));
        mTagFlowLayout.setOnSelectListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.tv_ok) {
            if (null != mOnStyleSelectListener && null != selectPosSet) {
                List<Style> result = new ArrayList<>();
                for (Integer i : selectPosSet) {
                    if (i < mData.size()) {
                        result.add(mData.get(i));
                    }
                }
                if (result.size() > 0) {
                    mOnStyleSelectListener.onSelected(result);
                }
                dismiss();
            }

        }
    }

    @Override
    public void show() {
        if(!NetworkUtils.isNetworkAvailable(getContext())){
            ToastUtils.showShort(getContext().getApplicationContext(), "网络不给力啊");
            return;
        }
        if (null != selectPosSet) {
            mAdapter.setSelectedList(selectPosSet);
        }
        HttpUtils.getJSON(false, Urls.DESIGNER_STYLE_LIST+"?catetory=2", null, null, new HttpUtils.JSONCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(getContext().getApplicationContext(), "网络不给力啊");
            }

            @Override
            public void onSuccess(JSONObject jsonObject, OkResponse okResponse) {
                int status = jsonObject.optInt("status");
                if (status == 0) {
                    String msg = jsonObject.optString("msg");
                    ToastUtils.showShort(getContext().getApplicationContext(), msg);
                    return;
                }
                JSONArray array = jsonObject.optJSONArray("data");
                if (array != null && array.length() > 0) {
                    mData.clear();
                    for (int i = 0; i < array.length(); i++) {
                        mData.add(new Style(array.optJSONObject(i)));
                    }
                    mAdapter.notifyDataChanged();
                    DesignerStyleDialog.super.show();
                }
            }
        });
    }

    private Set<Integer> selectPosSet;

    @Override
    public void onSelected(Set<Integer> selectPosSet) {
        if (selectPosSet.size() > 0) {
            mOkTv.setEnabled(true);
            this.selectPosSet = selectPosSet;
        } else {
            mOkTv.setEnabled(false);
            this.selectPosSet = null;
        }
    }

    public class Style {
        public String id;
        public String name;

        private Style(JSONObject object) {
            if (null != object) {
                this.id = object.optString("id");
                this.name = object.optString("name");
            }
        }
    }

    private class MyTagAdapter extends TagAdapter<Style> {

        private MyTagAdapter(List<Style> datas) {
            super(datas);
        }

        @Override
        public View getView(FlowLayout parent, int position, Style style) {
            TextView textView = (TextView) View.inflate(getContext(), R.layout.item_designer_tag_view, null);
            textView.setText(style.name);
            return textView;
        }
    }
}
