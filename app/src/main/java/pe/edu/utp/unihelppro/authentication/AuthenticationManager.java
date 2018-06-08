package pe.edu.utp.unihelppro.authentication;

import android.accounts.AuthenticatorException;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import android.accounts.OperationCanceledException;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.MsalException;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.User;
import java.io.IOException;

import pe.edu.utp.unihelppro.Connect;

public class AuthenticationManager {
    private static final String TAG = "AuthenticationManager";
    private static AuthenticationManager INSTANCE;
    private static PublicClientApplication mApplication;
    private AuthenticationResult mAuthResult;
    private MSALAuthenticationCallback mActivityCallback;
    private AuthenticationManager() {
    }

    public static synchronized AuthenticationManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AuthenticationManager();
            if (mApplication == null) {
                mApplication = new PublicClientApplication(
                        Connect.getInstance()
                                .getConnectActivity()
                                .getApplicationContext(),Constants.CLIENT_ID);
            }

        }
        return INSTANCE;
    }

    public static synchronized void resetInstance() {
        INSTANCE = null;
    }


    public void setAuthentcationResult(AuthenticationResult authentcationResult) {
        mAuthResult = authentcationResult;
    }
    /**
     * Returns the access token obtained in authentication
     *
     * @return mAccessToken
     */
    public String getAccessToken() throws AuthenticatorException, IOException, OperationCanceledException {
        return  mAuthResult.getAccessToken();
    }

    public void connect(Activity activity, final MSALAuthenticationCallback authenticationCallback){

        mActivityCallback = authenticationCallback;
        mApplication.acquireToken(
                activity, Constants.SCOPES, getAuthInteractiveCallback());

    }

    /**
     * Disconnects the app from Office 365 by clearing the token cache, setting the client objects
     * to null, and removing the user id from shred preferences.
     */
    public void disconnect() {

        mApplication.remove(mAuthResult.getUser());
        // Reset the AuthenticationManager object
        AuthenticationManager.resetInstance();
    }

    public void callAcquireToken(Activity activity, final MSALAuthenticationCallback authenticationCallback) {
        // The sample app is having the PII enable setting on the MainActivity. Ideally, app should decide to enable Pii or not,
        // if it's enabled, it should be  the setting when the application is onCreate.
//        if (mEnablePiiLogging) {
//            Logger.getInstance().setEnablePII(true);
//        } else {
//            Logger.getInstance().setEnablePII(false);
//        }
        mActivityCallback = authenticationCallback;

        mApplication.acquireToken(activity, Constants.SCOPES, getAuthInteractiveCallback());
    }
    public void callAcquireTokenSilent(User user, boolean forceRefresh, MSALAuthenticationCallback msalAuthenticationCallback) {
        mActivityCallback = msalAuthenticationCallback;
        mApplication.acquireTokenSilentAsync(Constants.SCOPES, user, null, forceRefresh, getAuthSilentCallback());
    }
//
// App callbacks for MSAL
// ======================
// getActivity() - returns activity so we can acquireToken within a callback
// getAuthSilentCallback() - callback defined to handle acquireTokenSilent() case
// getAuthInteractiveCallback() - callback defined to handle acquireToken() case
//

    public Context getActivity() {
        return Connect.getContext();
    }

    /* Callback method for acquireTokenSilent calls
     * Looks if tokens are in the cache (refreshes if necessary and if we don't forceRefresh)
     * else errors that we need to do an interactive request.
     */
    private AuthenticationCallback getAuthSilentCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call Graph now */
                Log.d(TAG, "Successfully authenticated");

                /* Store the authResult */
                mAuthResult = authenticationResult;

                //invoke UI callback
                if (mActivityCallback != null)
                    mActivityCallback.onSuccess(mAuthResult);
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());
                if (mActivityCallback != null)
                    mActivityCallback.onError(exception);

            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }


    /* Callback used for interactive request.  If succeeds we use the access
     * token to call the Microsoft Graph. Does not check cache
     */
    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "ID Token: " + authenticationResult.getIdToken());

                /* Store the auth result */
                mAuthResult = authenticationResult;

                //invoke UI callback
                //invoke UI callback
                if (mActivityCallback != null)
                    mActivityCallback.onSuccess(mAuthResult);
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());
                if (mActivityCallback != null)
                    mActivityCallback.onError(exception);

            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }
}
