package com.clear.faun.imgurredditapp.Model;

import com.clear.faun.imgurredditapp.R;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;

/**
 * Created by spencer on 9/2/2015.
 */
public interface ImgurAPI {



    @Headers("Authorization: Client-ID API")
    @GET("/3/gallery/r/{subreddit}/time/0.json")//here is the other url part.best way is to start using /
    public void getFeed(@Path("subreddit") String subreddit, Callback<ImgurContainer> response);

}
