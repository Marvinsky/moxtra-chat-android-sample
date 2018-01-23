package com.moxtra.moxiechat.chatlist.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.moxtra.moxiechat.ChatActivity;
import com.moxtra.moxiechat.MeetActivity;
import com.moxtra.moxiechat.R;
import com.moxtra.moxiechat.model.Session;
import com.moxtra.sdk.chat.model.Chat;
import com.moxtra.sdk.chat.repo.ChatRepo;
import com.moxtra.sdk.client.ChatClientDelegate;
import com.moxtra.sdk.common.ApiCallback;
import com.moxtra.sdk.common.BaseRepo;
import com.moxtra.sdk.common.model.MyProfile;
import com.moxtra.sdk.common.model.User;
import com.moxtra.sdk.meet.model.Meet;
import com.moxtra.sdk.meet.repo.MeetRepo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Marvin Abisrror
 */

public class ChatListAdapter extends RecyclerView.Adapter {

    private static final String TAG = "DEMO_ChatList";
    private Context mContext;
    private MyProfile mMyProfile;
    private ChatRepo mChatRepo;
    private MeetRepo mMeetRepo;
    private ChatClientDelegate mChatClientDelegate;

    private List<Session> mSessionList;
    List<com.moxtra.sdk.chat.model.Chat> chatList;
    List<Meet> meetList;

    private final ApiCallback<List<Meet>> mMeetListApiCallback = new ApiCallback<List<Meet>>() {
        @Override
        public void onCompleted(List<Meet> meets) {
            Log.d(TAG, "FetchMeets: onCompleted");
            updateMeets(meets);
        }

        @Override
        public void onError(int errorCode, String errorMsg) {
            Log.d(TAG, "FetchMeets: onError");
        }
    };

    public ChatListAdapter(Context context, MyProfile profile, ChatClientDelegate delegate, List<Session> chats) {
        super();

        this.mContext = context;
        this.mMyProfile = profile;
        this.mChatClientDelegate = delegate;
        this.mSessionList = chats;

        mChatRepo = mChatClientDelegate.createChatRepo();
        mMeetRepo = mChatClientDelegate.createMeetRepo();

        mChatRepo.setOnChangedListener(new BaseRepo.OnRepoChangedListener<com.moxtra.sdk.chat.model.Chat>() {
            @Override
            public void onCreated(List<com.moxtra.sdk.chat.model.Chat> items) {
                Log.d(TAG, "Session: onCreated");
                //setLoading(false);
                updateChats(mChatRepo.getList());
            }

            @Override
            public void onUpdated(List<com.moxtra.sdk.chat.model.Chat> items) {
                Log.d(TAG, "Session: onUpdated");
                //setLoading(false);
                updateChats(mChatRepo.getList());
            }

            @Override
            public void onDeleted(List<com.moxtra.sdk.chat.model.Chat> items) {
                Log.d(TAG, "Session: onDeleted");
                //setLoading(false);
                updateChats(mChatRepo.getList());
            }
        });

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

        //setLoading(true);
        updateChats(mChatRepo.getList());
        //setLoading(false);
        mMeetRepo.fetchMeets(mMeetListApiCallback);

    }

    private void leaveOrDeleteChat(com.moxtra.sdk.chat.model.Chat chat) {
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

    private void sortData() {
        Collections.sort(mSessionList, new Comparator<Session>() {
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


    public void updateChats(List<Chat> chats) {
        this.chatList = chats;
        refreshData();
    }

    public void updateMeets(List<Meet> meets) {
        this.meetList = meets;
        refreshData();
    }

    public void refreshData() {
        //setLoading(false);
        mSessionList.clear();
        if (chatList != null) {
            for (Chat chat : chatList) {
                mSessionList.add(new Session(chat));
            }
        }
        if (meetList != null) {
            for (Meet meet : meetList) {
                if (!isEnded(meet)) {
                    mSessionList.add(new Session(meet));
                }
            }
        }
        sortData();
        notifyDataSetChanged();
    }

    public void cleanObjects() {
        if (mMeetRepo != null) {
            mMeetRepo.cleanup();
        }

        if (mChatRepo != null) {
            mChatRepo.cleanup();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timeline, parent, false);
        return new ChatListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ChatListAdapter.ViewHolder theHolder = (ChatListAdapter.ViewHolder) holder;
        Session session = mSessionList.get(position);
        theHolder.session = session;

        if (session.isMeet) {
            final Meet meet = session.meet;
            ((CardView) theHolder.itemView).setCardBackgroundColor(
                    ContextCompat.getColor(mContext, R.color.yellow_100));
            theHolder.tvTopic.setText(meet.getTopic());
            if (!meet.isInProgress()) {
                theHolder.btnMeet.setVisibility(View.GONE);
            } else {
                theHolder.btnMeet.setVisibility(View.VISIBLE);
                theHolder.btnMeet.setText(R.string.Join);
                theHolder.btnMeet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MeetActivity.joinMeet(mContext, meet);
                    }
                });
            }
            theHolder.btnDelete.setVisibility(View.GONE);
            theHolder.tvBadge.setVisibility(View.INVISIBLE);
        } else {
            final com.moxtra.sdk.chat.model.Chat chat = session.chat;
            chat.fetchCover(new ApiCallback<String>() {
                @Override
                public void onCompleted(final String avatarPath) {
                    Log.d(TAG, " Session cover=" + avatarPath);
                    if (!TextUtils.isEmpty(avatarPath)) {
                        theHolder.ivCover.setImageURI(Uri.fromFile(new File(avatarPath)));
                    } else {
                        theHolder.ivCover.setImageResource(R.mipmap.ic_launcher);
                    }
                }

                @Override
                public void onError(int errorCode, String errorMsg) {
                }
            });

            ((CardView) theHolder.itemView).setCardBackgroundColor(
                    ContextCompat.getColor(mContext, R.color.white));
            theHolder.tvTopic.setText(chat.getTopic());
            theHolder.tvLastMessage.setText(chat.getLastFeedContent());
            theHolder.btnMeet.setText(R.string.Meet);
            theHolder.btnMeet.setVisibility(View.VISIBLE);
            theHolder.btnMeet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<User> userList = new ArrayList<>();
                    userList.addAll(chat.getMembers());
                    String topic = mMyProfile.getFirstName() + "'s " + "meet";
                    MeetActivity.startMeet(mContext, topic, userList);
                }
            });
            if (mMyProfile.getUniqueId().equals(chat.getOwner().getUniqueId())) {
                theHolder.btnDelete.setText(R.string.Delete);
            } else {
                theHolder.btnDelete.setText(R.string.Leave);
            }
            theHolder.btnDelete.setVisibility(View.VISIBLE);
            theHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(mContext)
                            .title(R.string.delete_confirm_title)
                            .content(R.string.delete_confirm)
                            .positiveText(android.R.string.yes)
                            .positiveColorRes(R.color.red_800)
                            .negativeColorRes(R.color.black)
                            .negativeText(android.R.string.no)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    leaveOrDeleteChat(chat);
                                    dialog.dismiss();
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            });
            if (chat.getUnreadFeedCount() > 0) {
                theHolder.tvBadge.setText(String.valueOf(chat.getUnreadFeedCount()));
                theHolder.tvBadge.setVisibility(View.VISIBLE);
            } else {
                theHolder.tvBadge.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mSessionList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView ivCover;
        final TextView tvTopic, tvLastMessage, tvBadge;
        final Button btnDelete, btnMeet;
        final View itemView;
        Session session;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ivCover = (ImageView) itemView.findViewById(R.id.iv_cover);
            tvTopic = (TextView) itemView.findViewById(R.id.tv_topic);
            tvLastMessage = (TextView) itemView.findViewById(R.id.tv_last_message);
            tvBadge = (TextView) itemView.findViewById(R.id.tv_badge);
            btnDelete = (Button) itemView.findViewById(R.id.btn_delete);
            btnMeet = (Button) itemView.findViewById(R.id.btn_meet);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (session.isMeet) {
                        MeetActivity.joinMeet(mContext, session.meet);
                    } else {
                        ChatActivity.showChat(mContext, session.chat);
                    }
                }
            });
        }
    }

    private static boolean isEnded(Meet meet) {
        return !meet.isInProgress() && !(meet.getScheduleStartTime() > 0 && System.currentTimeMillis() < meet.getScheduleEndTime());
    }
}
