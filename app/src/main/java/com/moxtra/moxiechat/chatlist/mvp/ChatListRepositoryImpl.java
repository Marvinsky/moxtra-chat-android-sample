package com.moxtra.moxiechat.chatlist.mvp;

import com.moxtra.sdk.common.ApiCallback;
import com.moxtra.sdk.meet.model.Meet;

import java.util.List;

/**
 * @author Marvin Abisrror
 */

public class ChatListRepositoryImpl implements ChatListRepository {

    ChatListTaskListener chatListTaskListener;


    private final ApiCallback<List<Meet>> mMeetListApiCallback = new ApiCallback<List<Meet>>() {
        @Override
        public void onCompleted(List<Meet> meets) {
            //Log.d(TAG, "FetchMeets: onCompleted");
            //updateMeets(meets);
        }

        @Override
        public void onError(int errorCode, String errorMsg) {
            //Log.d(TAG, "FetchMeets: onError");
        }
    };

    public ChatListRepositoryImpl(ChatListTaskListener listener) {
        this.chatListTaskListener = listener;
    }

    @Override
    public void getChatList() {

    }
}
