package com.chromeinfotech.socialmediaintegration.social.facebook;

import android.content.Context;
import android.os.Bundle;

import com.chromeinfotech.socialmediaintegration.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 1/8/17.
 */

public class FacebookManager {

    private Context context;
    private CallbackManager callbackManager;
    private final String TAG = getClass().getSimpleName();
    public FacebookManager(Context context, CallbackManager callbackManager){
        this.context = context;
        this.callbackManager = callbackManager;
        Utils.printLog(TAG,"constructor");

    }

    /**
     * registerCallbackSetup() register the call back of facebook.
     */
    public void registerCallbackSetup() {
        Utils.printLog(TAG,"registerCallbackSetup");

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    RequestData();
                }

            }

            @Override
            public void onCancel() {
                Utils.printLog(TAG,"onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Utils.printLog(TAG,"onCancel");

            }
        });
    }

    /**
     * RequestData() is used to fetch the data from fb request.
     */

    public void RequestData() {
        Utils.printLog(TAG,"RequestData");

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Utils.printLog(TAG,"onCompleted");
                        Utils.printLog("Fb", response.toString());
                        JSONObject jsonObject = response.getJSONObject();
                        try {
                            if (jsonObject != null) {
                                String name = jsonObject.getString("name");
                                String email = jsonObject.getString("email");
                                Utils.printLog(" Email ", email);
                                String profileLink = "Profile link :  " + jsonObject.getString("link");
                                String[] splited = name.split("\\s+");
                                String fname = splited[0];
                                String lname = splited[1];
                                Utils.printLog(TAG,"name="+name);
                                Utils.printLog(TAG,""+email);
                                Utils.printLog(TAG,"profileLink="+profileLink);
                                Utils.printLog(TAG,"fname="+fname);
                                Utils.printLog(TAG,"lname="+lname);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle bundle = new Bundle();
        bundle.putString("fields", "id,name,link,email,picture");
        request.setParameters(bundle);
        request.executeAsync();
    }
}
