package com.clear.faun.imgurredditapp.Controller;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.clear.faun.imgurredditapp.Model.ImgurContainer;
import com.clear.faun.imgurredditapp.Model.ImgurResponse;
import static android.widget.ImageView.ScaleType.CENTER_CROP;
import java.util.ArrayList;
import java.util.zip.Inflater;


/**
 * Created by spencer on 9/2/2015.
 */
public class ImageAdapter extends BaseAdapter {


    private Context mContext;
    ImgurContainer imgurContainers;


    // Constructor
    public ImageAdapter(Context c, ImgurContainer imgurContainers) {
        Log.i("MyImageAdapter", "ImageAdapter Constructor");
        mContext = c;
        this.imgurContainers = imgurContainers;


    }

    public int getCount() {
        return imgurContainers.getImgurData().size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    Inflater inflater;
    int width = 0;
    int height = 0;

    @Override
    public View getView(int position, View recycled, ViewGroup container) {
        Log.d("MyImageAdapter", "getView() pos " + position);
        final ImageView myImageView;
        if (recycled == null) {
            myImageView = new ImageView(mContext);
            myImageView.setScaleType(CENTER_CROP);
        }else {
            myImageView = (ImageView) recycled;
        }



        if(width == 0){
            width = getScreenWidth() / 2;
            height = width;
        }


        //Log.d("MyImageAdapter", "screen width :)" + width);

        String url = imgurContainers.getImgurData().get(position).getLink();

      /*  Picasso.with(mContext)
                .load(url)
                .resize(width, height)
                .centerCrop()
                //.centerInside()
                .into(myImageView);*/

        Glide.with(mContext)
                .load(url)
                .override(width, height)
                .centerCrop()
                .crossFade()
                .into(myImageView);


      /*  Glide.with(mContext)
                .load(url)
                //.override(width, height)
                .centerCrop()
                .crossFade()
                .into(myImageView);*/

        return myImageView;
    }

    public int getScreenWidth(){
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }


}