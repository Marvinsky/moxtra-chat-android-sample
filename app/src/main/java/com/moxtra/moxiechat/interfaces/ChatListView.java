package com.moxtra.moxiechat.interfaces;

import com.moxtra.moxiechat.model.Session;
import com.moxtra.sdk.chat.model.Chat;
import com.moxtra.sdk.common.ApiCallback;
import com.moxtra.sdk.meet.model.Meet;

import java.util.List;

/**
 * @author Marvin Abisrror
 */

public interface ChatListView extends LoadingView {
    void updateChats(List<Chat> chats);
    void updateMeets(List<Meet> meets);
    void fetchMeetsError(String errorCode, String errorMsg);
    void fetchMeets(ApiCallback<List<Meet>> listApiCallback);
}
