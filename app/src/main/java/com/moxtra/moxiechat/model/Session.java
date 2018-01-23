package com.moxtra.moxiechat.model;

import com.moxtra.sdk.chat.model.Chat;
import com.moxtra.sdk.meet.model.Meet;

/**
 * @author Marvin Abisrror
 */

public class Session {
    public final boolean isMeet;
    public com.moxtra.sdk.chat.model.Chat chat;
    public Meet meet;

    public Session(com.moxtra.sdk.chat.model.Chat chat) {
        this.chat = chat;
        this.isMeet = false;
    }

    public Session(Meet meet) {
        this.meet = meet;
        this.isMeet = true;
    }

    public boolean isMeet() {
        return isMeet;
    }

    public com.moxtra.sdk.chat.model.Chat getChat() {
        return chat;
    }

    public void setChat(com.moxtra.sdk.chat.model.Chat chat) {
        this.chat = chat;
    }

    public Meet getMeet() {
        return meet;
    }

    public void setMeet(Meet meet) {
        this.meet = meet;
    }
}
