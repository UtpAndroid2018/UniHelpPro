package pe.edu.utp.unihelppro;

import com.backendless.push.BackendlessBroadcastReceiver;
import com.backendless.push.BackendlessPushService;

public class MyPushReceiver extends BackendlessBroadcastReceiver {
    @Override
    public Class<? extends BackendlessPushService> getServiceClass(){
        return MyPushService.class;
    }
}
