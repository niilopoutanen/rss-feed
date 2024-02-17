package com.niilopoutanen.rss_feed.sourcemanager;

import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.Nullable;

import com.niilopoutanen.rss_feed.common.PrimaryButton;
import com.niilopoutanen.rss_feed.common.stages.StageFragment;
import com.niilopoutanen.rss_feed.common.stages.StageHostActivity;

import java.util.ArrayList;
import java.util.List;

import app.rive.runtime.kotlin.core.RendererType;
import app.rive.runtime.kotlin.core.Rive;

public class SourceManagerActivity extends StageHostActivity {
    private ManageType type;
    PrimaryButton primaryButton;

    public SourceManagerActivity() {}
    public SourceManagerActivity(ManageType type){
        this.type = type;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Rive.INSTANCE.init(this, RendererType.Rive);
        setContentView(R.layout.activity_sourcemanager);
        primaryButton = findViewById(R.id.sourcemanager_continue);

        findViewById(R.id.sourcemanager_continue).setOnClickListener(v -> {
            primaryButton.setIsEnabled(false);
            currentFragment.canContinue(isAllowed -> {
                runOnUiThread(() -> {
                    if(isAllowed){
                        nextFragment();
                    }
                    primaryButton.setIsEnabled(true);
                });

            });
        });
    }

    @Override
    protected List<StageFragment> getStages() {
        List<StageFragment> stages = new ArrayList<>();
        stages.add(new SourceInputFragment());
        stages.add(new SourceStatusFragment());
        return stages;
    }

    @Override
    protected int getFragmentFrame() {
        return R.id.sourcemanager_frame;
    }

    @Override
    public void onProgressLocked(boolean progressAllowed) {
        Animation animation;
        if(!progressAllowed){
            animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 2.0f
            );
            primaryButton.setClickable(false);
        }

        else {
            animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 2.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f
            );
            primaryButton.setClickable(true);
        }

        animation.setDuration(300);
        animation.setFillAfter(true);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        primaryButton.startAnimation(animation);
    }

    @Override
    public void onLoadingStateChange(boolean isLoading) {
        PrimaryButton primaryButton = findViewById(R.id.sourcemanager_continue);
        if(primaryButton != null){
            if (isLoading) {
                primaryButton.setText("Loading");
            }
            else{
                primaryButton.setText("Continue");
            }
        }
    }


    public enum ManageType{
        CREATE, EDIT
    }



}
