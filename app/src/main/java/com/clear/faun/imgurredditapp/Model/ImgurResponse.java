package com.clear.faun.imgurredditapp.Model;

import java.util.ArrayList;

/**
 * Created by spencer on 9/2/2015.
 */
public interface ImgurResponse {
    void processFinish(ImgurContainer imgurContainers);

    void processFailed();
}