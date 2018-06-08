package pe.edu.utp.unihelppro.authentication;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.core.DefaultClientConfig;
import com.microsoft.graph.core.IClientConfig;
import com.microsoft.graph.extensions.GraphServiceClient;
import com.microsoft.graph.extensions.IGraphServiceClient;
import com.microsoft.graph.http.IHttpRequest;

import java.io.IOException;

public class GraphServiceClientManager implements IAuthenticationProvider {
    private IGraphServiceClient mGraphServiceClient;
    private static GraphServiceClientManager INSTANCE;

    private GraphServiceClientManager() {}

    /**
     * Appends an access token obtained from the {@link AuthenticationManager} class to the
     * Authorization header of the request.
     * @param request
     */
    @Override
    public void authenticateRequest(IHttpRequest request)  {
        try {
            request.addHeader("Authorization", "Bearer "
                    + AuthenticationManager.getInstance()
                    .getAccessToken());
            // This header has been added to identify this sample in the Microsoft Graph service.
            // If you're using this code for your project please remove the following line.
            request.addHeader("SampleID", "android-java-connect-sample");
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static synchronized GraphServiceClientManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new GraphServiceClientManager();
        }
        return INSTANCE;
    }

    public synchronized IGraphServiceClient getGraphServiceClient() {
        return getGraphServiceClient(this);
    }

    public synchronized IGraphServiceClient getGraphServiceClient(IAuthenticationProvider authenticationProvider) {
        if (mGraphServiceClient == null) {
            IClientConfig clientConfig = DefaultClientConfig.createWithAuthenticationProvider(
                    authenticationProvider
            );
            mGraphServiceClient = new GraphServiceClient.Builder().fromConfig(clientConfig).buildClient();
        }

        return mGraphServiceClient;
    }
}
