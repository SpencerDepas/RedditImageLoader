package com.clear.faun.imgurredditapp.Controller;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import android.view.MenuItem;
import android.widget.ProgressBar;


import com.bumptech.glide.Glide;
import com.clear.faun.imgurredditapp.Model.CallAndParse;
import com.clear.faun.imgurredditapp.Model.ImgurContainer;
import com.clear.faun.imgurredditapp.Model.ImgurResponse;
import com.clear.faun.imgurredditapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements ImgurResponse {



    private Context mContext;
    private CallAndParse callAndParse;
    private ActionBar ab;
    private String subreddit = "NYCSTREETART";



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
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.i("MyMainActivity", "onCreate");


        mContext = getApplicationContext();

      

        setSupportActionBar(toolbar);

        collapsingToolbar.setTitle("/R/" + subreddit);


        ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null){

            Glide.with(mContext)
                    .load(R.drawable.defualt_banner_image)
                    .centerCrop()
                    .crossFade()
                    .into(bannerImageView);

        }





        view.setBackgroundColor(Color.WHITE);


        rv.setLayoutManager(new GridLayoutManager(this, 2));





    }


    @Override
    public void onResume(){
        super.onResume();
        Log.i("MyMainActivity", "onResume");

        Log.i("MyMainActivity", "subreddit: " + subreddit);
        setToolBarTitle(subreddit);
        apiCall(subreddit);
    }

    @OnClick(R.id.fab) void fabOnClick() {

        //DIALOG

        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View alertDialogView = li.inflate(R.layout.change_subreddit, null);

        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(MainActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(alertDialogView);

        final EditText subredditEditText = (EditText) alertDialogView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("CONFIRM",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text


                                loadingSwitch();

                                subreddit = subredditEditText.getText().toString()
                                        .toUpperCase()
                                        .replaceAll(" ", "");

                                apiCall(subreddit);

                                Log.i("MyMainActivity", "imgurContainers " + subredditEditText.getText().toString());


                            }
                        })
                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
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
            Snackbar.make(view, "Try a diferent subreddit", Snackbar.LENGTH_LONG).show();


        } else {


            Glide.clear(view);
            imgurContainers.setSubRedditName(subreddit);
            //imageAdapter = null;
            Log.i("MyMainActivity", "imgurContainers subreddit" + subreddit);


            setToolBarTitle(subreddit);

            //toolbar.setTitle(imgurContainers.getSubRedditName());


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




    public void setToolBarTitle(String subreddit) {
        Log.i("MyMainActivity", "imgurContainers SETTING TITLE NOW");
        /*if(collapsingToolbar.getTitle().toString()){
            Log.i("MyMainActivity", "collapsingToolbar.getTitle().toString(): "
                    + collapsingToolbar.getTitle().toString());
        }*/

        collapsingToolbar.setTitle("/R/" + subreddit);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);

        toolbar.setTitle("/R/" + subreddit);
        ab.setTitle("/R/" + subreddit);

      /*  collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);*/

        Log.i("MyMainActivity", "collapsingToolbar.getTitle().toString(): "
                + collapsingToolbar.getTitle().toString());

        Log.i("MyMainActivity", "toolbar.getTitle(): "
                + toolbar.getTitle());
    }

    @Override
    public void processFailed() {
        Log.i("MyMainActivity", "processFailed");
        Snackbar.make(view, "Try a diferent subreddit", Snackbar.LENGTH_LONG).show();
        rv.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putString("SUBREDDIT", subreddit);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        subreddit = savedInstanceState.getString("SUBREDDIT");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MyMainActivity", "item " + item.getItemId());

        if (item.getItemId() == android.R.id.home) {
            Log.d("MyMainActivity", "item.getItemId() == android.R.id.home");


            Snackbar.make(view, "Ah ah ah, you didn't say the magic word", Snackbar.LENGTH_LONG).show();
            return true;
        } else {
            Log.d("MyMainActivity", "Logout");

            return true;
        }


        //return super.onOptionsItemSelected(item);
    }


}
