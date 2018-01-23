package com.moxtra.moxiechat.login.mvp;

import android.content.Context;

/**
 * @author Marvin Abisrror
 */

public interface LoginPresenter {

    void linkWithUniqueID(String uniqueId);
    void linkWithAccessToken(String token);
    void onDestroy();


}
