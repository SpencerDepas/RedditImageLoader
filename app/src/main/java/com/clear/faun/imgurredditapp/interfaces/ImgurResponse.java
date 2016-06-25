package com.clear.faun.imgurredditapp.interfaces;

import com.clear.faun.imgurredditapp.model.ImgurContainer;

/**
 * Created by spencer on 9/2/2015.
 */
public interface ImgurResponse {
    void processFinish(ImgurContainer imgurContainers);
    void processFailed(String error);
}