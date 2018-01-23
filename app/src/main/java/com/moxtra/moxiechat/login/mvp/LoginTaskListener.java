package com.moxtra.moxiechat.login.mvp;

/**
 * @author Marvin Abisrror
 */

public interface LoginTaskListener {

    void loginSuccess();
    void loginError(String errorCode, String errorMsg);
    void persistUniqueId(String uniqueId);


}
