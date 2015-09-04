package com.clear.faun.imgurredditapp.Model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by spencer on 9/2/2015.
 */
public class CallAndParse {


    public ImgurResponse delegate = null;

    private final String API = "https://api.imgur.com";

    String subreddit;

    public CallAndParse(String subreddit){

        this.subreddit = subreddit;

        Log.i("MyCallAndParse", "CallAndParse   " );
        Gson gson = new GsonBuilder()
                .create();


        //Retrofit
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API)
                //.setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .build();



        ImgurAPI git = restAdapter.create(ImgurAPI.class);


        git.getFeed(subreddit, new Callback<ImgurContainer>() {



            @Override
            public void success(ImgurContainer imgurContainers, Response response) {
                Log.i("MyCallAndParse", "success");

                Log.i("MyCallAndParse", "dC " + imgurContainers.getImgurData().size());


                delegate.processFinish(imgurContainers);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("ParseImgurLink", "error  " + error);
            }


        });



    }

}
