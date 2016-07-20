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
import com.clear.faun.imgurredditapp.adapter.MainActivityAdapter;
import com.clear.faun.imgurredditapp.client.RxCallAndParse;
import com.clear.faun.imgurredditapp.client.ServiceFactory;
import com.clear.faun.imgurredditapp.interfaces.ImgurAPI;
import com.clear.faun.imgurredditapp.model.REST_ERRORS;
import com.clear.faun.imgurredditapp.ui.fragment.NavigationViewFragment;
import com.clear.faun.imgurredditapp.model.ImgurContainer;
import com.clear.faun.imgurredditapp.interfaces.ImgurResponse;
import com.clear.faun.imgurredditapp.database.RealmDatabase;
import com.clear.faun.imgurredditapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements ImgurResponse,
        NavigationViewFragment.NavigationDrawerCallbacks {


    private ArrayList<String> mSearchedSubreddits = new ArrayList<>();
    private Context mContext;
    private String mCurrentSubreddit = "NYCSTREETART";
    private SharedPreferences mPref;
    private RealmDatabase mDatabase;
    private MainActivityAdapter mRvAdapter;
    private ImgurContainer mImgurContainers;
    private NavigationViewFragment mNavigationDrawerFragment;


    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.main_content)
    View mView;

    @Bind(R.id.backdrop)
    ImageView mBannerImageView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;

    @Bind(R.id.rv)
    RecyclerView mRv;

    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    private RxCallAndParse rxCallAndParse ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.main_layout);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());


        Timber.tag("LifeCycles");
        Timber.d("Activity Created");


        mImgurContainers = null;

        mContext = getApplicationContext();
        mDatabase = new RealmDatabase(mContext);


        setUpToolBar();


        setUpNavView();


        mPref = getApplicationContext().getSharedPreferences("MyPref", 0);

        if (mPref.getString("saved_subreddit", null) != null) {
            mCurrentSubreddit = mPref.getString("saved_subreddit", null);
        }


        //change nav bar colour
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        }
        mView.setBackgroundColor(Color.WHITE);


        mRv.setLayoutManager(new GridLayoutManager(this, 2));
        mRvAdapter = new MainActivityAdapter();

        rxCallAndParse = new RxCallAndParse(this);


    }

    private void setUpToolBar() {

        //this enables the toolbar name to change when
        //you change a subreddit
        mCollapsingToolbar.setTitleEnabled(true);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



    private void setUpNavView() {

        mNavigationDrawerFragment = (NavigationViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_fragment);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_fragment,
                (DrawerLayout) findViewById(R.id.drawer_layout));


    }

    private void rxCall(String subreddit) {


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

                    }

                    @Override
                    public final void onNext(ImgurContainer response) {
                        Timber.i("onNext : ");

                        loadedRX(response);

                    }
                });

    }


    @Override
    public void onResume() {
        super.onResume();
        Timber.i("onResume : ");

        Timber.i("mCurrentSubreddit : " + mCurrentSubreddit);


        String[] subReddits = loadDataBase();


        mNavigationDrawerFragment.updateDraw(subReddits);


        if (mImgurContainers == null) {
            rxCallAndParse.subRedditApiCall(mCurrentSubreddit);
            rxCall(mCurrentSubreddit);
        }


    }

    private String[] loadDataBase() {


        if (mDatabase == null) {
            Timber.i("mDatabase == null : ");

            mDatabase = new RealmDatabase(mContext);
        }

        mDatabase.loadData(mSearchedSubreddits);
        String[] stockArr = new String[mSearchedSubreddits.size()];
        stockArr = mSearchedSubreddits.toArray(stockArr);


        return stockArr;

    }


    @Override
    public void onPause() {
        super.onPause();
        Timber.i("onPause ");


        saveSubReddits();

    }


    private void saveSubReddits() {

        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("saved_subreddit", mCurrentSubreddit);
        editor.apply();
    }

    @OnClick(R.id.fab)
    void fabOnClick() {
        Timber.i("fabOnClick ");


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

    private void searchForSubredditDialog(String subreddit) {


        if (subreddit.length() > 1) {
            loadingSwitch();
            rxCall(subreddit);
        } else {
            Snackbar.make(mView, "Please try again", Snackbar.LENGTH_LONG).show();
        }


    }


    private void loadingSwitch() {
        if (mRv.getVisibility() == View.VISIBLE) {
            Timber.i("mRv.getVisibility()== View.VISIBLE ");

            mProgressBar.setVisibility(View.VISIBLE);
            mRv.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRv.setVisibility(View.VISIBLE);
        }

    }


    private void loadedRX(ImgurContainer response) {
        mImgurContainers = response;
        Timber.i("loadedRX " + mImgurContainers.getImgurData().get(1).getLink());

        this.mImgurContainers = mImgurContainers;
        //allways want to be true
        mRv.setVisibility(View.VISIBLE);

        if (mImgurContainers.getImgurData().size() == 0) {

            // nothing returned
            Snackbar.make(mView, "Try a diferent mCurrentSubreddit", Snackbar.LENGTH_LONG).show();


        } else {

            //this will be saved to realm onpause


            //Log.i("MyMainActivity", "else " + mCurrentSubreddit);


            //currunt subreddit is only updated is load subreddit is returned with sucsess
            mCurrentSubreddit = mImgurContainers.getSubRedditName();

            Timber.i("mImgurContainers mCurrentSubreddit " + mCurrentSubreddit);


            Glide.clear(mView);
            mImgurContainers.setSubRedditName(mCurrentSubreddit);
            //imageAdapter = null;


            mCollapsingToolbar.setTitle("/R/" + mCurrentSubreddit);
            mToolbar.setTitle("/R/" + mCurrentSubreddit);


            Glide.with(mContext)
                    .load(mImgurContainers.getImgurData().get(0).getLink())
                    .centerCrop()
                    .crossFade()
                    .into(mBannerImageView);

            //dont want it to load in grid mView
            mImgurContainers.getImgurData().remove(0);


            MainActivity mApp = this;
            mRvAdapter.setInfo(mImgurContainers, mContext, mApp);
            mRv.setAdapter(mRvAdapter);

            addSubreddittoSavedList();


            mProgressBar.setVisibility(mView.INVISIBLE);
            mDatabase.saveData(mSearchedSubreddits);
            String[] stockArr = new String[mSearchedSubreddits.size()];
            stockArr = mSearchedSubreddits.toArray(stockArr);
            mNavigationDrawerFragment.updateDraw(stockArr);


        }


    }


    @Override
    public void processFinish(ImgurContainer imgurContainers) {
        Timber.i("processFinish");
        Timber.i("imgurContainers.getImgurData().size()" + imgurContainers.getImgurData().size());

        this.mImgurContainers = imgurContainers;
        //allways want to be true
        mRv.setVisibility(View.VISIBLE);

        if (imgurContainers.getImgurData().size() == 0) {

            // nothing returned
            Snackbar.make(mView, "Try a diferent mCurrentSubreddit", Snackbar.LENGTH_LONG).show();


        } else {

            //this will be saved to realm onpause


            //Log.i("MyMainActivity", "else " + mCurrentSubreddit);


            //currunt subreddit is only updated is load subreddit is returned with sucsess
            mCurrentSubreddit = imgurContainers.getSubRedditName();


            Glide.clear(mView);
            imgurContainers.setSubRedditName(mCurrentSubreddit);
            //imageAdapter = null;


            mCollapsingToolbar.setTitle("/R/" + mCurrentSubreddit);
            mToolbar.setTitle("/R/" + mCurrentSubreddit);


            Glide.with(mContext)
                    .load(imgurContainers.getImgurData().get(0).getLink())
                    .centerCrop()
                    .crossFade()
                    .into(mBannerImageView);

            //dont want it to load in grid mView
            imgurContainers.getImgurData().remove(0);


            MainActivity mApp = this;
            mRvAdapter.setInfo(imgurContainers, mContext, mApp);
            mRv.setAdapter(mRvAdapter);

            addSubreddittoSavedList();


            mProgressBar.setVisibility(mView.INVISIBLE);
            mDatabase.saveData(mSearchedSubreddits);
            String[] stockArr = new String[mSearchedSubreddits.size()];
            stockArr = mSearchedSubreddits.toArray(stockArr);
            mNavigationDrawerFragment.updateDraw(stockArr);


        }


    }


    private void addSubreddittoSavedList() {
        Timber.i("addSubreddittoSavedList");

        if (mSearchedSubreddits.size() > 0) {
            for (int i = 0; i < mSearchedSubreddits.size(); i++) {
                if (mSearchedSubreddits.get(i).equals(mCurrentSubreddit)) {
                    //if it is found then we break
                    break;

                } else {
                    if (i == mSearchedSubreddits.size() - 1) {
                        mSearchedSubreddits.add(mCurrentSubreddit);
                        Timber.i("added mCurrentSubreddit " + mCurrentSubreddit);
                        break;
                    }
                }
            }
        } else {
            mSearchedSubreddits.add(mCurrentSubreddit);
        }

    }

    @Override
    public void processFailed(String error) {
        Log.i("MyMainActivity", "error : " + error);
        Timber.i("processFailed");
        Timber.e("error : " + error);


        if (error.equals(REST_ERRORS.IMGUR_OVER_CAPACITY)) {
            Snackbar.make(mView, "R/Image is temporarily over capacity. Please try again", Snackbar.LENGTH_LONG).show();
        } else if (error.equals(REST_ERRORS.RETROFIT_NO_CONNECTION) || error.equals(REST_ERRORS.RETROFIT_NO_HOST)) {
            Snackbar.make(mView, "Not connected to the interweb", Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(mView, "Please try a diferent mCurrentSubreddit", Snackbar.LENGTH_LONG).show();
        }

        mRv.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putString("SUBREDDIT", mCurrentSubreddit);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mCurrentSubreddit = savedInstanceState.getString("SUBREDDIT");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.i("item " + item.getItemId());

        if (item.getItemId() == android.R.id.home) {
            Timber.i("item.getItemId() == android.R.id.home ");

            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else {
            Timber.i("Logout ");

            return true;
        }


    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Timber.i("onNavigationDrawerItemSelected position:" + position);

        mDrawerLayout.closeDrawers();
        rxCall(mSearchedSubreddits.get(position));
        mCurrentSubreddit = mSearchedSubreddits.get(position);
    }
}
