package com.chromeinfotech.socialmediaintegration.ui.MainActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.chromeinfotech.socialmediaintegration.R;
import com.chromeinfotech.socialmediaintegration.social.facebook.FacebookManager;
import com.chromeinfotech.socialmediaintegration.social.google.GooglePlusmanager;
import com.chromeinfotech.socialmediaintegration.social.instagram.InstagramManager;
import com.chromeinfotech.socialmediaintegration.social.linkdin.LinkdinManager;
import com.chromeinfotech.socialmediaintegration.social.twitter.TwitterManager;
import com.chromeinfotech.socialmediaintegration.ui.BaseActivity.BaseActivity;
import com.chromeinfotech.socialmediaintegration.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends BaseActivity implements View.OnClickListener ,GoogleApiClient.OnConnectionFailedListener {

    private Button btnFacebook,btnLinkdin, btnGoogle, btnTwitter, btnInstagram;
    private FacebookManager facebookIntegration ;
    private LinkdinManager linkdinManager ;
    private InstagramManager  instagramManager ;
    private TwitterManager    twitterManager ;
    private TwitterAuthClient mTwitterAuthClient;
    private final int RC_SIGN_IN = 1;
    private WebView webView ;
    private int code ;
    private   final String     TWITTER_KEY = "7tzBUJEr3TAWFJdjeeRxgjzss";
    private   final String      TWITTER_SECRET = "NxmVaRxLAd5UuOnVbITuVI6FdgATZ6Q4YHaSRiYj8CnVdZbdwZ";
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initializefabric();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generateHashkey();
        this.init();
        this.reference();
        Utils.printLog("onCreate" ,"onCreate");
        this.setListenrs();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Twitter.getInstance();
        Twitter.getSessionManager().clearActiveSession();
        Twitter.logOut();

    }

    private void initializefabric() {
        if (!Fabric.isInitialized()) {
            TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY,TWITTER_SECRET);
            Fabric.with(this, new Twitter(authConfig));
            mTwitterAuthClient =  new TwitterAuthClient() ;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void generateHashkey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            Utils.printLog("MainActivity","package name");
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(signature.toByteArray());

                Utils.printLog("MainActivity","package name    "+info.packageName);
                Utils.printLog("MainActivity","key hash   "+Base64.encodeToString(md.digest(),
                        Base64.NO_WRAP));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("MainActivity", e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.d("MainActivity", e.getMessage(), e);
        }
    }
    @Override
    public void init() {
        instagramManager   = new InstagramManager(this) ;
        mTwitterAuthClient = new TwitterAuthClient();
        twitterManager     = new TwitterManager(this);
        linkdinManager     = new LinkdinManager(this) ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient!=null) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }


    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            try {
                GooglePlusmanager googleLogin = new GooglePlusmanager(this);
                googleLogin.handleSignInResult(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if(code == 1){
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }else if(code == 2 && mTwitterAuthClient != null)
                mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
            else if(code == 3)
                LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override
    public void reference() {
        btnFacebook     = (Button)  findViewById(R.id.btn_Facebook) ;
        btnGoogle       = (Button)  findViewById(R.id.btn_Google) ;
        btnTwitter      = (Button)  findViewById(R.id.btn_Twitter) ;
        btnLinkdin      = (Button)  findViewById(R.id.btn_Linkdin) ;
        btnInstagram    = (Button)  findViewById(R.id.btn_Instagram) ;
        webView         = (WebView) findViewById(R.id.webView);

    }

    @Override
    public void setListenrs() {
        btnFacebook.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
        btnTwitter.setOnClickListener(this);
        btnInstagram.setOnClickListener(this);
        btnLinkdin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_Facebook :
                code = 1 ;
                fbLogin();
                break;
            case R.id.btn_Linkdin :
                code=3;
                linkdinLogin();
                break;
            case R.id.btn_Google :
                if(mGoogleApiClient!=null) {
                    processSignOut();
                    Utils.printLog("MainActivity","inside if");
                }else{
                    Utils.printLog("MainActivity","inside else");
                    googleSignIn();
                }
                break;
            case R.id.btn_Instagram :
                //  InstagramManager.logout();
                webView.setVisibility(View.VISIBLE);
                instagramLogin();
                break;
            case R.id.btn_Twitter :
                code = 2;
                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                if(session == null)
                {
                    twitterManager.twitterLogin();
                }
                else
                {
                    long userid = session.getUserId();
                    Twitter.getSessionManager().clearActiveSession();
                }
                break;
        }
    }

    private void linkdinLogin() {
        Utils.printLog("MainActivity","linkdinLogin");
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                Utils.printLog("MainActivity","onAuthSuccess");
                linkdinManager.linkededinApiHelper();
                // Authentication was successful.  You can now do
                // other calls with the SDK.
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Utils.printLog("MainActivity","onAuthError");
            }
        }, true);
    }

    // Build the list of member permissions our LinkedIn session requires
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE , Scope.R_EMAILADDRESS, Scope.RW_COMPANY_ADMIN);
    }

    /**
     * instagramLogin is used to perform instagram login task
     */
    private void instagramLogin() {

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        Utils.printLog("Instagram",""+InstagramManager.getTokenUrl());
        webView.loadUrl(InstagramManager.getTokenUrl());

    }

    /**
     * fbLogin() is used to perform  fb login task.
     */

    private void fbLogin() {
        callbackManager = CallbackManager.Factory.create();
        facebookIntegration = new FacebookManager(this, callbackManager);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
        facebookIntegration.registerCallbackSetup();
        if (AccessToken.getCurrentAccessToken() != null) {
            facebookIntegration.RequestData();
        }
    }

    /**
     * API to handle sign out of user
     */
    private void processSignOut() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

    }
    private void googleSignIn() {

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class MyWebViewClient extends WebViewClient {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            String url = request.getUrl().toString();
            Log.e("URL " , url);
            if (url.startsWith(InstagramManager.CALLBACK_URL)) {
                String urls[] = url.split("=");
                Log.e("Url " , "final " + urls[1]);
                getUsername(urls[1]);
                return true;
            }
            return false;
        }
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    private void getUsername(String url) {
        InstagramManager.getUsername(url);
        webView.setVisibility(View.GONE);
    }
}
