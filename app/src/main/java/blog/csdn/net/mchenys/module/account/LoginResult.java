package blog.csdn.net.mchenys.module.account;

import blog.csdn.net.mchenys.model.Account;

public abstract class LoginResult {

    public void onSuccess(Account account) {
    }


    public void onFailure(int errorCode, String errorMessage) {
    }
}