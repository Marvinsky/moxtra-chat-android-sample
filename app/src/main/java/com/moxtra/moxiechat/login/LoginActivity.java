package com.moxtra.moxiechat.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.moxtra.moxiechat.chatlist.ChatListActivity;
import com.moxtra.moxiechat.GcmRegistrationService;
import com.moxtra.moxiechat.R;
import com.moxtra.moxiechat.common.PreferenceUtil;
import com.moxtra.moxiechat.constants.Constants;
import com.moxtra.moxiechat.interfaces.LoginView;
import com.moxtra.moxiechat.login.mvp.LoginPresenter;
import com.moxtra.moxiechat.login.mvp.LoginPresenterImpl;
import com.moxtra.moxiechat.model.DummyData;
import com.moxtra.moxiechat.model.MoxieUser;
import com.moxtra.sdk.ChatClient;

import java.util.List;

/**
 * A login screen that offers login via unique ID.
 */
public class LoginActivity extends Activity implements LoginView {
    private static final String TAG = "DEMO_LoginActivity";

    /**
     * Here provides a sample to link with Moxtra account with accessToken via Intent.
     */


    private final Handler mHandler = new Handler();
    // UI references.
    private Context mContext;
    private AutoCompleteTextView mUniqueIdView;
    private View mProgressView;
    private View mLoginFormView;

    LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ChatClient.getMyProfile() != null) {
            // Already linked to Moxtra account
            startChatListActivity();
            return;
        }

        mContext = getApplicationContext();
        loginPresenter = new LoginPresenterImpl(this);
        initializeView();

        Intent intent = getIntent();
        if (intent != null) {
            String token = intent.getStringExtra(Constants.KEY_TOKEN);
            Log.d(TAG, "Token = " + token);
            if (token != null) {
                loginPresenter.linkWithAccessToken(token);
                return;
            }
        }

        MoxieUser user = PreferenceUtil.getUser(this);
        if (user != null) {
            loginPresenter.linkWithUniqueID(user.uniqueId);
        } else {
            showSelectionDialog();
        }
    }

    private void initializeView() {
        mProgressView = findViewById(R.id.login_progress);
        mUniqueIdView = (AutoCompleteTextView) findViewById(R.id.unique_id);
        mUniqueIdView.setText(DummyData.UNIQUE_IDS.get(0));
        addUniqueIdsToAutoComplete(DummyData.UNIQUE_IDS);

        Button mUniqueIdSignInButton = (Button) findViewById(R.id.unique_id_sign_in_button);
        mUniqueIdSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUniqueIdView.setError(null);
        String uniqueId = mUniqueIdView.getText().toString();

        // Check for a valid unique ID.
        if (TextUtils.isEmpty(uniqueId)) {
            mUniqueIdView.setError(getString(R.string.error_field_required));
            mUniqueIdView.requestFocus();
        } else {
            //linkWithUniqueID(uniqueId);
            loginPresenter.linkWithUniqueID(uniqueId);
        }
    }

    private void showSelectionDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.selectUserTitle)
                .items(DummyData.UNIQUE_IDS)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        MoxieUser moxieUser = DummyData.USERS.get(i);
                        mUniqueIdView.setText(moxieUser.uniqueId);
                    }
                })
                .show();
    }

    private void addUniqueIdsToAutoComplete(List<String> uniqueIdCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(mContext,
                        android.R.layout.simple_dropdown_item_1line, uniqueIdCollection);

        mUniqueIdView.setAdapter(adapter);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                mProgressView.animate().setDuration(shortAnimTime).alpha(
                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
            }
        });

    }

    @Override
    public void showLoading() {
        showProgress(true);
    }

    @Override
    public void hideLoading() {
        showProgress(false);
    }

    @Override
    public void startChatListActivity() {
        Log.i(TAG, "startChatListActivity");
        startActivity(new Intent(this, ChatListActivity.class));
        finish();
    }

    @Override
    public void gcmRegistrationService() {
        Intent intent = new Intent(mContext, GcmRegistrationService.class);
        startService(intent);
    }

    @Override
    public void loginError(String errorCode, String errorMsg) {
        Toast.makeText(mContext, "Failed to link to Moxtra account.", Toast.LENGTH_LONG).show();
        Log.e(TAG, "Failed to link to Moxtra account, errorCode=" + errorCode + ", errorMsg=" + errorMsg);
        hideLoading();
    }

    @Override
    public void persistUniqueId(String uniqueId) {
        PreferenceUtil.saveUser(mContext, uniqueId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
