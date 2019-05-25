package com.bsecure.scsm_mobile.callbacks;

public interface HttpHandler {

	public void onResponse(Object results, int requestType);

	public void onFailure(String errorCode, int requestType);

}