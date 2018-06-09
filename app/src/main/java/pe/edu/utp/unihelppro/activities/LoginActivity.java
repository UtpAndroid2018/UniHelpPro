package pe.edu.utp.unihelppro.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.Logger;
import com.microsoft.identity.client.MsalClientException;
import com.microsoft.identity.client.MsalException;
import com.microsoft.identity.client.MsalServiceException;
import com.microsoft.identity.client.MsalUiRequiredException;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.User;

import java.util.List;

import pe.edu.utp.unihelppro.Defaults;
import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.authentication.AuthenticationManager;
import pe.edu.utp.unihelppro.Connect;
import pe.edu.utp.unihelppro.authentication.Constants;
import pe.edu.utp.unihelppro.authentication.MSALAuthenticationCallback;
import pe.edu.utp.unihelppro.utils.Navigation;
import pe.edu.utp.unihelppro.utils.SharedPrefsUtils;
import pe.edu.utp.unihelppro.utils.UserUtils;

import android.os.Handler;

import java.net.URI;
import java.util.UUID;


public class LoginActivity extends AppCompatActivity implements MSALAuthenticationCallback {

    private static final String TAG = "LoginActivity";
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Context mContext;
    private AuthenticationResult mAuthResult;

    private PublicClientApplication mApplication;
    private boolean mEnablePiiLogging = false;
    private boolean doubleBackToExitPressedOnce = false;
    private User mUser;
    private Handler mHandler;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Bundle extras = getIntent().getExtras();
        Connect.getInstance().setConnectActivity(this);

        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mContext = this;

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        if (mApplication == null) {
            mApplication = new PublicClientApplication(
                    this.getApplicationContext(),
                    Constants.CLIENT_ID);
        }

        if (!hasAzureConfiguration()) {
            Toast.makeText(
                    LoginActivity.this,
                    getString(R.string.warning_client_id_redirect_uri_incorrect),
                    Toast.LENGTH_LONG).show();
            resetUIForConnect();
            return;
        }

        ImageView login_with_outlook = ( ImageView ) findViewById(R.id.login_with_outlook);
        login_with_outlook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
                showConnectingInProgressUI();
            }
        });

    }

    private void resetUIForConnect() {
        //mConnectButton.setVisibility(View.VISIBLE);
        //mTitleTextView.setVisibility(View.GONE);
        //mDescriptionTextView.setVisibility(View.GONE);
        //mConnectProgressBar.setVisibility(View.GONE);
    }

    private void showConnectingInProgressUI() {
        //mConnectButton.setVisibility(View.GONE);
        //mTitleTextView.setVisibility(View.GONE);
        //mDescriptionTextView.setVisibility(View.GONE);
        //mConnectProgressBar.setVisibility(View.VISIBLE);
    }

    private void showConnectErrorUI() {
        //mConnectButton.setVisibility(View.VISIBLE);
        //mConnectProgressBar.setVisibility(View.GONE);
        //mTitleTextView.setText(R.string.title_text_error);
        //mTitleTextView.setVisibility(View.VISIBLE);
        //mDescriptionTextView.setText(R.string.connect_text_error);
        //mDescriptionTextView.setVisibility(View.VISIBLE);
        Toast.makeText(
                LoginActivity.this,
                R.string.connect_toast_text_error,
                Toast.LENGTH_LONG).show();
    }

    private void showMessage(final String msg) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private Handler getHandler() {
        if (mHandler == null) {
            return new Handler(LoginActivity.this.getMainLooper());
        }

        return mHandler;
    }


    private void connect() {

        // The sample app is having the PII enable setting on the MainActivity. Ideally, app should decide to enable Pii or not,
        // if it's enabled, it should be  the setting when the application is onCreate.
        if (mEnablePiiLogging) {
            Logger.getInstance().setEnablePII(true);
        } else {
            Logger.getInstance().setEnablePII(false);
        }

        AuthenticationManager mgr = AuthenticationManager.getInstance();

        List<User> users = null;

        try {
            users = mApplication.getUsers();

            if (users != null && users.size() == 1) {
                /* We have 1 user */
                mUser = users.get(0);
                mgr.callAcquireTokenSilent(
                        mUser,
                        true,
                        this);
            } else {
                /* We have no user */

                /* Let's do an interactive request */
                mgr.callAcquireToken(
                        this,
                        this);
            }
        } catch (MsalClientException e) {
            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString());
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "User at this position does not exist: " + e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //_authContext.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (mApplication != null) {
            mApplication.handleInteractiveRequestRedirect(requestCode, resultCode, data);
        }
    }


    private void attemptLogin() {

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
                public void handleResponse(BackendlessUser user) {
                    //Toast.makeText(mContext, user.getEmail() + " " + user.getUserId(), Toast.LENGTH_SHORT).show();
                    showProgress(false);
                    Backendless.Messaging.registerDevice(Defaults.gcmSenderID, "default", new AsyncCallback<Void>() {

                        @Override
                        public void handleResponse(Void response) {

                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                        }
                    });
                    gotoMain();
                }

                public void handleFault(BackendlessFault fault) {
                    switch (fault.getCode()) {
                        case "2002":
                            //Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    //Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_SHORT).show();
                    //mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.setError(fault.getMessage());
                    mPasswordView.requestFocus();
                    showProgress(false);
                }
            });
        }
    }

    private void gotoMain() {
        Navigation.getInstance().startActivity(this, new Bundle(), getString(R.string.MainActivityClassName), true);
    }

    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 4;
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onSuccess(AuthenticationResult authenticationResult) {
        mUser = authenticationResult.getUser();
        mAuthResult = authenticationResult;


        String name = "";
        String preferredUsername = "";

        try {
            name = mAuthResult.getUser().getName();
            preferredUsername = mAuthResult.getUser().getDisplayableId();
            AuthenticationManager mgr = AuthenticationManager.getInstance();
            mgr.setAuthentcationResult(mAuthResult);

        } catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());

        }

        String domain = preferredUsername;
        int index = domain.indexOf('@');
        domain = domain.substring(index+1);

        if( domain.equals("utp.edu.pe") ) {
            SharedPrefsUtils sharedPrefsUtils =  SharedPrefsUtils.getInstance() ;
            sharedPrefsUtils.setStringPreference( Constants.ARG_GIVEN_NAME, name );
            sharedPrefsUtils.setStringPreference(Constants.ARG_DISPLAY_ID, preferredUsername);

            Connect.getInstance().setUserOutlook( mAuthResult.getUser() );


            if ( UserUtils.isValidLogin( mContext ) ) {
                BackendlessUser user = Backendless.UserService.CurrentUser();
                if( user != null ){
                    Connect.getInstance().setUserBackendless( user );
                    gotoMain();
                }
            /*
            String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();
            Backendless.Data.of( BackendlessUser.class ).findById( currentUserObjectId, new AsyncCallback<BackendlessUser> ( ) {
                @Override
                public void handleResponse(BackendlessUser user) {
                    Connect.getInstance().setUserBackendless( user );
                    gotoMain();
                }

                @Override
                public void handleFault(BackendlessFault fault) {

                }
            } );
            */
            } else {
                UserUtils.login( preferredUsername, preferredUsername, new AsyncCallback<BackendlessUser> () {
                    @Override
                    public void handleResponse(BackendlessUser user) {
                        Connect.getInstance().setUserBackendless( user );
                        gotoMain();
                    }
                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }  );
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    resetUIForConnect();
                }
            });
        }  else {
            UserUtils.logout();
        }
    }

    @Override
    public void onError(MsalException exception) {
    // Check the exception type.
        if (exception instanceof MsalClientException) {
            // This means errors happened in the sdk itself, could be network, Json parse, etc. Check MsalError.java
            // for detailed list of the errors.
            showMessage(exception.getMessage());
        } else if (exception instanceof MsalServiceException) {
            // This means something is wrong when the sdk is communication to the service, mostly likely it's the client
            // configuration.
            showMessage(exception.getMessage());
        } else if (exception instanceof MsalUiRequiredException) {
            // This explicitly indicates that developer needs to prompt the user, it could be refresh token is expired, revoked
            // or user changes the password; or it could be that no token was found in the token cache.
            AuthenticationManager mgr = AuthenticationManager.getInstance();


            mgr.callAcquireToken(LoginActivity.this, this);
        }
    }

    @Override
    public void onCancel() {

        showMessage("User cancelled the flow.");
        //showConnectErrorUI("User cancelled the flow.");
    }


    private static boolean hasAzureConfiguration() {
        try {
            UUID.fromString(Constants.CLIENT_ID);
            URI.create(Constants.REDIRECT_URI);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

