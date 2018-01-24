package com.moxtra.moxiechat.chatlist.mvp;

import android.util.Log;

import com.moxtra.sdk.common.ApiCallback;
import com.moxtra.sdk.meet.model.Meet;

import java.util.List;

/**
 * @author Marvin Abisrror
 */

public class ChatListRepositoryImpl implements ChatListRepository {
    ChatListTaskListener chatListTaskListener;

    private ApiCallback<List<Meet>> mMeetListApiCallback = null;

    public ChatListRepositoryImpl(ChatListTaskListener listener) {
        this.chatListTaskListener = listener;
    }

    @Override
    public void getMeetList() {
        mMeetListApiCallback = new ApiCallback<List<Meet>>() {
            @Override
            public void onCompleted(List<Meet> meets) {
                chatListTaskListener.onMeetListLoaded(meets);
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                chatListTaskListener.fetchMeetsError(String.valueOf(errorCode), errorMsg);
            }
        };
        chatListTaskListener.fetchMeets(mMeetListApiCallback);
    }
}
