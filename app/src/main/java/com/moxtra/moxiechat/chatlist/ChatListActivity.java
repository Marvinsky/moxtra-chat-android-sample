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
import com.moxtra.sdk.chat.model.Chat;
import com.moxtra.sdk.chat.repo.ChatRepo;
import com.moxtra.sdk.client.ChatClientDelegate;
import com.moxtra.sdk.common.ApiCallback;
import com.moxtra.sdk.common.BaseRepo;
import com.moxtra.sdk.common.model.MyProfile;
import com.moxtra.sdk.meet.model.Meet;
import com.moxtra.sdk.meet.repo.MeetRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatListActivity extends BaseActivity implements View.OnClickListener, ChatListView {

    private static final String TAG = "DEMO_ChatList";

    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mRecyclerView;
    private ChatListAdapter mAdapter;
    private ChatListPresenterImpl chatListPresenter;

    private RecyclerView.LayoutManager mLayoutManager;
    private List<MoxieUser> mMoxieUserList;

    private MyProfile mMyProfile;
    private ChatClientDelegate mChatClientDelegate;
    private ChatRepo mChatRepo;
    private MeetRepo mMeetRepo;
    List<Session> sessionList;
    List<Chat> chatList;
    List<Meet> meetList;


    private ChatController mChatController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Log.d(TAG, "onCreate!");

        sessionList = new ArrayList<>();
        chatListPresenter = new ChatListPresenterImpl(this);
        initializeView();

        //Initialize with ChatClient
        mMyProfile = ChatClient.getMyProfile();
        mChatClientDelegate = ChatClient.getClientDelegate();
        if (mChatClientDelegate == null) {
            Log.e(TAG, "Unlinked, ChatClient is null.");
            finish();
            return;
        }

        mAdapter = new ChatListAdapter(ChatListActivity.this, mMyProfile, sessionList);
        mRecyclerView.setAdapter(mAdapter);
        showLoading();
        handlers();
        chatListPresenter.loadChatMeetList();

    }

    private void handlers() {
        mChatRepo = mChatClientDelegate.createChatRepo();
        mMeetRepo = mChatClientDelegate.createMeetRepo();

        mChatRepo.setOnChangedListener(new BaseRepo.OnRepoChangedListener<Chat>() {
            @Override
            public void onCreated(List<Chat> items) {
                Log.d(TAG, "Chat: onCreated");
                updateChats(mChatRepo.getList());
            }

            @Override
            public void onUpdated(List<Chat> items) {
                Log.d(TAG, "Chat: onUpdated");
                updateChats(mChatRepo.getList());
            }

            @Override
            public void onDeleted(List<Chat> items) {
                Log.d(TAG, "Chat: onDeleted");
                updateChats(mChatRepo.getList());
            }
        });

        updateChats(mChatRepo.getList());
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
        refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatController != null) {
            mChatController.cleanup();
            mChatController = null;
        }

        if (mMeetRepo != null) {
            mMeetRepo.cleanup();
        }

        if (mChatRepo != null) {
            mChatRepo.cleanup();
        }
    }

    @Override
    public void updateChats(List<Chat> chats) {
        this.chatList = chats;
        refreshData();
    }

    @Override
    public void updateMeets(List<Meet> meets) {
        Log.d(TAG, "FetchMeets: onCompleted");
        this.meetList = meets;
        refreshData();
    }

    @Override
    public void fetchMeetsError(String errorCode, String errorMsg) {
        Log.d(TAG, "FetchMeets: onError - errorCode=" + errorCode + ", errorMsg="+ errorMsg);
    }

    @Override
    public void fetchMeets(final ApiCallback<List<Meet>> mMeetListApiCallback) {
        mMeetRepo.setOnChangedListener(new BaseRepo.OnRepoChangedListener<Meet>() {
            @Override
            public void onCreated(List<Meet> items) {
                Log.d(TAG, "Meet: onCreated");
                mMeetRepo.fetchMeets(mMeetListApiCallback);
            }

            @Override
            public void onUpdated(List<Meet> items) {
                Log.d(TAG, "Meet: onUpdated");
                mMeetRepo.fetchMeets(mMeetListApiCallback);
            }

            @Override
            public void onDeleted(List<Meet> items) {
                Log.d(TAG, "Meet: onDeleted");
                mMeetRepo.fetchMeets(mMeetListApiCallback);
            }
        });
    }

    public void refreshData() {
        sessionList.clear();
        if (sessionList != null) {
            for (Chat chat : chatList) {
                sessionList.add(new Session(chat));
            }
        }
        if (meetList != null) {
            for (Meet meet : meetList) {
                if (!isEnded(meet)) {
                    sessionList.add(new Session(meet));
                }
            }
        }
        sortData();
        mAdapter.notifyDataSetChanged();
    }

    private void sortData() {
        Collections.sort(sessionList, new Comparator<Session>() {
            @Override
            public int compare(Session lhs, Session rhs) {
                if (lhs.isMeet()) return -1;
                if (rhs.isMeet()) return 1;
                if (lhs.getChat().getLastFeedTimeStamp() > rhs.getChat().getLastFeedTimeStamp())
                    return -1;
                return 0;
            }
        });
    }

    @Override
    public void showLoading() {
        setLoading(true);
    }

    @Override
    public void hideLoading() {
        setLoading(false);
    }

    private void leaveOrDeleteChat(Chat chat) {
        mChatRepo.deleteOrLeaveChat(chat, new ApiCallback<Void>() {
            @Override
            public void onCompleted(Void result) {
                Log.i(TAG, "Leave or delete session successfully.");
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Log.e(TAG, "Failed to leave or delete session, errorCode=" + errorCode + ", errorMsg=" + errorMsg);
            }
        });
    }



    private List<String> getUserList() {
        mMoxieUserList = DummyData.getUserListForSelect(DummyData.findByUniqueId(mMyProfile.getUniqueId()));
        List<String> users = new ArrayList<>(mMoxieUserList.size());
        for (MoxieUser user : mMoxieUserList) {
            users.add(user.firstName + " " + user.lastName);
        }
        return users;
    }

    private static boolean isEnded(Meet meet) {
        return !meet.isInProgress() && !(meet.getScheduleStartTime() > 0 && System.currentTimeMillis() < meet.getScheduleEndTime());
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
}
