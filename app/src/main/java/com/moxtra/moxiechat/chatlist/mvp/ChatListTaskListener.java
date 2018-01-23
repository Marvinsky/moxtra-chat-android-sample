package com.moxtra.moxiechat.chatlist.mvp;

import com.moxtra.moxiechat.model.Session;
import com.moxtra.sdk.chat.model.Chat;
import com.moxtra.sdk.meet.model.Meet;

import java.util.List;

/**
 * @author Marvin Abisrror
 */

public interface ChatListTaskListener {
    void onChatListLoaded(List<Chat> chat);
    void onMeetListLoaded(List<Meet> meets);
    void fetchMeetsError(String errorCode, String errorMsg);
}
