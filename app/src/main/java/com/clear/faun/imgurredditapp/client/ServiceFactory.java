package com.clear.faun.imgurredditapp.client;

import retrofit.RestAdapter;

/**
 * Created by SpencerDepas on 6/25/16.
 */
public class ServiceFactory {

    public static <T> T createRetrofitService(final Class<T> clazz, final String endPoint) {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(endPoint)
                .build();
        T service = restAdapter.create(clazz);

        return service;
    }


}
