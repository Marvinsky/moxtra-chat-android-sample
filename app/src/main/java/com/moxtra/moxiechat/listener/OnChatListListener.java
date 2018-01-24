package com.moxtra.moxiechat.listener;

import com.moxtra.sdk.chat.model.Chat;
import com.moxtra.sdk.common.model.User;
import com.moxtra.sdk.meet.model.Meet;

import java.util.ArrayList;

/**
 * @author Marvin Abisrror
 */

public interface OnChatListListener {

    void showChat(Chat chat);
    void leaveOrDeleteChat(Chat chat);
    void joinMeet(Meet meet);
    void startMeet(String topic, ArrayList<User> userList);
}
