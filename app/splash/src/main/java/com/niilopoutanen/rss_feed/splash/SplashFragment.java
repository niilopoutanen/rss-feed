package com.niilopoutanen.rss_feed.splash;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.transition.MaterialSharedAxis;

public abstract class SplashFragment extends Fragment {
    protected Context context;


    public SplashFragment() {
        this.context = getContext();
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }

    public void next(){
        if(getActivity() instanceof SplashActivity){
            SplashActivity splashActivity = (SplashActivity) getActivity();
            splashActivity.next();
        }
    }
    public void cancel(){
        if(getActivity() instanceof SplashActivity){
            SplashActivity splashActivity = (SplashActivity) getActivity();
            splashActivity.finish();
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (context == null) {
            context = getContext();
        }
    }
}
