package pe.edu.utp.unihelppro.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.MsalClientException;
import com.microsoft.identity.client.MsalException;
import com.microsoft.identity.client.MsalServiceException;
import com.microsoft.identity.client.MsalUiRequiredException;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.User;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pe.edu.utp.unihelppro.authentication.AuthenticationManager;
import pe.edu.utp.unihelppro.Connect;
import pe.edu.utp.unihelppro.authentication.Constants;
import pe.edu.utp.unihelppro.authentication.MSALAuthenticationCallback;
import pe.edu.utp.unihelppro.utils.Navigation;
import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.utils.SharedPrefsUtils;
import pe.edu.utp.unihelppro.utils.UserUtils;

public class SplashActivity extends AppCompatActivity implements MSALAuthenticationCallback {

    private static final String TAG = "SplashActivity";

    private static final int REQUEST_READ_PERMITIONS = 1;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private Context mContext;
    private AuthenticationResult mAuthResult;
    private PublicClientApplication mApplication;
    private User mUser;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Bundle extras = getIntent().getExtras();
        Connect.getInstance().setConnectActivity(this);

        mContext = this;
        if (mApplication == null) {
            mApplication = new PublicClientApplication(
                    this.getApplicationContext(),
                    Constants.CLIENT_ID);
        }
        if (!mayRequestPermisions()) {
            return;
        }

        connect();
    }

    @Override
    public void onBackPressed() {

    }

    private void connect() {

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
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        //sharedPrefs.setBooleanPreference( "skipSplash", true );
                        gotoLogin();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 3000);
            }
        } catch (MsalClientException e) {
            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString());
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "User at this position does not exist: " + e.toString());
        }
    }

    private void gotoMain() {
        Navigation.getInstance().startActivity( this, new Bundle(), getString(R.string.MainActivityClassName), true);
    }

    private void gotoLogin() {
        Navigation.getInstance().startActivity( this, new Bundle(), getString(R.string.LoginActivityClassName), true);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean mayRequestPermisions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if(!hasPermissions(this, REQUIRED_PERMISSIONS)){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_READ_PERMITIONS);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_PERMITIONS) {
            boolean rs = true;
            for (int rst: grantResults) {
                if( rst != PackageManager.PERMISSION_GRANTED ){
                    mayRequestPermisions();
                    rs = false;
                    break;
                }
            }
            if ( rs ) {
                connect();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (mApplication != null) {
            mApplication.handleInteractiveRequestRedirect(requestCode, resultCode, data);
        }
    }
    private void resetUIForConnect() {

    }

    private void showConnectingInProgressUI() {

    }

    private void showConnectErrorUI() {
        Toast.makeText(
                SplashActivity.this,
                R.string.connect_toast_text_error,
                Toast.LENGTH_LONG).show();
    }

    private Handler getHandler() {
        if (mHandler == null) {
            return new Handler(SplashActivity.this.getMainLooper());
        }

        return mHandler;
    }

    private void showMessage(final String msg) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SplashActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
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

        SharedPrefsUtils sharedPrefsUtils = SharedPrefsUtils.getInstance();
        sharedPrefsUtils.setStringPreference( Constants.ARG_GIVEN_NAME, name );
        sharedPrefsUtils.setStringPreference(Constants.ARG_DISPLAY_ID, preferredUsername);
        Connect.getInstance().setUserOutlook( mAuthResult.getUser() );

        if ( UserUtils.isValidLogin( mContext ) ) {
            //gotoMain();
            BackendlessUser user = Backendless.UserService.CurrentUser();
            if( user != null ){
                Connect.getInstance().setUserBackendless( user );
                gotoMain();
            }
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
    }

    @Override
    public void onError(MsalException exception) {
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
            mgr.callAcquireToken(SplashActivity.this, this);
        }
    }

    @Override
    public void onCancel() {
        showMessage("User cancelled the flow.");
    }
}
