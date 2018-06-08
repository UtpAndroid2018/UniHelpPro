package pe.edu.utp.unihelppro.authentication;

import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.MsalException;

public interface MSALAuthenticationCallback {
    void onSuccess(AuthenticationResult authenticationResult);
    void onError(MsalException exception);
    void onCancel();
}
