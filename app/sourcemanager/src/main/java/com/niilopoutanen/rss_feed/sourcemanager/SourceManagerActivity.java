package com.niilopoutanen.rss_feed.sourcemanager;

import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.niilopoutanen.rss_feed.common.PrimaryButton;

import app.rive.runtime.kotlin.core.RendererType;
import app.rive.runtime.kotlin.core.Rive;

public class SourceManagerActivity extends AppCompatActivity implements StateListener {
    private ManageType type;
    private SourceInputFragment inputFragment;
    private SourceStatusFragment statusFragment;

    public SourceManagerActivity() {}
    public SourceManagerActivity(ManageType type){
        this.type = type;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Rive.INSTANCE.init(this, RendererType.Rive);
        setContentView(R.layout.activity_sourcemanager);

        inputFragment = new SourceInputFragment();
        setFragment(inputFragment);

        findViewById(R.id.sourcemanager_continue).setOnClickListener(v -> {
            statusFragment = SourceStatusFragment.newInstance(inputFragment.getInput());
            statusFragment.setStateListener(this);
            setFragment(statusFragment);
        });
    }


    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.sourcemanager_frame, fragment);
        fragmentTransaction.commit();
    }
    public enum ManageType{
        CREATE, EDIT
    }

    public void setContinueAllowed(boolean continueAllowed){
        PrimaryButton primaryButton = findViewById(R.id.sourcemanager_continue);
        if(!continueAllowed){
            Animation animation = new TranslateAnimation(
                      Animation.RELATIVE_TO_SELF, 0.0f,
                      Animation.RELATIVE_TO_SELF, 0.0f,
                      Animation.RELATIVE_TO_SELF, 0.0f,
                      Animation.RELATIVE_TO_SELF, 2.0f
            );
            animation.setDuration(300);
            animation.setFillAfter(true);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());

            primaryButton.setClickable(false);
            primaryButton.startAnimation(animation);
        }

        else {
            Animation animation = new TranslateAnimation(
                      Animation.RELATIVE_TO_SELF, 0.0f,
                      Animation.RELATIVE_TO_SELF, 0.0f,
                      Animation.RELATIVE_TO_SELF, 2.0f,
                      Animation.RELATIVE_TO_SELF, 0.0f
            );
            animation.setDuration(300);
            animation.setFillAfter(true);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());

            primaryButton.setClickable(true);
            primaryButton.startAnimation(animation);
        }
    }

    @Override
    public void allowFinish() {
        PrimaryButton primaryButton = findViewById(R.id.sourcemanager_continue);
        primaryButton.setText(getString(com.niilopoutanen.rss_feed.common.R.string.close));
        primaryButton.setOnClickListener(v -> finish());
    }

}
