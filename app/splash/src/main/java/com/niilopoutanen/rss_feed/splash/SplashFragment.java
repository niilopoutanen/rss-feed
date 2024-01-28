package com.niilopoutanen.rss_feed.splash;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.transition.MaterialSharedAxis;

public abstract class SplashFragment extends Fragment {
    protected Context context;
    protected Runnable next;
    protected Runnable skip;


    public SplashFragment() {
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }
    public SplashFragment(Context context, Runnable next, Runnable skip){
        this.context = context;
        this.next = next;
        this.skip = skip;

        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (context == null) {
            context = getContext();
        }
    }
}
