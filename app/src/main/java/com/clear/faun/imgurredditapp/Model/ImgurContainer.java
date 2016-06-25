package com.clear.faun.imgurredditapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by spencer on 9/2/2015.
 */
public class ImgurContainer {

    //public List<ImgurData> data;

    private String subRedditName = "nycstreetart";

    public String getSubRedditName(){
        return subRedditName.toUpperCase();
    }

    public void setSubRedditName(String subRedditName){
        this.subRedditName = subRedditName;
    }

    @SerializedName(value="data")
    private List<ImgurData> data;

    public void setImgurData(List<ImgurData> data) {
        this.data = data;
    }

    public List<ImgurData> getImgurData() {
        return data;
    }
}
