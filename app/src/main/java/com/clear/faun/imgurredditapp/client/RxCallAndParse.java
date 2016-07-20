package com.clear.faun.imgurredditapp.client;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.bumptech.glide.Glide;
import com.clear.faun.imgurredditapp.interfaces.ImgurAPI;
import com.clear.faun.imgurredditapp.interfaces.ImgurResponse;
import com.clear.faun.imgurredditapp.model.ImgurContainer;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by SpencerDepas on 7/20/16.
 */
public class RxCallAndParse {

    private ImgurResponse imgurResponse;

    public RxCallAndParse(ImgurResponse imgurResponse) {
        this.imgurResponse = imgurResponse;
    }

    public void subRedditApiCall(String subreddit) {


        ImgurAPI service = ServiceFactory.createRetrofitService(ImgurAPI.class, ImgurAPI.SERVICE_ENDPOINT);

        service.getSubreddit(subreddit)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ImgurContainer>() {
                    @Override
                    public final void onCompleted() {
                        // do nothing
                        Timber.i("onCompleted : ");

                    }

                    @Override
                    public final void onError(Throwable e) {
                        Timber.e("onError : " + e.getMessage());
                        //imgurResponse.processFailed(e.getMessage());

                    }

                    @Override
                    public final void onNext(ImgurContainer response) {
                        Timber.i("onNext : ");

                        Timber.i("response : " + response.getImgurData().size());

                        loadedRX(response);
                    }
                });

    }


    private void loadedRX(ImgurContainer response) {
        Timber.i("loadedRX : " + response.getImgurData().size());

        imgurResponse.processFinish(response);

    }


    private void randRxCall() {
        Timber.d("randRxCall");

        List<String> users = new ArrayList<String>();

        users.add("jon snow");
        users.add("tyrion lannister");

//        Observable
//                .just(users)
//                .distinct()
//                .subscribe(new Action1<List<String> >() {
//                    @Override
//                    public void call(String s) {
//                        System.out.println(s);
//                    }
//                });


    }


}
