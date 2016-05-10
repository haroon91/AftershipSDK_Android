package com.aftershipsdk100.api.api;

/**
 * Stores the response and its success/failure status
 */
public class ApiResult {
    public boolean success = false;
    public String failReason;
    public String failType;
    public Object valueObject;
}
