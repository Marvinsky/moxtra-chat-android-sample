package com.moxtra.moxiechat;

import android.app.Application;
import android.content.Context;

import com.moxtra.sdk.ChatClient;

public class MoxieChatApplication extends Application {

    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        ChatClient.initialize(this);
        this.context = this;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
