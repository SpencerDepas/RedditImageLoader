package com.clear.faun.imgurredditapp.Controller;

import android.content.Context;
import android.graphics.Point;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clear.faun.imgurredditapp.Model.ImgurContainer;
import com.clear.faun.imgurredditapp.R;

/**
 * Created by spencer on 9/6/2015.
 */
public class RVAdapter  extends RecyclerView.Adapter<RVAdapter.ImgurViewHolder>{

    private Context mContext;
    private int width = 0;
    private int height = 0;
    private ImgurContainer imgurContainers;
    private int itemLayout;

    RVAdapter(){

    }


    public void setInfo(ImgurContainer imgurContainers, Context mContext) {
        Log.i("MyRVAdapter", "RVAdapter " );
        this.imgurContainers = imgurContainers;
        this.mContext = mContext;
        if (width == 0) {
            width = getScreenWidth() / 2;
            height = width;
        }


    }


    @Override
    public ImgurViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("MyRVAdapter", "onCreateViewHolder" );
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_grid_view_layout, parent, false);
        ImgurViewHolder imVW = new ImgurViewHolder(v);
        return imVW;

    }

    @Override
    public void onBindViewHolder(ImgurViewHolder holder, int position) {
        Log.i("MyRVAdapter", "onBindViewHolder");
        Log.i("MyRVAdapter", "position " + position);

        if(imgurContainers.getImgurData().get(position).getTitle().length() > 30){
            holder.imageTittle.setText(imgurContainers.getImgurData().get(position).getTitle()
                    .substring(0, 25) + "...");
        }else{
            holder.imageTittle.setText(imgurContainers.getImgurData().get(position).getTitle());
        }

        Glide.with(mContext)
                .load(imgurContainers.getImgurData().get(position).getLink())
                .override(width, height)
                .centerCrop()
                .crossFade()
                .into(holder.imageView);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return imgurContainers.getImgurData().size();
    }

    public static class ImgurViewHolder extends RecyclerView.ViewHolder {


        TextView imageTittle;
        ImageView imageView;

        ImgurViewHolder(View itemView) {
            super(itemView);
            Log.i("MyRVAdapter", "ImgurViewHolder" );
            //cv = (CardView)itemView.findViewById(R.id.cv);
            imageTittle = (TextView)itemView.findViewById(R.id.image_tittle);
            imageView = (ImageView)itemView.findViewById(R.id.image_view);
        }
    }

    private int getScreenWidth(){
        Log.i("MyRVAdapter", "getScreenWidth " );
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

}