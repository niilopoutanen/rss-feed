package com.niilopoutanen.rss_feed.splash;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.niilopoutanen.rss_feed.common.PreferencesManager;

public class SplashActivity extends AppCompatActivity {
    private Stage currentStage;
    public SplashActivity(){
        this.currentStage = Stage.INITIAL;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesManager.setSavedTheme(this, PreferencesManager.loadPreferences(this));
        setContentView(R.layout.activity_splash);

        next();
    }

    private void setFragment(Fragment target){
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.splash_container, target, "fragment").commit();
    }


    public void next(){
        switch (currentStage){
            case INITIAL:
                setFragment(new OnBoardingFragment());
                currentStage = Stage.ONBOARDING;
                break;
            case ONBOARDING:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        PreferencesManager.setLatestVersion(this);
        super.finish();
    }

    public enum Stage{
        INITIAL, ONBOARDING
    }
}
