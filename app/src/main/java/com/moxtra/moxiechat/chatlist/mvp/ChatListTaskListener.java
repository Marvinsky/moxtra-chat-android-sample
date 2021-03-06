package com.moxtra.moxiechat.chatlist.mvp;

import com.moxtra.moxiechat.model.Session;
import com.moxtra.sdk.chat.model.Chat;
import com.moxtra.sdk.common.ApiCallback;
import com.moxtra.sdk.meet.model.Meet;

import java.util.List;

/**
 * @author Marvin Abisrror
 */

public interface ChatListTaskListener {
    void onMeetListLoaded(List<Meet> meets);
    void fetchMeetsError(String errorCode, String errorMsg);
    void fetchMeets(ApiCallback<List<Meet>> listApiCallback);
}
