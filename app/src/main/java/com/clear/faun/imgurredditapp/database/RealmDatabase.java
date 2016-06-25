package com.clear.faun.imgurredditapp.database;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by SpencerDepas on 10/7/15.
 */
public class RealmDatabase {


    private ArrayList<String> searchedSubreddits = new ArrayList<>();
    private Context mContext;

    private Realm realm;
    private RealmResults<SearchedSubredditList> query;



    public RealmDatabase( Context mContext){
        this.mContext = mContext;
        Log.i("MyRealmDatabase", "RealmDatabase");
    }

    public void saveData(ArrayList<String> searchedSubreddits) {
        Log.i("MyRealmDatabase", "saveDatadata");

        this.searchedSubreddits = searchedSubreddits;



        deleteOldDatabase();

        //saves information to database
        for(int i = 0; i < searchedSubreddits.size(); i ++){
            Log.i("MyRealmDatabase", "saveDatadata searchedSubreddits : " + searchedSubreddits.get(i));


            realm.beginTransaction();

            SearchedSubredditList searchedSubredditList = realm.createObject(SearchedSubredditList.class);
            searchedSubredditList.setSearchedSubreddit(searchedSubreddits.get(i));

            realm.commitTransaction();



        }

    }


    public void loadData(ArrayList<String> searchedSubreddits) {
        Log.i("MyRealmDatabase", "loadDatadata");
        realm = Realm.getInstance(mContext);

        this.searchedSubreddits = searchedSubreddits;

        query = realm.where(SearchedSubredditList.class)
                .findAll();

        //json body convert to array
        try{


            searchedSubreddits.clear();
            Log.i("MyRealmDatabase", "searchedSubreddits size"  + searchedSubreddits.size());

            for(int i = 0 ; i < query.size(); i ++){
                searchedSubreddits.add(query.get(i).getSearchedSubreddit());
                Log.i("MyRealmDatabase", "loadDatadata searchedSubreddits.get(i); " + searchedSubreddits.get(i));
            }


            Log.i("MyRealmDatabase", "loadDatadata searchedSubredditsz: " + searchedSubreddits.size());
        }catch (Exception e){
            Log.i("MyRealmDatabase", "loadDatadata Exception: e " + e.toString());
        }

    }

    private void deleteOldDatabase(){
        Realm realm = Realm.getInstance(mContext);
        realm.beginTransaction();

        realm.where(SearchedSubredditList.class)
                .findAll().clear();
        realm.commitTransaction();
    }

}
