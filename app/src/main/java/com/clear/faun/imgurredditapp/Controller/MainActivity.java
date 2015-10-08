package com.clear.faun.imgurredditapp.Controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import android.view.MenuItem;
import android.widget.ProgressBar;


import com.bumptech.glide.Glide;
import com.clear.faun.imgurredditapp.Model.CallAndParse;
import com.clear.faun.imgurredditapp.Model.ImgurContainer;
import com.clear.faun.imgurredditapp.Model.ImgurResponse;
import com.clear.faun.imgurredditapp.Model.RealmDatabase;
import com.clear.faun.imgurredditapp.Model.SearchedSubredditList;
import com.clear.faun.imgurredditapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements ImgurResponse,
        NavigationViewFragment.NavigationDrawerCallbacks{


    private ArrayList<String> searchedSubreddits = new ArrayList<>();
    private Context mContext;
    private CallAndParse callAndParse;
    private String curruntSubreddit = "NYCSTREETART";
    private SharedPreferences pref;
    private Gson gson;
    private RealmDatabase database;

    private NavigationViewFragment mNavigationDrawerFragment;
    //@Bind(R.id.nav_header_tittle) TextView navViewTextView;
    @Bind(R.id.drawer_layout)  DrawerLayout mDrawerLayout;

    @Bind(R.id.main_content)  View view;
    @Bind(R.id.backdrop)  ImageView bannerImageView;
    @Bind(R.id.toolbar)  Toolbar toolbar;
    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.rv)  RecyclerView rv;
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    //@Bind(R.id.change_sub)  View alertDialogView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.test);
        ButterKnife.bind(this);
        Log.i("MyMainActivity", "onCreate");


        mContext = getApplicationContext();

        //this is the temp fix that allows the tittle to be changed
        collapsingToolbar.setTitleEnabled(false);


        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //nav draw stuff
        mNavigationDrawerFragment = (NavigationViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_fragment);


        mNavigationDrawerFragment.setUp(
                R.id.navigation_fragment,
                (DrawerLayout) findViewById(R.id.drawer_layout));





        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        if(pref.getString("saved_subreddit", null) != null){
            curruntSubreddit = pref.getString("saved_subreddit", null);
        }

        gson = new Gson();


        Log.i("MyMainActivity", "searchedSubreddits : size " + searchedSubreddits.size());

        if(savedInstanceState == null){

            Glide.with(mContext)
                    .load(R.drawable.defualt_banner_image)
                    .centerCrop()
                    .crossFade()
                    .into(bannerImageView);

        }





        view.setBackgroundColor(Color.WHITE);


        rv.setLayoutManager(new GridLayoutManager(this, 2));

        //saves and loads data
        database = new RealmDatabase(mContext);


    }




    @Override
    public void onResume(){
        super.onResume();
        Log.i("MyMainActivity", "onResume");

        Log.i("MyMainActivity", "curruntSubreddit: " + curruntSubreddit);


        database.loadData(searchedSubreddits);
        String[] stockArr = new String[searchedSubreddits.size()];
        stockArr = searchedSubreddits.toArray(stockArr);
        mNavigationDrawerFragment.updateDraw(stockArr);

        apiCall(curruntSubreddit);
    }



    @Override
    public void onPause(){
        super.onPause();
        Log.i("MyMainActivity", "onPause");


        SharedPreferences.Editor editor = pref.edit();
        editor.putString("saved_subreddit", curruntSubreddit);
        editor.apply();


        //saveData();
        database.saveData(searchedSubreddits);



    }


    @OnClick(R.id.fab) void fabOnClick() {
        Log.i("MyMainActivity", "fabOnClick: " );
        //DIALOG


        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View alertDialogView = li.inflate(R.layout.change_subreddit, null);


        final EditText subredditEditText = (EditText) alertDialogView
                .findViewById(R.id.editTextDialogUserInput);


        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
        alertDialogBuilder.setView(alertDialogView);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text


                        curruntSubreddit = subredditEditText.getText().toString()
                                .toUpperCase()
                                .replaceAll(" ", "");

                        if (curruntSubreddit.length() > 1) {
                            loadingSwitch();
                            apiCall(curruntSubreddit);
                        } else {
                            Snackbar.make(view, "Please try again", Snackbar.LENGTH_LONG).show();
                        }


                        Log.i("MyMainActivity", "imgurContainers " + subredditEditText.getText().toString());


                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", null);
        alertDialogBuilder.show();

    }


    private void apiCall(String subreddit){
        callAndParse = new CallAndParse(subreddit);
        callAndParse.delegate = MainActivity.this;
    }

    private void loadingSwitch(){
        if(rv.getVisibility()== View.VISIBLE){
            Log.i("MyMainActivity", "rv.getVisibility()== View.VISIBLE " );
            progressBar.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            rv.setVisibility(View.VISIBLE);
        }

    }



    @Override
    public void processFinish(ImgurContainer imgurContainers) {
        Log.i("MyMainActivity", "processFinish");
        Log.i("MyMainActivity", "imgurContainers size" + imgurContainers.getImgurData().size());

        //allways want to be true
        rv.setVisibility(View.VISIBLE);

        if (imgurContainers.getImgurData().size() == 0) {

            // nothing returned
            Snackbar.make(view, "Try a diferent curruntSubreddit", Snackbar.LENGTH_LONG).show();


        } else {

            //this will be saved to realm onpause

            if(searchedSubreddits.size() > 0){
                Log.i("MyMainActivity", "searchedSubreddits.size() > 0" );
                if(!searchedSubreddits.get(searchedSubreddits.size() - 1).equals(curruntSubreddit)){
                    Log.i("MyMainActivity", "!searchedSubreddits.get(searchedSubreddits.size() - 1).equals(curruntSubreddit)" );
                    searchedSubreddits.add(curruntSubreddit);
                }

            }else{
                Log.i("MyMainActivity", "else " + curruntSubreddit);
                searchedSubreddits.add(curruntSubreddit);
            }






            Log.i("MyMainActivity", "imgurContainers curruntSubreddit " + curruntSubreddit);

            Glide.clear(view);
            imgurContainers.setSubRedditName(curruntSubreddit);
            //imageAdapter = null;



            toolbar.setTitle("/R/" + imgurContainers.getSubRedditName());


            Glide.with(mContext)
                    .load(imgurContainers.getImgurData().get(0).getLink())
                    .centerCrop()
                    .crossFade()
                    .into(bannerImageView);

            //dont want it to load in grid view
            imgurContainers.getImgurData().remove(0);


            RVAdapter rvAdapter = new RVAdapter(imgurContainers, mContext);
            rv.setAdapter(rvAdapter);

            progressBar.setVisibility(view.INVISIBLE);


        }

    }




    @Override
    public void processFailed() {
        Log.i("MyMainActivity", "processFailed");
        Snackbar.make(view, "Try a diferent curruntSubreddit", Snackbar.LENGTH_LONG).show();
        rv.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putString("SUBREDDIT", curruntSubreddit);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        curruntSubreddit = savedInstanceState.getString("SUBREDDIT");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MyMainActivity", "item " + item.getItemId());

        if (item.getItemId() == android.R.id.home) {
            Log.d("MyMainActivity", "item.getItemId() == android.R.id.home");

            mDrawerLayout.openDrawer(GravityCompat.START);
            // Set up the drawer.

            //navViewTextView.setText("/R/" + curruntSubreddit);
            //Snackbar.make(view, "Ah ah ah, you didn't say the magic word", Snackbar.LENGTH_LONG).show();
            return true;
        } else {
            Log.d("MyMainActivity", "Logout");

            return true;
        }


    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Log.d("MyMainActivity", "position: " + position);
//        Snackbar.make(view, "position: " + searchedSubreddits.get(position),
//                Snackbar.LENGTH_SHORT).show();
        apiCall(searchedSubreddits.get(position));
        curruntSubreddit = searchedSubreddits.get(position);
    }
}
