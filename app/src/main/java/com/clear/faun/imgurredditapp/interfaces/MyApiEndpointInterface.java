package com.clear.faun.imgurredditapp.interfaces;




import com.clear.faun.imgurredditapp.model.ImgurContainer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by SpencerDepas on 4/1/16.
 */
public interface MyApiEndpointInterface {





    @Headers("Authorization: Client-ID 3d5419ceff3c88f")
    @GET("/3/gallery/r/{subreddit}/time/0.json")//here is the other url part.best way is to start using /
    Call<ImgurContainer> getFeed(@Path("subreddit") String subreddit);




}
