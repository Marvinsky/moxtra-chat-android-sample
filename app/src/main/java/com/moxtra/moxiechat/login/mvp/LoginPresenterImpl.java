package com.moxtra.moxiechat.login.mvp;

import com.moxtra.moxiechat.interfaces.LoginView;

/**
 * @author Marvin Abisrror
 */

public class LoginPresenterImpl implements LoginPresenter, LoginTaskListener {

    LoginView loginView;
    LoginInterator loginInterator;

    public LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
        loginInterator = new LoginInteratorImpl(this);
    }

    @Override
    public void linkWithUniqueID(String uniqueId) {
        if (loginView != null) {
            loginView.showLoading();
        }
        loginInterator.doLinkWithUniqueId(uniqueId);
    }

    @Override
    public void linkWithAccessToken(String token) {
        if (loginView != null) {
            loginView.showLoading();
        }
        loginInterator.doLinkWithToken(token);
    }

    @Override
    public void onDestroy() {
        loginView = null;
    }

    @Override
    public void loginSuccess() {
        loginView.gcmRegistrationService();
        loginView.startChatListActivity();
        loginView.hideLoading();
    }

    @Override
    public void loginError(String errorCode, String errorMsg) {
        loginView.loginError(errorCode, errorMsg);
        loginView.hideLoading();
    }

    @Override
    public void persistUniqueId(String uniqueId) {
        loginView.persistUniqueId(uniqueId);
    }
}
