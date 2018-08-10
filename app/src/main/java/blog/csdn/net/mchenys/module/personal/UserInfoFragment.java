package blog.csdn.net.mchenys.module.personal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.utils.StringUtils;
import blog.csdn.net.mchenys.model.UserInfo;


/**
 * 个人资料
 * Created by mChenys on 2018/8/9.
 */

public class UserInfoFragment extends Fragment {
    private TextView mUserNameTv,nameTv;
    private TextView mSexTv,sexTv;
    private TextView mPriceTv,priceTv;
    private TextView mCityTv,cityTv;
    private TextView mPositionTv,positionTv;
    private TextView mDescTv,descTv;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frament_user_info, null);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        mUserNameTv = (TextView) rootView.findViewById(R.id.tv_username);
        nameTv = (TextView) rootView.findViewById(R.id.name);

        mSexTv = (TextView) rootView.findViewById(R.id.tv_sex);
        sexTv = (TextView) rootView.findViewById(R.id.sex);

        mPriceTv = (TextView) rootView.findViewById(R.id.tv_price);
        priceTv = (TextView) rootView.findViewById(R.id.price);

        mCityTv = (TextView) rootView.findViewById(R.id.tv_city);
        cityTv = (TextView) rootView.findViewById(R.id.city);

        mPositionTv = (TextView) rootView.findViewById(R.id.tv_position);
        positionTv = (TextView) rootView.findViewById(R.id.position);

        mDescTv = (TextView) rootView.findViewById(R.id.tv_desc);
        descTv = (TextView) rootView.findViewById(R.id.desc);
    }

    public void setData(UserInfo userInfo){
        setUserName(userInfo.nickName);
        setSex(userInfo.sex==1?"男":"女");
        setPrice(userInfo.price);
        setCity(userInfo.city);
        setPosition(userInfo.position);
        setDesc(userInfo.desc);


    }

    private void setUserName(String userName) {
        mUserNameTv.setText(userName);
        if (StringUtils.isEmpty(userName)) {
            mUserNameTv.setVisibility(View.GONE);
            nameTv.setVisibility(View.GONE);
        }else{
            mUserNameTv.setVisibility(View.VISIBLE);
            nameTv.setVisibility(View.VISIBLE);
        }
    }

    private void setSex(String sex) {
        mSexTv.setText(sex);
        if (StringUtils.isEmpty(sex)) {
            mSexTv.setVisibility(View.GONE);
            sexTv.setVisibility(View.GONE);
        }else{
            mSexTv.setVisibility(View.VISIBLE);
            sexTv.setVisibility(View.VISIBLE);
        }
    }

    private void setPrice(String price) {
        mPriceTv.setText(price);
        if (StringUtils.isEmpty(price)) {
            mPriceTv.setVisibility(View.GONE);
            priceTv.setVisibility(View.GONE);
        }else{
            mPriceTv.setVisibility(View.VISIBLE);
            priceTv.setVisibility(View.VISIBLE);
        }
    }

    private void setCity(String city) {
        mCityTv.setText(city);
        if (StringUtils.isEmpty(city)) {
            mCityTv.setVisibility(View.GONE);
            cityTv.setVisibility(View.GONE);
        }else{
            mCityTv.setVisibility(View.VISIBLE);
            cityTv.setVisibility(View.VISIBLE);
        }
    }
    private void setPosition(String city) {
        mPositionTv.setText(city);
        if (StringUtils.isEmpty(city)) {
            mPositionTv.setVisibility(View.GONE);
           positionTv.setVisibility(View.GONE);
        }else{
            mPositionTv.setVisibility(View.VISIBLE);
            positionTv.setVisibility(View.VISIBLE);
        }
    }
    private void setDesc(String desc) {
        mDescTv.setText(desc);
        if (StringUtils.isEmpty(desc)) {
            mDescTv.setVisibility(View.GONE);
            descTv.setVisibility(View.GONE);
        }else{
            mDescTv.setVisibility(View.VISIBLE);
            descTv.setVisibility(View.VISIBLE);
        }
    }
}
