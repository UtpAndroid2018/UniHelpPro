package pe.edu.utp.unihelppro.authentication;

public class Constants {
    public static final String AUTHORITY_URL = "https://login.microsoftonline.com/common";
    public static final String AUTHORIZATION_ENDPOINT = "/oauth2/v2.0/authorize";
    public static final String TOKEN_ENDPOINT = "/oauth2/v2.0/token";
    // Update these two constants with the values for your application:
    public static String CLIENT_ID = "a1452f64-3e43-427d-a49e-d40e4fca6dcc";
    public static final String REDIRECT_URI =  "https://login.microsoftonline.com/common/oauth2/nativeclient";
    public static final String[] SCOPES = {"openid","User.ReadBasic.All"};
    public static final String LOGIN_HINT = "alexd@contoso.com";
    public static final String EXTRA_QUERY_PARAM = null;
    public static final String[] ADDITIONAL_SCOPE = {"",""};

    public static final String ARG_GIVEN_NAME = "givenName";
    public static final String ARG_DISPLAY_ID = "displayableId";
}
