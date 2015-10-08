package com.clear.faun.imgurredditapp.Model;

import io.realm.RealmObject;

/**
 * Created by SpencerDepas on 10/6/15.
 */
public class SearchedSubredditList extends RealmObject {



    private String searchedSubreddit;



    public String getSearchedSubreddit() {
        return searchedSubreddit;
    }

    public void setSearchedSubreddit(String searchedSubreddit) {
        this.searchedSubreddit = searchedSubreddit;
    }








}