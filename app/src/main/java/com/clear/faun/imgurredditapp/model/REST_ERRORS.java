package com.clear.faun.imgurredditapp.model;

/**
 * Created by SpencerDepas on 6/25/16.
 */
public class REST_ERRORS {

    public static final String IMGUR_OVER_CAPACITY = "retrofit.RetrofitError: 500 Unknown Error";
    public static final String RETROFIT_NO_CONNECTION = "retrofit.RetrofitError: failed to connect to api.imgur.com/199.27.76.193 (port 443) after 15000ms: isConnected failed: ENETUNREACH (Network is unreachable)";
    public static final String RETROFIT_NO_HOST = "retrofit.RetrofitError: Unable to resolve host \"api.imgur.com\": No address associated with hostname";
}
