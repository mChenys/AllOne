package blog.csdn.net.mchenys.common.widget.dialog;

import android.annotation.SuppressLint;
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
 * 擅长类别
 * Created by mChenys on 2018/8/13.
 */

public class DesignerSpaceDialog extends Dialog implements TagFlowLayout.OnSelectListener, View.OnClickListener {

    private TagFlowLayout mTagFlowLayout;
    private TagAdapter mAdapter;
    private List<Space> mData = new ArrayList<>();
    private TextView mOkTv;


    public interface OnSpaceSelectListener {
        void onSelected(Space space);
    }

    private OnSpaceSelectListener mOnSpaceSelectListener;

    public void setOnSpaceSelectListener(OnSpaceSelectListener onSpaceSelectListener) {
        mOnSpaceSelectListener = onSpaceSelectListener;
    }

    @SuppressLint("ResourceType")
    public DesignerSpaceDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_designer_space);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = Env.screenWidth;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.dialogBottomInOut);
        mOkTv = findViewById(R.id.tv_ok);
        mOkTv.setEnabled(false);
        mOkTv.setOnClickListener(this);
        mTagFlowLayout = findViewById(R.id.tag_layout);
        mTagFlowLayout.setAdapter(mAdapter = new MyTagAdapter(mData));
        mTagFlowLayout.setMaxSelectCount(1);
        mTagFlowLayout.setOnSelectListener(this);
    }

    @Override
    public void show() {
        if(!NetworkUtils.isNetworkAvailable(getContext())){
            ToastUtils.showShort(getContext().getApplicationContext(), "网络不给力啊");
            return;
        }
        if (mSelectIndex != -1) {
            mAdapter.setSelectedList(mSelectIndex);
        }

        HttpUtils.getJSON(false, Urls.DESIGNER_SPACE_LIST + "?catetory=2",
                null, null, new HttpUtils.JSONCallback() {
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
                                mData.add(new Space(array.optJSONObject(i)));
                            }
                            mAdapter.notifyDataChanged();
                            DesignerSpaceDialog.super.show();
                        }


                    }
                });

    }

    private int mSelectIndex = -1;

    @Override
    public void onSelected(Set<Integer> selectPosSet) {
        if (selectPosSet.size() > 0) {
            mOkTv.setEnabled(true);
            mSelectIndex = selectPosSet.iterator().next();
        } else {
            mOkTv.setEnabled(false);
            mSelectIndex=-1;
        }


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_ok) {
            if (null != mOnSpaceSelectListener && mSelectIndex > -1 &&
                    mSelectIndex < mData.size()) {

                Space space = mData.get(mSelectIndex);
                mOnSpaceSelectListener.onSelected(space);

                dismiss();
            }

        }
    }

    public class Space {
        public String id;
        public String name;

        private Space(JSONObject object) {
            if (null != object) {
                this.id = object.optString("id");
                this.name = object.optString("name");
            }
        }
    }

    private class MyTagAdapter extends TagAdapter<Space> {

        private MyTagAdapter(List<Space> datas) {
            super(datas);
        }

        @Override
        public View getView(FlowLayout parent, int position, Space space) {
            TextView textView = (TextView) View.inflate(getContext(), R.layout.item_designer_tag_view, null);
            textView.setText(space.name);
            return textView;
        }
    }
}
