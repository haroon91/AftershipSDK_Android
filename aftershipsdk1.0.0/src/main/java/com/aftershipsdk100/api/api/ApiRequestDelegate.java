package com.aftershipsdk100.api.api;


public interface ApiRequestDelegate {
    void apiOnRequest();
    void apiCompleted(ApiResult apiResult, HttpRequest httpRequest);
}
