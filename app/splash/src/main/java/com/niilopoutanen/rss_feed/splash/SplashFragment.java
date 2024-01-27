package com.niilopoutanen.rss_feed.splash;

import android.content.Context;

import androidx.fragment.app.Fragment;

import java.util.function.Consumer;

public abstract class SplashFragment extends Fragment {
    protected Context context;
    protected Runnable finisher;

    public SplashFragment() {}
    public SplashFragment(Context context, Runnable finisher){
        this.context = context;
        this.finisher = finisher;
    }
}
