package com.chromeinfotech.socialmediaintegration.social.twitter;

import android.app.Activity;
import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.chromeinfotech.socialmediaintegration.utils.Utils;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import io.fabric.sdk.android.Fabric;

/**
 * Created by user on 18/7/17.
 */

public class TwitterManager {

    private   final String     TWITTER_KEY     = "7tzBUJEr3TAWFJdjeeRxgjzss";
    private   final String      TWITTER_SECRET = "NxmVaRxLAd5UuOnVbITuVI6FdgATZ6Q4YHaSRiYj8CnVdZbdwZ";
    private TwitterAuthClient mTwitterAuthClient;
    private Context context;
    private User user ;
    private String fullname , userSocialProfile , userEmail , userScreenName , userFirstNmae , userLastNmae ;
    private Long twitterID ;

    /**
     * constructor
     * @param context
     */
    public TwitterManager(Context context) {
        this.context = context ;
        if (!Fabric.isInitialized()) {
            TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY,TWITTER_SECRET);
            Fabric.with(context, new Twitter(authConfig));
        }
    }

    /**
     * Login to Twitter
     */
    public  void twitterLogin() {
        Utils.printLog("this", "twitterLogin");
        mTwitterAuthClient =  new TwitterAuthClient() ;
            mTwitterAuthClient.authorize((Activity) context, new com.twitter.sdk.android.core.Callback<TwitterSession>() {

                @Override
                public void success(Result<TwitterSession> twitterSessionResult) {
                    getUserDetails(twitterSessionResult.data);
                    getEmail(twitterSessionResult.data);
                }

                @Override
                public void failure(TwitterException e) {
                    e.printStackTrace();
                }
            });
        }


    private void getEmail(TwitterSession data) {
        mTwitterAuthClient.requestEmail(data, new com.twitter.sdk.android.core.Callback<String>() {
            @Override
            public void success(Result<String> emailResult) {
                String email = emailResult.data;
                Utils.printLog("this", "email"+email);
            }

            @Override
            public void failure(TwitterException e) {
                Utils.printLog("this", "email not Return");
            }
        });
    }

    /**
     * Logout from twitter
     */
    public void twitterLogout() {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        Twitter.getSessionManager().clearActiveSession();
        Twitter.logOut();
        Utils.showToast(context ,"Logout Successfully");
    }

    /**
     * get user Details from Twitter
     * @param twitterSession
     */
    public void getUserDetails(TwitterSession twitterSession) {
        Twitter.getApiClient(twitterSession).getAccountService().verifyCredentials(true, false).enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {
                try {
                    user              = userResult.data;
                    fullname          = user.name;
                    twitterID         = user.getId();
                    userSocialProfile = user.profileImageUrl;
                    userEmail         = user.email;
                    userFirstNmae     = fullname.substring(0, fullname.lastIndexOf(" "));
                    userLastNmae      = fullname.substring(fullname.lastIndexOf(" "));
                    userScreenName    = user.screenName;

                    Utils.printLog("this" ,"fullname"+fullname);
                    Utils.printLog("this" ,"twitterID"+twitterID);
                    Utils.printLog("this" ,"userEmail"+userEmail);
                    Utils.printLog("this" ,"userFirstNmae"+userFirstNmae);
                    Utils.printLog("this" ,"userLastNmae"+userLastNmae);
                    Utils.printLog("this" ,"userScreenName="+userScreenName);
                    Utils.printLog("this" ,"userScreenName="+userSocialProfile);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void failure(TwitterException e) {
            }
        });
    }


}
