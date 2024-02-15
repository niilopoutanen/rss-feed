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
import com.niilopoutanen.rss_feed.common.StageFragment;
import com.niilopoutanen.rss_feed.common.StageHostActivity;
import com.niilopoutanen.rss_feed.parser.Callback;
import com.niilopoutanen.rss_feed.parser.Parser;
import com.niilopoutanen.rss_feed.parser.RSSException;
import com.niilopoutanen.rss_feed.rss.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import app.rive.runtime.kotlin.core.RendererType;
import app.rive.runtime.kotlin.core.Rive;

public class SourceManagerActivity extends StageHostActivity {
    private ManageType type;

    public SourceManagerActivity() {}
    public SourceManagerActivity(ManageType type){
        this.type = type;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Rive.INSTANCE.init(this, RendererType.Rive);
        setContentView(R.layout.activity_sourcemanager);

        findViewById(R.id.sourcemanager_continue).setOnClickListener(v -> {
            currentFragment.canContinue(isAllowed -> {
                if(isAllowed){
                    runOnUiThread(this::nextFragment);
                }
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


}
