package com.moxtra.moxiechat.chatlist.mvp;

import com.moxtra.moxiechat.interfaces.ChatListView;
import com.moxtra.moxiechat.model.Session;
import com.moxtra.sdk.chat.model.Chat;
import com.moxtra.sdk.common.ApiCallback;
import com.moxtra.sdk.meet.model.Meet;

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
    public void loadChatMeetList() {
        if (chatListView != null) {
            chatListView.showLoading();
        }
        //chatListInterator.getChatList();
        chatListInterator.getMeetList();
    }

    @Override
    public void onChatListLoaded(List<Chat> chats) {
        //chatListView.updateChats(chats);
    }

    @Override
    public void onMeetListLoaded(List<Meet> meets) {
        chatListView.updateMeets(meets);
    }

    @Override
    public void fetchMeetsError(String errorCode, String errorMsg) {
        chatListView.fetchMeetsError(errorCode, errorMsg);
    }

    @Override
    public void fetchMeets(ApiCallback<List<Meet>> listApiCallback) {
        chatListView.fetchMeets(listApiCallback);
    }
}
