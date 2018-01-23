package com.moxtra.moxiechat.chatlist.mvp;

import com.moxtra.moxiechat.model.Session;

import java.util.List;

/**
 * @author Marvin Abisrror
 */

public interface ChatListTaskListener {
    void onChatListLoaded(List<Session> sessions);
}
