package net.rainville.android.bcugc;

import java.net.URL;

/**
 * Created by jim on 1/27/18.
 */

public class BrightcoveCMS {
    private String AccountID;
    private String ClientID;
    private String ClientSecret;

    private static final String OauthUrl = "https://oauth.brightcove.com/v4/access_token";


    public BrightcoveCMS() {}

    public BrightcoveCMS(String accountID, String clientID, String clientSecret) {
        AccountID = accountID;
        ClientID = clientID;
        ClientSecret = clientSecret;
    }

    public String getAccessToken() {
        String accessToken = null;

        // URL authEndpoint = new URL(OauthUrl);

        return (accessToken);
    }
}
