package com.moxtra.moxiechat.interfaces;

/**
 * @author Marvin Abisrror
 */

public interface LoginView extends LoadingView {

    void startChatListActivity();
    void gcmRegistrationService();
    void loginError(String errorCode, String errorMsg);
    void persistUniqueId(String uniqueId);

}
