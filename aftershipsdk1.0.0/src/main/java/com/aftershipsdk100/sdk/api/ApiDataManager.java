package com.aftershipsdk100.sdk.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.aftershipsdk100.sdk.model.Checkpoint;
import com.aftershipsdk100.sdk.model.CustomFields;
import com.aftershipsdk100.sdk.model.Tracking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all the http request (GET or POST) that are sent to the server
 * Also manages the JSON responses received and parses into customized
 * object accordingly, depending on the type of request
 */

public class ApiDataManager implements HttpRequestDelegate {

    // Singleton
    private static ApiDataManager instance = null;

    /**
     * Creation of the singleton object (if necessary)
     * @return singleton object
     */
    public static ApiDataManager getInstance() {
        if (instance == null){
            instance = new ApiDataManager();
            HttpRequest.initTrust();
        }
        return instance;
    }

    /**
     * Sends an HTTP POST request in order to create a tracking
     * @param slug Unique code of courier
     * @param trackingNo Tracking number of a shipment
     * @param title Title of the tracking
     * @param smses Phone number(s) to receive sms notifications
     * @param emails Email address(es) to receive email notifications
     * @param orderId Text field for order ID
     * @param orderIdPath Text field for order ID path
     * @param customFields Custom fields that accept any text string
     * @param requestDelegate interface reference
     */
    public void createTracking(String slug, String trackingNo, String title, List<String> smses,
                               List<String> emails, String orderId, String orderIdPath,
                               CustomFields customFields, ApiRequestDelegate requestDelegate, String API_KEY) {

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.url = URLConstants.URL_ENDPOINT + URLConstants.URL_CREATE_TRACKING;
        httpRequest.tag = URLConstants.URL_CREATE_TRACKING;
        httpRequest.method = HttpRequest.METHOD_POST;
        httpRequest.requestDelegate = requestDelegate;
        httpRequest.delegate = this;
        httpRequest.setKey(API_KEY);

        Tracking trackingObject = new Tracking();
        trackingObject.slug = slug;
        trackingObject.tracking_number = trackingNo;
        trackingObject.title = title;
        trackingObject.smses = smses;
        trackingObject.emails = emails;
        trackingObject.order_id = orderId;
        trackingObject.order_id_path = orderIdPath;
        trackingObject.custom_fields = customFields;

        httpRequest.setTrackingObjectParams(trackingObject);
        this.doHttpRequest(httpRequest);
    }

    /**
     * Send an HTTP GET request to get tracking results of a single tracking
     * @param slug Unique code of courier
     * @param trackingNo Tracking number of a shipment
     * @param requestDelegate interface reference
     */
    public void requestSingleTracking (String slug, String trackingNo, ApiRequestDelegate requestDelegate, String API_KEY){

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.url = URLConstants.URL_ENDPOINT + String.format(URLConstants.URL_GET_SINGLE_TRACKING, slug, trackingNo);
        httpRequest.tag = URLConstants.URL_GET_SINGLE_TRACKING;
        httpRequest.method = HttpRequest.METHOD_GET;
        httpRequest.requestDelegate = requestDelegate;
        httpRequest.delegate = this;
        httpRequest.setKey(API_KEY);

        this.doHttpRequest(httpRequest);
    }

    /**
     * Makes the HTTP Request
     * @param httpRequest HTTP Request object
     */
    private void doHttpRequest(HttpRequest httpRequest) {
        httpRequest.doRequest();
    }

    /**
     *
     * @param httpRequest the original http request that was made
     */
    @Override
    public void requestCompleted(HttpRequest httpRequest) {

        if (httpRequest.requestDelegate != null){
            ApiResultWithValueMap apiResultWithValueMap = this.parseSuccessFailResult(httpRequest.responseString);

            ApiResult apiResult = apiResultWithValueMap.apiResult;

            if (!apiResult.success){
                httpRequest.requestDelegate.apiCompleted(apiResult, httpRequest);
                return;
            }

            JSONObject valueJson = apiResultWithValueMap.valueJson;

            if (URLConstants.URL_CREATE_TRACKING.equals(httpRequest.tag)) {
                apiResult.valueObject = this.parseTracking(valueJson);
            }
            else if (URLConstants.URL_GET_SINGLE_TRACKING.equals(httpRequest.tag)) {
                apiResult.valueObject = this.parseTracking(valueJson);
            }

            httpRequest.requestDelegate.apiCompleted(apiResult, httpRequest);
        }
    }

    /**
     * Parses the JSON response into a Tracking object
     * @param valueJson the JSON response
     * @return Tracking object
     */
    private Tracking parseTracking (JSONObject valueJson) {

        if (valueJson.has("tracking")) {

            JSONObject jsonObject = valueJson.optJSONObject("tracking");
            Tracking tracking = new Tracking();

            tracking.id = jsonObject.optString("id");

            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            try {
                tracking.createdAt = df1.parse(jsonObject.optString("created_at"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tracking.tracking_number = jsonObject.optString("tracking_number");
            tracking.trackingAccountNo = jsonObject.optString("tracking_account_number");
            tracking.trackingPostalCode = jsonObject.optString("tracking_postal_code");
            tracking.trackingShipDate = jsonObject.optString("tracking_postal_code");
            tracking.slug = jsonObject.optString("slug");
            tracking.active = jsonObject.optBoolean("active");

            JSONObject customFields = jsonObject.optJSONObject("custom_fields");
            if (customFields != null) {
                tracking.custom_fields = new CustomFields();
                tracking.custom_fields.product_price = customFields.optString("product_price");
                tracking.custom_fields.product_name = customFields.optString("product_name");
            }

            tracking.customerName = jsonObject.optString("customer_name");
            tracking.destinationCountryIso3 = jsonObject.optString("destination_country_iso3");

            tracking.emails = new ArrayList<>();
            JSONArray emails = jsonObject.optJSONArray("emails");
            for (int i = 0; i<emails.length(); i++) {
                tracking.emails.add(emails.optString(i));
            }

            tracking.expectedDelivery = jsonObject.optString("expected_delivery");
//            tracking.note
            tracking.order_id = jsonObject.optString("order_id");
            tracking.order_id_path = jsonObject.optString("order_id_path");
            tracking.originCountryIso3 = jsonObject.optString("origin_country_iso3");
            tracking.shipmentPackageCount = jsonObject.optInt("shipment_package_count");
            tracking.shipmentType = jsonObject.optString("shipment_type");
            tracking.signedBy = jsonObject.optString("signed_by");

            tracking.smses = new ArrayList<>();
            JSONArray smses = jsonObject.optJSONArray("smses");
            for (int i = 0; i<smses.length(); i++) {
                tracking.smses.add(smses.optString(i));
            }

//            tracking.source
            tracking.tag = jsonObject.optString("tag");
//            tracking.title
            tracking.trackedCount = jsonObject.optInt("tracked_count");
            tracking.uniqueToken = jsonObject.optString("unique_token");

            JSONArray checkpoints = jsonObject.optJSONArray("checkpoints");
            tracking.checkpoints = new ArrayList<>();
            for (int i=0; i<checkpoints.length(); i++) {

                Checkpoint checkpoint = new Checkpoint();
                try {
                    checkpoint.createdAt = df1.parse(((JSONObject) checkpoints.get(i)).optString("created_at"));
                    checkpoint.slug = ((JSONObject) checkpoints.get(i)).optString("slug");
                    checkpoint.checkpointTime = ((JSONObject) checkpoints.get(i)).optString("checkpoint_time");
                    checkpoint.city = ((JSONObject) checkpoints.get(i)).optString("city");
                    checkpoint.countryIso3 = ((JSONObject) checkpoints.get(i)).optString("country_iso3");
                    checkpoint.countryName = ((JSONObject) checkpoints.get(i)).optString("country_name");
                    checkpoint.message = ((JSONObject) checkpoints.get(i)).optString("message");
                    checkpoint.state = ((JSONObject) checkpoints.get(i)).optString("state");
                    checkpoint.tag = ((JSONObject) checkpoints.get(i)).optString("tag");
                    checkpoint.zip = ((JSONObject) checkpoints.get(i)).optString("zip");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                tracking.checkpoints.add(checkpoint);
            }

            return tracking;
        }

        return null;
    }

    /**
     * Determines whether the http request was valid or not using the http response codes
     * @param responseString JSON response
     * @return ApiResultWithValueMap
     */
    private ApiResultWithValueMap parseSuccessFailResult(String responseString) {
        ApiResultWithValueMap result = new ApiResultWithValueMap();

        ApiResult apiResult = new ApiResult();
        apiResult.success = false;

        result.apiResult = apiResult;

        try {
            JSONObject jsonObject = new JSONObject(responseString);

            if (jsonObject.has("meta")){
                JSONObject meta = jsonObject.optJSONObject("meta");
                int code = meta.optInt("code");

                if (code >= 400){
                    apiResult.success = false;
                    apiResult.failReason = meta.optString("message");
                    apiResult.failType = meta.optString("type");
                }
                else {
                    apiResult.success = true;
                }

            }

            if (jsonObject.has("data")){
                result.valueJson = jsonObject.optJSONObject("data");
            }

            return result;
        } catch (JSONException e) {
            return result;
        }
    }

    /**
     * Incase of a request fail, it displays a message explaining the reason of the request failure
     * @param reason Request failure reason
     * @param title Request failure title
     * @param activity The activity from which the request is called
     */
    public static void handleMessageForReason(String reason, String title, final Activity activity) {
        if (title == null) {
            title = "";
        }

        if (reason != null ){
            displayMessage(reason, title, activity);
        }
    }

    /**
     * Constructs an Alert Dialog displaying the reason of request failure
     * @param message Request failure reason
     * @param title Request failure title
     * @param activity The activity from which the request is called
     */
    private static void displayMessage(String message, String title, final Activity activity) {
        // Need to use Activity.this (Cannot use the Context to build the Dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        alertDialog.show();
    }

    /**
     * A custom data-structure that contains the JSON response and its success/failure status
     */
    private class ApiResultWithValueMap{
        public ApiResult apiResult;
        public JSONObject valueJson;
    }
}