package com.moxtra.moxiechat.login.mvp;

import android.content.Context;

/**
 * @author Marvin Abisrror
 */

public class LoginInteratorImpl implements LoginInterator {

    LoginRepository loginRepository;

    public LoginInteratorImpl(LoginTaskListener listener) {
        this.loginRepository = new LoginRepositoryImpl(listener);
    }

    @Override
    public void doLinkWithUniqueId(String uniqueId) {
        loginRepository.LinkWithUniqueId(uniqueId);
    }

    @Override
    public void doLinkWithToken(String token) {

    }
}
