package com.aftershipsdk.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.aftershipsdk.R;
import com.aftershipsdk100.api.api.ApiDataManager;
import com.aftershipsdk100.api.api.ApiRequestDelegate;
import com.aftershipsdk100.api.api.ApiResult;
import com.aftershipsdk100.api.api.HttpRequest;
import com.aftershipsdk100.api.api.URLConstants;
import com.aftershipsdk100.api.model.CustomFields;
import com.aftershipsdk100.api.model.Tracking;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements ApiRequestDelegate {

    Handler mHandler = new Handler();
    Runnable runnable;

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
//                "ID 1234","http://www.www.com", customFields, this);

        ApiDataManager.getInstance().requestSingleTracking("fedex","671821155064",MainActivity.this);

    }

    @Override
    public void apiOnRequest() {

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
