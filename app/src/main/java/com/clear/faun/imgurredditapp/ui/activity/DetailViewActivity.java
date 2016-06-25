package com.clear.faun.imgurredditapp.ui.activity;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clear.faun.imgurredditapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailViewActivity extends AppCompatActivity {

    @Bind(R.id.detail_image) ImageView detailImageView;
    @Bind(R.id.detail_text) TextView textView;
    @Bind(R.id.detail_card_view) CardView mCardView;

    @Bind(R.id.detail_image_full) ImageView detailImageViewFullScreen;



    private Context mContext;
    public static String IMAGE_URL_KEY = "transition_key";
    public static String IMAGE_TITTLE_KEY = "transition_key_ONE";

    private int width = 0;
    private int height = 0;
    private String imageTittle;
    private String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view_two);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        Log.i("MyDetailView", "onCreate  :");
        mContext = getApplicationContext();



        mCardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.BlueGray));

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                imageURL= null;
                imageTittle = null;
            } else {
                imageURL= extras.getString(IMAGE_URL_KEY);
                imageTittle = extras.getString(IMAGE_TITTLE_KEY);
            }
        } else {
            imageURL= (String) savedInstanceState.getSerializable(IMAGE_URL_KEY);
            imageTittle = (String) savedInstanceState.getSerializable(IMAGE_TITTLE_KEY);
        }

        Log.i("MyDetailView", "imageURL  : " + imageURL);

//        if (width == 0) {
//            width = getScreenWidth() / 2;
//            height = width;
//        }


        Glide.with(mContext)
                .load(imageURL)
                //.crossFade()
                //.override(width, height)
                .into(detailImageView);



        textView.setText(imageTittle);

    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i("MyDetailView", "onResume:");
        // put your code here...
        detailImageView.setVisibility(View.INVISIBLE);

        detailImageViewFullScreen.setVisibility(View.VISIBLE);
        Glide.with(mContext)
                .load("http://i.imgur.com/WqrrOks.jpg")
                        //.crossFade()
                        //.override(width, height)
                .into(detailImageViewFullScreen);

    }

    private boolean mIsFullscreen = false;

    @OnClick(R.id.detail_image)
    void imageOnClick() {
        Log.i("MyDetailView", "imageOnClick:");

//        if(mIsFullscreen){
//            Log.i("MyDetailView", "mIsFullscreen:"+ mIsFullscreen);
//            mIsFullscreen = false;
//            Glide.with(mContext)
//                    .load(imageURL)
//                    .crossFade()
//                    .override(width, height)
//                    .centerCrop()
//                    .into(detailImageView);
//        }else {
//            Log.i("MyDetailView", "mIsFullscreen:"+ mIsFullscreen);
//            mIsFullscreen = true;
//            Glide.with(mContext)
//                    .load(imageURL)
//                    .crossFade()
//                    .into(detailImageView);
//        }


    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)  {
//        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5
//                && keyCode == KeyEvent.KEYCODE_BACK
//                && event.getRepeatCount() == 0) {
//            Log.d("MyDetailView", "onKeyDown Called");
//
//            if(!mIsFullscreen){
//                Log.i("MyDetailView", "mIsFullscreen:"+ mIsFullscreen);
//                mIsFullscreen = false;
//                Glide.with(mContext)
//                        .load(imageURL)
//                        .crossFade()
//                        .override(width, height)
//                        .centerCrop()
//                        .into(detailImageView);
//            }
//            onBackPressed();
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }



    private int getScreenWidth(){
        Log.i("MyDetailView", "getScreenWidth ");
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }



}
