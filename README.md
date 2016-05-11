# Aftership Android SDK

This repository contains an interface for Aftership SDK that supports two types of API calls. 

The two API end points supported by this SDK are:

  1. <a href="https://www.aftership.com/docs/api/4/trackings/post-trackings">POST /trackings</a>
  2. <a href="https://www.aftership.com/docs/api/4/trackings/get-trackings-slug-tracking_number">GET /trackings/:slug/:tracking_number</a>

For more information about these API calls please follow the link on each of them.

# Usage

All the API calls are managed by the `ApiDataManager` class. This is a singleton.

    ApiDataManager apiDataManager = ApiDataManager.getInstance() // singleton instance
    
    

There are three steps involved that can help us to make the required API calls and get the response.

  1. First we implement the `ApiRequestDelegate` interface in our `Activity/Fragment` from where we are making the API call. The purpose of the `ApiRequestDelegate` is to enable the `Activity/Fragment` to receive asynchronous response on the API call that we made.
  2. Secondly we call the apporporiate method in `ApiDataManager` class that will make the http request.
  3. Finally we implement the `apiCompleted()` method in `ApiRequestDelegate` interface that helps us to get the response from the API call we made.
  
In case of  <a href="https://www.aftership.com/docs/api/4/trackings/post-trackings">POST /trackings</a> we use the `createTracking()` method since this API call creates a tracking.

      ApiDataManager.getInstance()
      .createTracking(slug, trackingNo, title, smses, emails, orderId, orderIdPath, customFields, apiRequestDelegate);
      
      
Then we implement the `apiCompleted()` as follows:

      @Override
      public void apiCompleted(ApiResult apiResult, HttpRequest httpRequest) {
      
       // Fail case - Our request 
        if (!apiResult.success){
            ApiDataManager.handleMessageForReason(apiResult.failReason, apiResult.failType, this);
            return;
        }
        
        //tracking class that stores our tracking object (response)
        Tracking tracking = (Tracking) apiResult.valueObject;
        
      }
  
  If we are making the API call inside an `Activity` our `Activity` class may look like this:
  
      public class MainActivity extends Activity implements ApiRequestDelegate {
      
        @Override
        protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);
          
          ...
          ApiDataManager.getInstance().createTracking("dhl","4134837490","title name",smses, emails, "ID 1234","http://www.www.com", customFields, this);
        }
        
         @Override
         public void apiCompleted(ApiResult apiResult, HttpRequest httpRequest) {

          // Fail case
          if (!apiResult.success){
              ApiDataManager.handleMessageForReason(apiResult.failReason, apiResult.failType, this);
              return;
          }
          
          Tracking tracking = (Tracking) apiResult.valueObject;
        }  
      }

In case of  <a href="https://www.aftership.com/docs/api/4/trackings/get-trackings-slug-tracking_number">GET /trackings/:slug/:tracking_number</a> we use the `requestSingleTracking()` method since this API call gets the tracking results of a single tracking.

      ApiDataManager.getInstance().requestSingleTracking(slug, trackingNo, apiRequestDelegate);

The procedure of getting the response is the same as is detailed above.

# Extensability

The SDK is developed in such an approach that it compatible with future growth in API calls.

The URLs of the API calls are stored in a public class `URLConstants`. The URL addresses of additional API calls can be added here and corresponding http request methods can be added in the `ApiDataManager` class. The `ApiDataManager` class also contains a couple of private methods that are used to parse the response `JSONObject` into customized objects.

e.g. a `GET` request for single tracking result (<a href="https://www.aftership.com/docs/api/4/trackings/get-trackings-slug-tracking_number">GET /trackings/:slug/:tracking_number</a>) returns a `JSONObject` that is parsed into a `Tracking` object with the help of the method:

      private Tracking parseTracking (JSONObject valueJson) { ... }

For additional API calls similar methods (if required) can be added.

# Documentation

For more information about classes and objects please refer to
<a href = "https://haroon91.github.io/AftershipSDK_Android/aftershipsdk1.0.0/javadoc/index.html">Javadocs</a>
