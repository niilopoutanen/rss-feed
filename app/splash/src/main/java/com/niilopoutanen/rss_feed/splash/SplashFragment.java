package com.niilopoutanen.rss_feed.splash;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.google.android.material.transition.MaterialSharedAxis;

import java.util.function.Consumer;

public abstract class SplashFragment extends Fragment {
    protected Context context;
    protected Runnable finisher;

    public SplashFragment() {
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }
    public SplashFragment(Context context, Runnable finisher){
        this.context = context;
        this.finisher = finisher;

        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }
}
