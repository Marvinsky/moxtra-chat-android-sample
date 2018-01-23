package com.moxtra.moxiechat.login.mvp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.moxtra.moxiechat.GcmRegistrationService;
import com.moxtra.moxiechat.MoxieChatApplication;
import com.moxtra.moxiechat.common.PreferenceUtil;
import com.moxtra.moxiechat.constants.Constants;
import com.moxtra.sdk.ChatClient;
import com.moxtra.sdk.client.ChatClientDelegate;
import com.moxtra.sdk.common.ApiCallback;

/**
 * @author Marvin Abisrror
 */

public class LoginRepositoryImpl implements LoginRepository {

    private static final String TAG = "DEMO_LoginActivity";
    LoginTaskListener loginTaskListener;


    public LoginRepositoryImpl(LoginTaskListener listener) {
        this.loginTaskListener = listener;
    }

    @Override
    public void LinkWithUniqueId(final String uniqueId) {
        Log.d(TAG, "Start to linkWithUniqueID...");
        ChatClient.linkWithUniqueId(uniqueId, Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.ORG_ID, Constants.BASE_DOMAIN,
                new ApiCallback<ChatClientDelegate>() {
                    @Override
                    public void onCompleted(ChatClientDelegate ccd) {
                        Log.i(TAG, "Linked to Moxtra account successfully.");
                        //PreferenceUtil.saveUser(mContext, uniqueId);
                        loginTaskListener.persistUniqueId(uniqueId);
                        loginTaskListener.loginSuccess();
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        //Toast.makeText(mContext, "Failed to link to Moxtra account.", Toast.LENGTH_LONG).show();
                        loginTaskListener.loginError(String.valueOf(errorCode), errorMsg);
                        Log.e(TAG, "Failed to link to Moxtra account, errorCode=" + errorCode + ", errorMsg=" + errorMsg);
                    }
                });
    }

    @Override
    public void LinkWithToken(String token) {
        Log.d(TAG, "Start to linkWithAccessToken...");
        ChatClient.linkWithAccessToken(token, Constants.BASE_DOMAIN, new ApiCallback<ChatClientDelegate>() {
            @Override
            public void onCompleted(ChatClientDelegate ccd) {
                Log.i(TAG, "Linked to Moxtra account successfully.");
                loginTaskListener.loginSuccess();
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                //Toast.makeText(mContext, "Failed to link to Moxtra account.", Toast.LENGTH_LONG).show();
                loginTaskListener.loginError(String.valueOf(errorCode), errorMsg);
                Log.e(TAG, "Failed to link to Moxtra account, errorCode=" + errorCode + ", errorMsg=" + errorMsg);
            }
        });
    }
}
