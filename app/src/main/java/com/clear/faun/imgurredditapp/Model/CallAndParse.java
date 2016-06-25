package com.clear.faun.imgurredditapp.Model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



/**
 * Created by spencer on 9/2/2015.
 */
public class CallAndParse {


    public ImgurResponse delegate = null;

    private final String API = "https://api.imgur.com";

    String subreddit;

//    public CallAndParse(String subreddit){
//
//        this.subreddit = subreddit;
//        final String FINAL_SUBREDDIT = subreddit;
//        Log.i("MyCallAndParse", "CallAndParse   " );
//        Log.i("MyCallAndParse", "subreddit : " + subreddit );
//        Gson gson = new GsonBuilder()
//                .create();
//
//
//        //Retrofit
//        RestAdapter restAdapter = new RestAdapter.Builder()
//                .setEndpoint(API)
//                .setLogLevel(RestAdapter.LogLevel.FULL)
//                .setConverter(new GsonConverter(gson))
//                .build();
//
//
//
//        ImgurAPI git = restAdapter.create(ImgurAPI.class);
//
//
//        git.getFeed(subreddit, new Callback<ImgurContainer>() {
//
//
//
//            @Override
//            public void success(ImgurContainer imgurContainers, Response response) {
//                Log.i("MyCallAndParse", "success");
//
//                Log.i("MyCallAndParse", "dC " + imgurContainers.getImgurData().size());
//
//                imgurContainers.setSubRedditName(FINAL_SUBREDDIT);
//                for(int i = 0; i < imgurContainers.getImgurData().size(); i ++){
//                    //Log.i("MyCallAndParse", "dC " + imgurContainers.getImgurData().get(i).getTitle());
//                    if(imgurContainers.getImgurData().get(i).isAnimated()){
//                        imgurContainers.getImgurData().remove(i);
//                    }
//                }
//
//                delegate.processFinish(imgurContainers);
//
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Log.i("MyCallAndParse", "error  " + error.toString());
//                delegate.processFailed(error.toString());
//            }
//
//
//        });
//
//
//
//    }

}
