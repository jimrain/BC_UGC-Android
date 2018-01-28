package net.rainville.android.bcugc;

/**
 * Created by jim on 1/27/18.
 */

public class BrightcoveCMS {
    private String AccountID;
    private String ClientID;
    private String ClientSecret;

    public BrightcoveCMS() {}

    public BrightcoveCMS(String accountID, String clientID, String clientSecret) {
        AccountID = accountID;
        ClientID = clientID;
        ClientSecret = clientSecret;
    }
}
