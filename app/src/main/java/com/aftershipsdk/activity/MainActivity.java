package com.aftershipsdk.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.aftershipsdk.R;
import com.aftershipsdk100.sdk.api.ApiDataManager;
import com.aftershipsdk100.sdk.api.ApiRequestDelegate;
import com.aftershipsdk100.sdk.api.ApiResult;
import com.aftershipsdk100.sdk.api.HttpRequest;
import com.aftershipsdk100.sdk.api.URLConstants;
import com.aftershipsdk100.sdk.model.CustomFields;
import com.aftershipsdk100.sdk.model.Tracking;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements ApiRequestDelegate {

    Handler mHandler = new Handler();
    Runnable runnable;

    private static final String API_KEY = "0d675b8f-7a3c-4f98-b1b8-46824a791174";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> smses = new ArrayList<>();
        smses.add("+18555072509");
        smses.add("+18555072503");

        List<String> emails = new ArrayList<>();
        emails.add("email@yourdomain.com");
        emails.add("another_email@yourdomain.com");

        CustomFields customFields = new CustomFields();
        customFields.product_name = "iPhone Case";
        customFields.product_price = "USD19.99";

//        ApiDataManager.getInstance().createTracking("dhl","4134837490","title name",smses, emails,
//                "ID 1234","http://www.www.com", customFields, this, API_KEY);

        ApiDataManager.getInstance().requestSingleTracking("fedex","671821155064",MainActivity.this, API_KEY);

    }

    @Override
    public void apiCompleted(ApiResult apiResult, HttpRequest httpRequest) {

        // Fail case
        if (!apiResult.success){
            ApiDataManager.handleMessageForReason(apiResult.failReason, apiResult.failType, this);
            return;
        }

        switch (httpRequest.tag) {

            case URLConstants.URL_CREATE_TRACKING:
                Tracking tracking = (Tracking) apiResult.valueObject;

                break;

            case URLConstants.URL_GET_SINGLE_TRACKING:

                Log.v("Tracking_Single", "Success");
                tracking = (Tracking) apiResult.valueObject;

                break;

            default:
                break;
        }
    }
}
