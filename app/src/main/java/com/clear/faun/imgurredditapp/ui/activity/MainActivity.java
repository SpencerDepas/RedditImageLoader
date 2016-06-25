package com.clear.faun.imgurredditapp.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.clear.faun.imgurredditapp.adapter.RVAdapter;
import com.clear.faun.imgurredditapp.client.CallAndParseRetrofitTwo;
import com.clear.faun.imgurredditapp.ui.fragment.NavigationViewFragment;
import com.clear.faun.imgurredditapp.model.ImgurContainer;
import com.clear.faun.imgurredditapp.interfaces.ImgurResponse;
import com.clear.faun.imgurredditapp.database.RealmDatabase;
import com.clear.faun.imgurredditapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements ImgurResponse,
        NavigationViewFragment.NavigationDrawerCallbacks {

    private String loadSubreddit;
    private final String IMGUR_OVER_CAPACITY = "retrofit.RetrofitError: 500 Unknown Error";
    private final String RETROFIT_NO_CONNECTION = "retrofit.RetrofitError: failed to connect to api.imgur.com/199.27.76.193 (port 443) after 15000ms: isConnected failed: ENETUNREACH (Network is unreachable)";
    private final String RETROFIT_NO_HOST = "retrofit.RetrofitError: Unable to resolve host \"api.imgur.com\": No address associated with hostname";
    private ArrayList<String> searchedSubreddits = new ArrayList<>();
    private Context mContext;
    private CallAndParseRetrofitTwo callAndParse;
    private String curruntSubreddit = "NYCSTREETART";
    private SharedPreferences pref;
    private Gson gson;
    private RealmDatabase database;
    private RVAdapter rvAdapter;

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

    private ImgurContainer imgurContainers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.main_layout);
        ButterKnife.bind(this);
        Log.i("MyMainActivity", "onCreate");

        imgurContainers = null;

        mContext = getApplicationContext();

        //this is the temp fix that allows the tittle to be changed
        collapsingToolbar.setTitleEnabled(true);


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



        //change nav bar colour
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        }


        view.setBackgroundColor(Color.WHITE);


        rv.setLayoutManager(new GridLayoutManager(this, 2));

        //saves and loads data
        database = new RealmDatabase(mContext);

        rvAdapter = new RVAdapter();





    }





    @Override
    public void onResume(){
        super.onResume();
        Log.i("MyMainActivity", "onResume");

        Log.i("MyMainActivity", "curruntSubreddit: " + curruntSubreddit);


        if(database == null){
            Log.i("MyMainActivity", "database == null "  );
            database = new RealmDatabase(mContext);
        }
        database.loadData(searchedSubreddits);
        String[] stockArr = new String[searchedSubreddits.size()];
        stockArr = searchedSubreddits.toArray(stockArr);
        mNavigationDrawerFragment.updateDraw(stockArr);


        if( imgurContainers != null){

        }else{
            apiCall(curruntSubreddit);
        }




    }





    @Override
    public void onPause(){
        super.onPause();
        Log.i("MyMainActivity", "onPause");


        SharedPreferences.Editor editor = pref.edit();
        editor.putString("saved_subreddit", curruntSubreddit);
        editor.apply();






    }


    @OnClick(R.id.fab) void fabOnClick() {
        Log.i("MyMainActivity", "fabOnClick: ");
        //DIALOG


        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View alertDialogView = li.inflate(R.layout.change_subreddit_dialog, null);


        final EditText subredditEditText = (EditText) alertDialogView
                .findViewById(R.id.editTextDialogUserInput);



        final AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
        alertDialogBuilder.setView(alertDialogView);


        alertDialogBuilder.setPositiveButton("SEARCH",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // get user input and set it to result
                        // edit text

                        searchForSubredditDialog(subredditEditText.getText().toString()
                                .toUpperCase()
                                .replaceAll(" ", ""));
                    }

                });
        alertDialogBuilder.setNegativeButton("Cancel", null);
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        subredditEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.i("MyMainActivity", "onEditorAction");

                    alert.dismiss();
                    searchForSubredditDialog(subredditEditText.getText().toString()
                            .toUpperCase()
                            .replaceAll(" ", ""));


                    return true;
                }
                return false;
            }


        });

    }

    private void searchForSubredditDialog(String subreddit){



        if (subreddit.length() > 1) {
            loadingSwitch();
            apiCall(subreddit);
        } else {
            Snackbar.make(view, "Please try again", Snackbar.LENGTH_LONG).show();
        }



    }


    private void apiCall(String subreddit){
//        callAndParse = new CallAndParse(subreddit);
//


        CallAndParseRetrofitTwo callAndParse = new CallAndParseRetrofitTwo(subreddit);
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
        Log.i("MyMainActivity", "link 1" + imgurContainers.getImgurData().get(1).getLink());
        this.imgurContainers = imgurContainers;
        //allways want to be true
        rv.setVisibility(View.VISIBLE);

        if (imgurContainers.getImgurData().size() == 0) {

            // nothing returned
            Snackbar.make(view, "Try a diferent curruntSubreddit", Snackbar.LENGTH_LONG).show();


        } else {

            //this will be saved to realm onpause


            //Log.i("MyMainActivity", "else " + curruntSubreddit);



            //currunt subreddit is only updated is load subreddit is returned with sucsess
            curruntSubreddit = imgurContainers.getSubRedditName();




            Log.i("MyMainActivity", "imgurContainers curruntSubreddit " + curruntSubreddit);

            Glide.clear(view);
            imgurContainers.setSubRedditName(curruntSubreddit);
            //imageAdapter = null;


            collapsingToolbar.setTitle("/R/" + curruntSubreddit);
            toolbar.setTitle("/R/" + curruntSubreddit);


            Glide.with(mContext)
                    .load(imgurContainers.getImgurData().get(0).getLink())
                    .centerCrop()
                    .crossFade()
                    .into(bannerImageView);

            //dont want it to load in grid view
            imgurContainers.getImgurData().remove(0);


            MainActivity mApp = this;
            rvAdapter.setInfo(imgurContainers, mContext, mApp);
            rv.setAdapter(rvAdapter);

            addSubreddittoSavedList();


            progressBar.setVisibility(view.INVISIBLE);
            database.saveData(searchedSubreddits);
            String[] stockArr = new String[searchedSubreddits.size()];
            stockArr = searchedSubreddits.toArray(stockArr);
            mNavigationDrawerFragment.updateDraw(stockArr);




        }





    }



    private void addSubreddittoSavedList(){
        Log.i("MyMainActivity", "addSubreddittoSavedList " );
        if(searchedSubreddits.size() > 0){
            for(int i = 0 ; i < searchedSubreddits.size(); i ++){
                Log.i("MyMainActivity", "i " + i);
                Log.i("MyMainActivity", "else " + curruntSubreddit);
                if(searchedSubreddits.get(i).equals(curruntSubreddit)){
                    //if it is found then we break
                    break;

                }else{
                    if(i == searchedSubreddits.size() - 1){
                        searchedSubreddits.add(curruntSubreddit);
                        Log.i("MyMainActivity", "added curruntSubreddit " + curruntSubreddit);
                        break;
                    }
                }
            }
        }else{
            searchedSubreddits.add(curruntSubreddit);
            Log.i("MyMainActivity", "added curruntSubreddit " + curruntSubreddit);
        }

    }

    @Override
    public void processFailed(String error) {
        Log.i("MyMainActivity", "processFailed");
        Log.i("MyMainActivity", "error : " + error);



        if(error.equals(IMGUR_OVER_CAPACITY)){
            Snackbar.make(view, "R/Image is temporarily over capacity. Please try again", Snackbar.LENGTH_LONG).show();
        }else if(error.equals(RETROFIT_NO_CONNECTION) || error.equals(RETROFIT_NO_HOST)){
            Snackbar.make(view, "Not connected to the interweb", Snackbar.LENGTH_LONG).show();
        }else{
            Snackbar.make(view, "Please try a diferent curruntSubreddit", Snackbar.LENGTH_LONG).show();
        }

        rv.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
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
        mDrawerLayout.closeDrawers();
        apiCall(searchedSubreddits.get(position));
        curruntSubreddit = searchedSubreddits.get(position);
    }
}
