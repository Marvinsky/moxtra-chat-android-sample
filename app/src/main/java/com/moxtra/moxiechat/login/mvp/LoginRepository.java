package com.moxtra.moxiechat.login.mvp;

import android.content.Context;

/**
 * @author Marvin Abisrror
 */

public interface LoginRepository {
    void LinkWithUniqueId(String uniqueId);
    void LinkWithToken(String token);
}
