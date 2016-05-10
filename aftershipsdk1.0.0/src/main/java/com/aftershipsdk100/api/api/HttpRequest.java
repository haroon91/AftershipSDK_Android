package com.aftershipsdk100.api.api;

import android.os.AsyncTask;
import android.util.Log;

import com.aftershipsdk100.BuildConfig;
import com.aftershipsdk100.api.model.Tracking;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Establishes connnection with the server in order to send http requests
 */
public class HttpRequest {

    private String key;

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    public String url = "";
    public String method = METHOD_GET;
    public String tag = "";

    public HttpRequestDelegate delegate;
    public ApiRequestDelegate requestDelegate;

    public String responseString;

    private Tracking trackingObject = new Tracking();

    private Map<String,String> postMap = new HashMap<>();

    public void putPostParams(String key, String value) {
        postMap.put(key, value);
    }

    public void setKey(String key){
        this.key = key;
    }

    public void setTrackingObjectParams(Tracking trackingObject){
        this.trackingObject = trackingObject;
    }

    /**
     * Initiates an async task thread for communication with the server
     */
    public void doRequest() {
        new HttpRequestTask().execute();
    }

    /**
     * initTrust is to allow self-signed certificate
     */
    public static void initTrust() {

        try {
            //step 1
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            }, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());


            //step 2
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }catch (KeyManagementException | NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Aysnc task class for communication between client and server
     */
    private class HttpRequestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String result = "";
            try {
                result =  requestUrl();
            } catch (Exception e) {
                Log.d("SHO", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            responseString = result;
            if (delegate != null) {
                delegate.requestCompleted(HttpRequest.this);
            }
        }

        private String requestUrl() throws Exception {
            InputStream is = null;

            try {
                URL urlObject = new URL(url);

//                Log.v("URL", String.valueOf(url));

                HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();

                conn.setReadTimeout(10000); //milliseconds
                conn.setConnectTimeout(15000); //milliseconds
                conn.setRequestMethod(method);
                conn.setDoInput(true);
                conn.setRequestProperty("aftership-user-agent", "aftership-android-sdk " + BuildConfig.VERSION_NAME);
                conn.setRequestProperty("aftership-api-key", key);
                conn.setRequestProperty("Content-Type", "application/json");

                if (METHOD_POST.equals(method)) {
                    conn.setDoOutput(true);

                    String query = makeQueryString();

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.writeBytes(query);
                    wr.flush();
                    wr.close();
                }

                conn.connect();

                int responseCode;

                try {
                    // Will throw IOException if server responds with 401.
                    responseCode = conn.getResponseCode();
                } catch (IOException e) {
                    // Will return 401, because now connection has the correct internal state.
                    responseCode = conn.getResponseCode();
                }

                Log.d("SHO", "responseCode = " + responseCode);

                if (responseCode >= 400) {
                    is = conn.getErrorStream();

                }else{
                    is = conn.getInputStream();
                }

                return readStreamToString(is);
            } finally {
                if (is!=null) {
                    is.close();
                }
            }

        }

        /**
         * Build a request json string out of key / values
         * @return String
         * @throws JSONException
         */
        private String makeQueryString() throws JSONException {
            if (trackingObject == null) {
                return "";
            }

            JSONObject jsonParam = new JSONObject();
            JSONObject jTObject = new JSONObject();
            jTObject.put("slug",trackingObject.slug);
            jTObject.put("tracking_number",trackingObject.tracking_number);
            jTObject.put("title",trackingObject.title);
            jTObject.put("smses",trackingObject.smses);
            jTObject.put("emails",trackingObject.emails);
            jTObject.put("order_id",trackingObject.order_id);
            jTObject.put("order_id_path",trackingObject.order_id_path);

            JSONObject customFields = new JSONObject();
            customFields.put("product_name",trackingObject.custom_fields.product_name);
            customFields.put("product_price",trackingObject.custom_fields.product_price);

            jTObject.put("custom_fields",customFields);
            jsonParam.put("tracking",jTObject);

            return jsonParam.toString();
        }

        public String readStreamToString(InputStream stream) throws IOException {
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

            StringBuilder builder = new StringBuilder();
            char[] chars = new char[4*1024];
            int len;

            while ((len = reader.read(chars)) >= 0) {
                builder.append(chars, 0, len);
            }

            return builder.toString();
        }

    }
}
