package pe.edu.utp.unihelppro;

import android.app.Application;

import com.backendless.Backendless;

public class UniHelpProApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Backendless.setUrl( Defaults.SERVER_URL );
        Backendless.initApp( getApplicationContext(),
                Defaults.APPLICATION_ID,
                Defaults.API_KEY );
    }
}
