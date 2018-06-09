package pe.edu.utp.unihelppro.utils;

import android.content.Context;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;

import pe.edu.utp.unihelppro.Defaults;
import pe.edu.utp.unihelppro.authentication.AuthenticationManager;
import pe.edu.utp.unihelppro.authentication.Constants;

public class UserUtils {
    public static boolean isValidLogin(final Context mContext ) {
        try {
            return Backendless.UserService.isValidLogin();
        }
        catch( BackendlessException | IllegalArgumentException exception ) {
            //Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    public static void register(BackendlessUser user, final AsyncCallback<BackendlessUser> listener ) {
        Backendless.UserService.register( user, new AsyncCallback<BackendlessUser>() {
            public void handleResponse( BackendlessUser registeredUser ) {
                Backendless.Messaging.registerDevice(Defaults.gcmSenderID, "default", new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void response) {
                        //Toast.makeText(mContext, response.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        //Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                listener.handleResponse( registeredUser );
            }

            public void handleFault( BackendlessFault fault ) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
                listener.handleFault( fault );
            }
        } );
    }
    public static void login(String email, String password, final AsyncCallback<BackendlessUser> listener ) {
        Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
            public void handleResponse(BackendlessUser user) {
                Backendless.Messaging.registerDevice(Defaults.gcmSenderID, "default", new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void response) {
                        //Toast.makeText(mContext, response.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        //Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                listener.handleResponse( user );
            }

            public void handleFault(BackendlessFault fault) {
                switch (fault.getCode()) {
                    case "2002":
                        //Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_SHORT).show();
                        break;
                }
                //Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_SHORT).show();
                listener.handleFault( fault );
            }
        }, true);
    }
    public static void logout() {
        try {
            AuthenticationManager mgr = AuthenticationManager.getInstance();
            mgr.disconnect();
            Backendless.UserService.logout();
            Backendless.Messaging.unregisterDevice();
            SharedPrefsUtils sharedPrefsUtils =  SharedPrefsUtils.getInstance() ;
            sharedPrefsUtils.setStringPreference( Constants.ARG_GIVEN_NAME, "" );
            sharedPrefsUtils.setStringPreference(Constants.ARG_DISPLAY_ID, "");

        }
        catch( BackendlessException exception ) {
            //Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
