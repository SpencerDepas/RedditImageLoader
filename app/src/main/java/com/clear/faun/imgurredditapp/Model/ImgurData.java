package com.clear.faun.imgurredditapp.model;

/**
 * Created by spencer on 9/2/2015.
 */
public class ImgurData {



    private String link;
    private String id;
    private boolean animated;
    private String title;



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public boolean isAnimated() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }



    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }


    public String getLink(){
        return link;
    }

    public void setLink(String link){
        this.link = link;
    }




}