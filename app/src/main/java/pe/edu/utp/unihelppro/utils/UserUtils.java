package pe.edu.utp.unihelppro.utils;

import android.content.Context;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;

import pe.edu.utp.unihelppro.Defaults;
import pe.edu.utp.unihelppro.authentication.AuthenticationManager;
import pe.edu.utp.unihelppro.authentication.Constants;
import pe.edu.utp.unihelppro.models.UsuarioBackendless;

public class UserUtils {
    public static void isValidLogin(final Context mContext, final AsyncCallback<Boolean> listener ) {
        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {
                listener.handleResponse( response );
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                listener.handleFault( fault );
            }
        });
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    return Backendless.UserService.isValidLogin();
                }
                catch( BackendlessException | IllegalArgumentException exception ) {
                    //Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }).start();
        */
    }
    public static void register(BackendlessUser user, final AsyncCallback<BackendlessUser> listener ) {
        Backendless.UserService.register( user, new AsyncCallback<BackendlessUser>() {
            public void handleResponse( BackendlessUser registeredUser ) {
                UsuarioBackendless ub = new UsuarioBackendless( registeredUser.getObjectId()  );
                ub.setupUser( registeredUser );
                //ub.save();
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
                listener.handleFault( fault );
            }
        } );
    }
    public static void login(final BackendlessUser user, final AsyncCallback<BackendlessUser> listener ) {
        Backendless.UserService.login(user.getEmail(), user.getPassword(), new AsyncCallback<BackendlessUser>() {
            public void handleResponse(final BackendlessUser user) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
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
                    }
                }).start();
                listener.handleResponse( user );
            }

            public void handleFault(BackendlessFault fault) {
                switch (fault.getCode()) {
                    case "3003":
                        register(user, listener) ;
                        break;
                    default:
                        listener.handleFault( fault );
                        break;
                }
                //Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, true);
    }
    public static void logout() {
        try {
            AuthenticationManager mgr = AuthenticationManager.getInstance();
            mgr.disconnect();
            Backendless.Messaging.unregisterDevice();
            Backendless.UserService.logout();
            SharedPrefsUtils sharedPrefsUtils =  SharedPrefsUtils.getInstance() ;
            sharedPrefsUtils.setStringPreference( Constants.ARG_GIVEN_NAME, "" );
            sharedPrefsUtils.setStringPreference(Constants.ARG_DISPLAY_ID, "");
        }
        catch( BackendlessException exception ) {
            //Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
