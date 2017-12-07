package com.chromeinfotech.socialmediaintegration.social.instagram;

import android.content.Context;
import android.util.Log;

import com.chromeinfotech.socialmediaintegration.utils.Utils;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by user on 1/8/17.
 */

public class InstagramManager {

    private static final String CLIENT_ID = "521dbd873dcb4355a12dc8e27d0c1f86";
    private static final String SECRATE_ID = "8c5ce115551a4416917734204383ab06";
    private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    public  static final String  CALLBACK_URL = "http://www.instagram.com";
    private Context context ;
    public static  String user ;
    public InstagramManager(Context context) {
        this.context = context ;
    }

    public static String getTokenUrl(){
        String mAuthUrl = AUTH_URL
                + "?client_id="
                + CLIENT_ID
                + "&redirect_uri="
                + CALLBACK_URL
                + "&response_type=code";
        return mAuthUrl;
    }

    public static void getUsername(final String code) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(TOKEN_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(
                            urlConnection.getOutputStream());
                    writer.write("client_id=" + CLIENT_ID + "&client_secret="
                            + SECRATE_ID + "&grant_type=authorization_code"
                            + "&redirect_uri=" + CALLBACK_URL + "&code=" + code);
                    writer.flush();
                    String response = Utils.streamToString(urlConnection
                            .getInputStream());
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response)
                            .nextValue();
                    String id = jsonObj.getJSONObject("user").getString("id");
                    user = jsonObj.getJSONObject("user").getString(
                            "username");
                    //instagramCallback.instaCallback(user);
                    String name = jsonObj.getJSONObject("user").getString(
                            "full_name");
                    Utils.printLog("Instagram" ,"="+response);
                    Log.e("TAG" , id + " " + user + " " + name);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    public static void logout(){
        android.webkit.CookieManager.getInstance().removeAllCookie();
    }
}
