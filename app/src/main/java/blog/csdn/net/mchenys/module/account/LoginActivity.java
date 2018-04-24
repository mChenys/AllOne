package blog.csdn.net.mchenys.module.account;

import android.view.View;
import android.widget.EditText;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.utils.AccountUtils;
import blog.csdn.net.mchenys.common.utils.StringUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;
import blog.csdn.net.mchenys.common.widget.view.LoadingView;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import blog.csdn.net.mchenys.model.Account;

/**
 * Created by mChenys on 2018/4/18.
 */

public class LoginActivity extends BaseActivity {
    private EditText mPhoneEdt, mPasswordEdt;
    private LoadingView mLoadingView;
    private String password, phone;

    private LoginResult mLoginResult = new LoginResult() {
        @Override
        public void onSuccess(Account account) {
            mLoadingView.hide();
            ToastUtils.showShort(mContext, "登录成功");
            setResult(RESULT_OK, getIntent());
            finish();
        }

        @Override
        public void onFailure(int errorCode, String errorMessage) {
            mLoadingView.hide();
            ToastUtils.showShort(mContext, errorMessage);
        }
    };

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_login;
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setCenterTv("登录");
    }

    @Override
    protected void initView() {
        super.initView();
        mPhoneEdt = findViewById(R.id.edt_phone);
        mPasswordEdt = findViewById(R.id.edt_pwd);
        mLoadingView = findViewById(R.id.loadView);
    }

    public void login(View view) {
        password = mPasswordEdt.getText().toString().trim();
        phone = mPhoneEdt.getText().toString().trim();
        if (checkValue()) {
            mLoadingView.show("请稍后...");
            AccountUtils.login(phone, password, mLoginResult);
        }
    }

    private boolean checkValue() {
        if (StringUtils.isEmpty(phone)) {
            ToastUtils.showShort(mContext, "手机号码不能为空");
            return false;
        } else if (StringUtils.isEmpty(password)) {
            ToastUtils.showShort(mContext, "密码不能为空");
            return false;
        }
        return true;
    }

}
