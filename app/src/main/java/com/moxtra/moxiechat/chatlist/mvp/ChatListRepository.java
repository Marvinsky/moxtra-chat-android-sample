package com.moxtra.moxiechat.chatlist.mvp;

import com.moxtra.sdk.common.ApiCallback;
import com.moxtra.sdk.meet.model.Meet;

import java.util.List;

/**
 * @author Marvin Abisrror
 */

public interface ChatListRepository {

    void getChatList();
    void getMeetList();

}
