package com.moxtra.moxiechat.login.mvp;

import android.content.Context;

/**
 * @author Marvin Abisrror
 */

public interface LoginInterator {
    void doLinkWithUniqueId(String uniqueId);
    void doLinkWithToken(String token);
}
