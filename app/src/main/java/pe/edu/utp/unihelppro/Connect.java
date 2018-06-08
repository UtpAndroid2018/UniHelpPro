package pe.edu.utp.unihelppro;

import android.app.Activity;
import android.content.Context;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.microsoft.identity.client.ILoggerCallback;
import com.microsoft.identity.client.Logger;
import com.microsoft.identity.client.User;
import com.orm.SugarApp;
import com.orm.SugarContext;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import java.util.List;

import pe.edu.utp.unihelppro.models.Incidentes;
import pe.edu.utp.unihelppro.utils.SharedPrefsUtils;
public class Connect extends MultiDexApplication  {
    public static Connect instance;

    public static Connect getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
    }

    private Activity mConnectActivity;
    private User userOutlook;
    private BackendlessUser userBackendless;
    private List<Incidentes> incidentesPublicos;
    private List<Incidentes> incidentesPropios;

    public List<Incidentes> getIncidentesPublicos() {
        return incidentesPublicos;
    }

    public void setIncidentesPublicos(List<Incidentes> incidentesPublicos) {
        this.incidentesPublicos = incidentesPublicos;
    }

    public List<Incidentes> getIncidentesPropios() {
        return incidentesPropios;
    }

    public void setIncidentesPropios(List<Incidentes> incidentesPropios) {
        this.incidentesPropios = incidentesPropios;
    }

    public BackendlessUser getUserBackendless() {
        return userBackendless;
    }

    public void setUserBackendless(BackendlessUser userBackendless) {
        this.userBackendless = userBackendless;
    }

    public User getUserOutlook() {
        return userOutlook;
    }

    public void setUserOutlook(User userOutlook) {
        this.userOutlook = userOutlook;
    }

    private StringBuffer mLogs;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    public void onCreate(){
        instance = this;
        super.onCreate();
        SugarContext.init( this );
        SharedPrefsUtils.init(this);
        Backendless.setUrl( Defaults.SERVER_URL );
        Backendless.initApp( instance,
                Defaults.APPLICATION_ID,
                Defaults.API_KEY );

        mLogs = new StringBuffer();

        Logger.getInstance().setExternalLogger(new ILoggerCallback() {
            @Override
            public void log(String tag, Logger.LogLevel logLevel, String message, boolean containsPII) {
                // contains PII indicates that if the log message contains PII information. If Pii logging is
                // disabled, the sdk never returns back logs with Pii.
                mLogs.append(message).append('\n');
            }
        });
    }
    String getLogs() {
        return mLogs.toString();
    }

    void clearLogs() {
        mLogs = new StringBuffer();
    }

    public Activity getConnectActivity() {
        return mConnectActivity;
    }

    public void setConnectActivity(Activity connectActivity) {
        mConnectActivity = connectActivity;
    }

}
