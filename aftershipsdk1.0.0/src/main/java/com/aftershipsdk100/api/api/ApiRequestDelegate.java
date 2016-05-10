package com.aftershipsdk100.api.api;


/**
 * Functions as a bridge between an http request and api response.
 * Helps to get the api response for its corresponding http request
 **/
public interface ApiRequestDelegate {

    /**
     * Provides the response for the corresponding http request
     * @param apiResult contains the parsed http response
     * @param httpRequest the original http request that was made
     */
    void apiCompleted(ApiResult apiResult, HttpRequest httpRequest);
}
