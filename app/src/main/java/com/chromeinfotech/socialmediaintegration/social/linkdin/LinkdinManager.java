package com.chromeinfotech.socialmediaintegration.social.linkdin;

import android.content.Context;

import com.chromeinfotech.socialmediaintegration.utils.Utils;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;

import org.json.JSONObject;

/**
 * Created by user on 8/8/17.
 */

public class LinkdinManager {
    private static final String host = "api.linkedin.com";
    private static final String url = "https://" + host
            + "/v1/people/~:" +"(email-address,formatted-name,phone-numbers,picture-urls::(original))";
    private  Context context ;
    public LinkdinManager(Context context) {
        this.context = context;
    }
    public void linkededinApiHelper(){
        Utils.printLog("LinkdinManager","linkededinApiHelper");

        APIHelper apiHelper = APIHelper.getInstance(context);
        apiHelper.getRequest(context , url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                Utils.printLog("LinkdinManager","onApiSuccess");

                try {
                    showResult(result.getResponseDataAsJson());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onApiError(LIApiError error) {
                Utils.printLog("LinkdinManager","onApiError");
            }
        });
    }
    public  void  showResult(JSONObject response){
        Utils.printLog("LinkdinManager","showResult"+response.toString());

        try {
            Utils.printLog("LinkdinManager" ,"formattedName="+response.get("formattedName").toString());
         //   Utils.printLog("LinkdinManager" ,"emailAddress= "+response.get("emailAddress").toString());
            Utils.printLog("LinkdinManager" ,"pictureUrl= "+response.getJSONArray("pictureUrls"));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

