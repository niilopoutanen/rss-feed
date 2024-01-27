package com.niilopoutanen.rss_feed.splash;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.niilopoutanen.rss_feed.common.PreferencesManager;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesManager.setSavedTheme(this, PreferencesManager.loadPreferences(this));
        setContentView(R.layout.activity_splash);

        setFragment();
    }

    private void setFragment(){
        FragmentManager manager = getSupportFragmentManager();
        Fragment target = new OnBoardingFragment(this);
        manager.beginTransaction().replace(R.id.splash_container, target, "fragment").commit();
    }
}
