package pe.edu.utp.unihelppro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.backendless.push.BackendlessPushService;

import pe.edu.utp.unihelppro.utils.Navigation;

public class MyPushService extends BackendlessPushService {

    @Override
    public void onRegistered(Context context, String registrationId ) {
        //Toast.makeText( context, "device registered" + registrationId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnregistered( Context context, Boolean unregistered ) {
        //Toast.makeText( context,"device unregistered", Toast.LENGTH_SHORT).show();
    }



    private void gotoMain(Context context) {
        Navigation.getInstance().startActivity( context, new Bundle(), getString(R.string.MainActivityClassName), false);
    }
    @Override
    public boolean onMessage( Context context, Intent intent ) {
        String message = intent.getStringExtra( "message" );
        /*
        Toast.makeText( context,
                "Push message received. Message: " + message,
                Toast.LENGTH_LONG ).show();
        */
        //gotoMain(context);
        return true;
    }

    @Override
    public void onError( Context context, String message ) {
        Toast.makeText( context, message, Toast.LENGTH_SHORT).show();
    }
}