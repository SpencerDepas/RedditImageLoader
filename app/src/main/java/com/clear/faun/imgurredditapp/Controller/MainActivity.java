package com.clear.faun.imgurredditapp.Controller;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.clear.faun.imgurredditapp.Model.CallAndParse;
import com.clear.faun.imgurredditapp.Model.ImgurContainer;
import com.clear.faun.imgurredditapp.Model.ImgurResponse;
import com.clear.faun.imgurredditapp.R;

public class MainActivity extends AppCompatActivity implements ImgurResponse {

    private Context mContext;
    //private GridView gridview;
    private ImageView imageView;
    private CallAndParse callAndParse;

    private String subreddit = "nycstreetart";
    private Toolbar toolbar;
    private ImageAdapter imageAdapter;
    private RecyclerView rv;


    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        Log.i("MyMainActivity", "onCreate   ");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("NYCSTREETART");
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("NYCSTREETART");
        //collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));


        rv = (RecyclerView)findViewById(R.id.rv);
        /*LinearLayoutManager llm = new LinearLayoutManager(mContext);
        rv.setLayoutManager(llm);*/
        rv.setLayoutManager(new GridLayoutManager(this, 2));


        mContext = getApplicationContext();

        view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.WHITE);

        //final String API_KEY = getApplicationContext().getString(R.string.API_KEY_IMGUR);
        callAndParse = new CallAndParse(subreddit);
        callAndParse.delegate = this;

        imageView = (ImageView) findViewById(R.id.backdrop);
        //gridview = (GridView) findViewById(R.id.gridview);


        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Snackbar.make(view, "Hello Snackbar", Snackbar.LENGTH_LONG).show();


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




                                        subreddit = subredditEditText.getText().toString();

                                        toolbar.setTitle(subreddit.toUpperCase());
                                        setSupportActionBar(toolbar);

                                        callAndParse = new CallAndParse(subreddit);
                                        callAndParse.delegate = MainActivity.this;

                                        Log.i("MyMainActivity", "imgurContainers " +  subredditEditText.getText().toString());



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
        });
    }

    @Override
    public void processFinish(ImgurContainer imgurContainers) {
        Log.i("MyMainActivity", "processFinish");
        Log.i("MyMainActivity", "imgurContainers " + imgurContainers.getImgurData().size());


        if(imgurContainers.getImgurData().size()  == 0 ){

            Snackbar.make(view, "Try a diferent subreddit", Snackbar.LENGTH_LONG).show();


        }else {

            imgurContainers.setSubRedditName(subreddit);
            imageAdapter = null;
            Log.i("MyMainActivity", "imgurContainers " + imgurContainers.getImgurData().get(0).getLink());



            Glide.with(mContext)
            .load(imgurContainers.getImgurData().get(0).getLink())
            .centerCrop()
            .crossFade()
            .into(imageView);

            imageAdapter = new ImageAdapter(mContext, imgurContainers);
            //gridview.setAdapter(imageAdapter);


            RVAdapter rvAdapter = new RVAdapter(imgurContainers, mContext);
            rv.setAdapter(rvAdapter);
        }

    }



}
