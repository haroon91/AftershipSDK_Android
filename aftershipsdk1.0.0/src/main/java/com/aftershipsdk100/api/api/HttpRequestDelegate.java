package com.aftershipsdk100.api.api;

/**
 * Functions as a bridge between http request and the json response
 */
public interface HttpRequestDelegate {

    /**
     * Notifes once the http request is finished so that the corresponding response is processed
     * @param httpRequest the original http request that was made
     */
    void requestCompleted(HttpRequest httpRequest);
}
