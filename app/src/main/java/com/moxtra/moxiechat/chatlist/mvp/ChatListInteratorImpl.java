package com.moxtra.moxiechat.chatlist.mvp;

/**
 * @author Marvin Abisrror
 */

public class ChatListInteratorImpl implements ChatListInterator {

    ChatListRepository chatListRepository;


    public ChatListInteratorImpl(ChatListTaskListener listener) {
        this.chatListRepository = new ChatListRepositoryImpl(listener);
    }

    @Override
    public void getChatList() {
        chatListRepository.getChatList();
    }
}
