package com.niilopoutanen.rss_feed.sourcemanager;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.niilopoutanen.rss_feed.common.PrimaryButton;

import app.rive.runtime.kotlin.RiveAnimationView;
import app.rive.runtime.kotlin.RiveInitializer;
import app.rive.runtime.kotlin.core.RendererType;
import app.rive.runtime.kotlin.core.Rive;

public class SourceManagerActivity extends AppCompatActivity {
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
            setContinueAllowed(false);
            statusFragment = SourceStatusFragment.newInstance(inputFragment.getInput());
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
                      Animation.RELATIVE_TO_SELF, 1.0f
            );
            animation.setDuration(500);
            animation.setFillAfter(true);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Animation started
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Animation ended
                    primaryButton.clearAnimation(); // Clear animation after it ends
                    // Hide or remove the button from the layout here
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // Animation repeated
                }
            });

            primaryButton.setClickable(false);
            primaryButton.startAnimation(animation);
        }
    }
}
