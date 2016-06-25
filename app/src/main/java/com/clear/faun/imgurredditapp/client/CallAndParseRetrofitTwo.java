package com.clear.faun.imgurredditapp.client;

import android.util.Log;

import com.clear.faun.imgurredditapp.model.ImgurContainer;
import com.clear.faun.imgurredditapp.interfaces.ImgurResponse;
import com.clear.faun.imgurredditapp.interfaces.MyApiEndpointInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by SpencerDepas on 4/1/16.
 */
public class CallAndParseRetrofitTwo{

    public static final String IMGUR_API = "https://api.imgur.com/";

    public ImgurResponse delegate = null;

    private final String API = "https://api.imgur.com";

    String subreddit;

        public CallAndParseRetrofitTwo(String subreddit){

            this.subreddit = subreddit;
            final String FINAL_SUBREDDIT = subreddit;
            Log.i("CallAndParseRetrofitTwo", "CallAndParse   ");
            Log.i("CallAndParseRetrofitTwo", "subreddit : " + subreddit );


            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();


            // Trailing slash is needed

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(IMGUR_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MyApiEndpointInterface apiService =
                    retrofit.create(MyApiEndpointInterface.class);



            Call<ImgurContainer> call = apiService.getFeed(subreddit);
            call.enqueue(new Callback<ImgurContainer>() {
                @Override
                public void onResponse(Call<ImgurContainer> call, Response<ImgurContainer> response) {
                    int statusCode = response.code();
                    ImgurContainer imgurContainers = response.body();
                    Log.i("CallAndParseRetrofitTwo", "onResponse   ");

                    Log.i("CallAndParseRetrofitTwo", "ImgurContainer size :" + imgurContainers.getImgurData().size());


                    imgurContainers.setSubRedditName(FINAL_SUBREDDIT);
                    for(int i = 0; i < imgurContainers.getImgurData().size(); i ++){
                        //Log.i("MyCallAndParse", "dC " + imgurContainers.getImgurData().get(i).getTitle());
                        if(imgurContainers.getImgurData().get(i).isAnimated()){
                            imgurContainers.getImgurData().remove(i);
                        }
                    }

                    delegate.processFinish(imgurContainers);
                }

                @Override
                public void onFailure(Call<ImgurContainer> call, Throwable t) {
                    // Log error here since request failed
                    Log.i("CallAndParseRetrofitTwo", "onFailure   ");
                }
            });




        }

}

