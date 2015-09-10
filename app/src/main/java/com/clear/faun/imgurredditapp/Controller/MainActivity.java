package com.clear.faun.imgurredditapp.Controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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
    private String subreddit = "nyc";
    private CollapsingToolbarLayout collapsingToolbar;


    @Bind(R.id.main_content)  View view;
    @Bind(R.id.backdrop)  ImageView bannerImageView;
    @Bind(R.id.toolbar)  Toolbar toolbar;
    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.rv)  RecyclerView rv;
    @Bind(R.id.fab) FloatingActionButton fab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.i("MyMainActivity", "onCreate   ");


        mContext = getApplicationContext();

        subreddit = "nycstreetart";

        setSupportActionBar(toolbar);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("/R/" + subreddit);


        ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        Glide.with(mContext)
                .load(R.drawable.defualt_banner_image)
                .centerCrop()
                .crossFade()
                .into(bannerImageView);


        Log.i("MyMainActivity", "subreddit " + subreddit);


        view.setBackgroundColor(Color.WHITE);


        rv.setLayoutManager(new GridLayoutManager(this, 2));

        callAndParse = new CallAndParse(subreddit);
        callAndParse.delegate = MainActivity.this;



    }

    @OnClick(R.id.fab) void fabOnClick() {

        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.change_sub, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText subredditEditText = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("CONFIRM",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text

                                progressBar.setVisibility(View.VISIBLE);
                                rv.setVisibility(View.GONE);

                                subreddit = subredditEditText.getText().toString()
                                        .toUpperCase()
                                        .replaceAll(" ", "");

                                callAndParse = new CallAndParse(subreddit);
                                callAndParse.delegate = MainActivity.this;

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


    @Override
    public void processFinish(ImgurContainer imgurContainers) {
        Log.i("MyMainActivity", "processFinish");
        Log.i("MyMainActivity", "imgurContainers size" + imgurContainers.getImgurData().size());

        rv.setVisibility(View.VISIBLE);

        if (imgurContainers.getImgurData().size() == 0) {

            Snackbar.make(view, "Try a diferent subreddit", Snackbar.LENGTH_LONG).show();


        } else {


            Glide.clear(view);
            imgurContainers.setSubRedditName(subreddit);
            //imageAdapter = null;
            Log.i("MyMainActivity", "imgurContainers subreddit" + subreddit);


            setToolBarTitle(imgurContainers.getSubRedditName());

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
        Log.i("MyMainActivity", "imgurContainers size" + imgurContainers.getImgurData().size());
    }


    public void setToolBarTitle(String subredit) {
        Log.i("MyMainActivity", "imgurContainers SETTING TITLE NOW");
        /*if(collapsingToolbar.getTitle().toString()){
            Log.i("MyMainActivity", "collapsingToolbar.getTitle().toString(): "
                    + collapsingToolbar.getTitle().toString());
        }*/

        collapsingToolbar.setTitle("/R/" + subredit);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);

        toolbar.setTitle("/R/" + subredit);
        ab.setTitle("/R/" + subredit);

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
