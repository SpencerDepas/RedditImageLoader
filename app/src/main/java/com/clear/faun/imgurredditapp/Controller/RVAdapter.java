package com.clear.faun.imgurredditapp.Controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;

import android.os.Build;

import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
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
public class RVAdapter  extends RecyclerView.Adapter<RVAdapter.ImgurViewHolder> {

    private Context mContext;
    private int width = 0;
    private int height = 0;
    private static ImgurContainer imgurContainers;
    private int itemLayout;
    private RecyclerView recyclerView;

    //AdapterView.OnItemClickListener mItemClickListener;
    RVAdapter(){

    }
    public static MainActivity mActivity;




    public void setInfo(ImgurContainer imgurContainers, Context mContext,  MainActivity mActivity) {
        Log.i("MyRVAdapter", "RVAdapter " );
        this.imgurContainers = imgurContainers;
        this.mContext = mContext;
        this.mActivity = mActivity;
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
    public void onBindViewHolder(final ImgurViewHolder holder, int position) {
        Log.i("MyRVAdapter", "onBindViewHolder");
        Log.i("MyRVAdapter", "position " + position);

        if(imgurContainers.getImgurData().get(position).getTitle().length() > 30){
            holder.imageTittle.setText(imgurContainers.getImgurData().get(position).getTitle()
                    .substring(0, 25) );
        }else{
            holder.imageTittle.setText(imgurContainers.getImgurData().get(position).getTitle());
        }

        Log.i("MyRVAdapter", "width : " + width);
        Log.i("MyRVAdapter", "height : " + height);

        Glide.with(mContext)
                .load(imgurContainers.getImgurData().get(position).getLink())
                //.override(width, height)
                .centerCrop()
                //.crossFade()
                .fitCenter()
                .into(holder.imageView);


        holder.bind(position);

    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
         this.recyclerView = recyclerView;
    }




    @Override
    public int getItemCount() {
        return imgurContainers.getImgurData().size();
    }

    public static class ImgurViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        private int mPosition;

        final TextView imageTittle;

        final ImageView imageView;


        ImgurViewHolder(View itemView) {
            super(itemView);
            Log.i("MyRVAdapter", "ImgurViewHolder" );
            //cv = (CardView)itemView.findViewById(R.id.cv);
            imageTittle = (TextView)itemView.findViewById(R.id.image_tittle);
            imageView = (ImageView)itemView.findViewById(R.id.image_view);

            itemView.setOnClickListener(this);

        }

        public void bind(int position) {
            Log.i("MyRVAdapter", "ImgurViewHolder" );

            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            Log.i("MyRVAdapter", "onClick new");

            Log.i("MyRVAdapter", "mPosition " + mPosition);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.i("MyRVAdapter", "onClick on lolipop");

                Pair<View, String> pair1 = Pair.create((View)imageView, "profile1");
                Pair<View, String> pair2 = Pair.create((View)imageTittle, "transition1");
                Intent intent = new Intent(mActivity, DetailView.class);
                intent.putExtra(DetailView.IMAGE_URL_KEY, imgurContainers.getImgurData().get(mPosition).getLink());
                intent.putExtra(DetailView.IMAGE_TITTLE_KEY, imgurContainers.getImgurData().get(mPosition).getTitle());
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(mActivity, pair1, pair2);
//                ActivityOptionsCompat options = ActivityOptionsCompat.
//                        .makeSceneTransitionAnimation(mActivity, pair1 , pair2);
                mActivity.startActivity(intent, options.toBundle());
            }else{

                Intent intent = new Intent(mActivity, DetailView.class);
                intent.putExtra(DetailView.IMAGE_URL_KEY, "http://i.imgur.com/Mb6AMg0.jpg");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }


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