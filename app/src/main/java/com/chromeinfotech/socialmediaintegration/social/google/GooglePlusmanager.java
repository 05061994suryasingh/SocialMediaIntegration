package com.chromeinfotech.socialmediaintegration.social.google;

import android.content.Context;
import android.util.Log;

import com.chromeinfotech.socialmediaintegration.utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;

/**
 * Created by user on 1/8/17.
 */

public class GooglePlusmanager {

    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private final String TAG = getClass().getSimpleName() ;

    public GooglePlusmanager(Context context) {
        this.context = context;
    }

    /**
     * handleSignInResult() handle the result of goolgle sign in.
     *
     * @param result
     * @throws JSONException
     */

    public void handleSignInResult(GoogleSignInResult result) throws JSONException {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String name = acct.getDisplayName();
            String email = acct.getEmail();
            String[] splited = name.split("\\s+");
            String fname = splited[0];
            String lname = splited[1];
            Utils.printLog(TAG,"name="+name);
            Utils.printLog(TAG,""+email);
//            Utils.printLog(TAG,"profileLink="+profileLink);
            Utils.printLog(TAG,"fname="+fname);
            Utils.printLog(TAG,"lname="+lname);
            Utils.printLog("Google details : " + result, "\n" + fname + email + lname + "\n" + acct);

        } else {
           Utils.showToast(context,"false");
        }

    }

}
