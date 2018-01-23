package com.moxtra.moxiechat.chatlist;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.moxtra.moxiechat.BaseActivity;
import com.moxtra.moxiechat.ChatActivity;
import com.moxtra.moxiechat.R;
import com.moxtra.moxiechat.chatlist.adapter.ChatListAdapter;
import com.moxtra.moxiechat.interfaces.ChatListView;
import com.moxtra.moxiechat.model.DummyData;
import com.moxtra.moxiechat.model.MoxieUser;
import com.moxtra.moxiechat.model.Session;
import com.moxtra.moxiechat.chatlist.mvp.ChatListPresenterImpl;
import com.moxtra.sdk.ChatClient;
import com.moxtra.sdk.chat.controller.ChatController;
import com.moxtra.sdk.chat.repo.ChatRepo;
import com.moxtra.sdk.client.ChatClientDelegate;
import com.moxtra.sdk.common.ApiCallback;
import com.moxtra.sdk.common.model.MyProfile;
import com.moxtra.sdk.meet.model.Meet;
import com.moxtra.sdk.meet.repo.MeetRepo;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends BaseActivity implements View.OnClickListener, ChatListView {

    private static final String TAG = "DEMO_ChatList";

    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mRecyclerView;
    private ChatListAdapter mAdapter;
    //private ChatListPresenterImpl chatListPresenter;


    private final ApiCallback<List<Meet>> mMeetListApiCallback = new ApiCallback<List<Meet>>() {
        @Override
        public void onCompleted(List<Meet> meets) {
            Log.d(TAG, "FetchMeets: onCompleted");
            setLoading(false);
            mAdapter.updateMeets(meets);
        }

        @Override
        public void onError(int errorCode, String errorMsg) {
            Log.d(TAG, "FetchMeets: onError");
        }
    };

    private RecyclerView.LayoutManager mLayoutManager;
    private List<MoxieUser> mMoxieUserList;
    List<Session> sessionList;
    private MyProfile mMyProfile;
    private ChatClientDelegate mChatClientDelegate;
    private ChatRepo mChatRepo;
    private MeetRepo mMeetRepo;
    private ChatController mChatController;

    /*private static boolean isEnded(Meet meet) {
        return !meet.isInProgress() && !(meet.getScheduleStartTime() > 0 && System.currentTimeMillis() < meet.getScheduleEndTime());
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Log.d(TAG, "onCreate!");

        sessionList = new ArrayList<>();
        //chatListPresenter = new ChatListPresenterImpl(this);
        initializeView();

        //Initialize with ChatClient
        mMyProfile = ChatClient.getMyProfile();
        mChatClientDelegate = ChatClient.getClientDelegate();
        if (mChatClientDelegate == null) {
            Log.e(TAG, "Unlinked, ChatClient is null.");
            finish();
            return;
        }

        mAdapter = new ChatListAdapter(ChatListActivity.this, mMyProfile, mChatClientDelegate, sessionList);
        mRecyclerView.setAdapter(mAdapter);

        //chatListPresenter.loadChatList();
    }

    private void initializeView() {
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_chat_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatController != null) {
            mChatController.cleanup();
            mChatController = null;
        }
        mAdapter.cleanObjects();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            new MaterialDialog.Builder(this)
                    .title(R.string.selectUserTitle)
                    .items(getUserList())
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                            ArrayList<String> uniqueIdList = new ArrayList<>();
                            uniqueIdList.add(mMoxieUserList.get(i).uniqueId);
                            String topic = mMyProfile.getFirstName() + "'s chat";
                            ChatActivity.startGroupChat(ChatListActivity.this, topic, uniqueIdList);
                        }
                    })
                    .show();
        }
    }

    @Override
    public void showChatList(List<Session> sessions) {
        sessionList.clear();
        sessionList.addAll(sessions);
        hideLoading();
    }

    @Override
    public void showLoading() {
        setLoading(true);
    }

    @Override
    public void hideLoading() {
        setLoading(false);
    }

    private List<String> getUserList() {
        mMoxieUserList = DummyData.getUserListForSelect(DummyData.findByUniqueId(mMyProfile.getUniqueId()));
        List<String> users = new ArrayList<>(mMoxieUserList.size());
        for (MoxieUser user : mMoxieUserList) {
            users.add(user.firstName + " " + user.lastName);
        }
        return users;
    }
}
