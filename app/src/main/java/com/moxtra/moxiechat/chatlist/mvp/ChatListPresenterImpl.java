package com.moxtra.moxiechat.chatlist.mvp;

import com.moxtra.moxiechat.interfaces.ChatListView;
import com.moxtra.moxiechat.model.Session;

import java.util.List;

/**
 * @author Marvin Abisrror
 */

public class ChatListPresenterImpl implements ChatListPresenter, ChatListTaskListener {

    ChatListView chatListView;
    ChatListInterator chatListInterator;

    public ChatListPresenterImpl(ChatListView view) {
        this.chatListView = view;
        chatListInterator = new ChatListInteratorImpl(this);
    }

    @Override
    public void loadChatList() {
        if (chatListView != null) {
            chatListView.showLoading();
        }
        chatListInterator.getChatList();
    }

    @Override
    public void onChatListLoaded(List<Session> sessions) {
        chatListView.showChatList(sessions);
    }
}
