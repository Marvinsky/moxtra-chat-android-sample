package com.moxtra.moxiechat.interfaces;

import com.moxtra.moxiechat.model.Session;

import java.util.List;

/**
 * @author Marvin Abisrror
 */

public interface ChatListView extends LoadingView {
    void showChatList(List<Session> sessions);
}
