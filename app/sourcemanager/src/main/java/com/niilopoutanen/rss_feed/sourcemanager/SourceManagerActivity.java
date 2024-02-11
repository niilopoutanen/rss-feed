package com.niilopoutanen.rss_feed.sourcemanager;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import app.rive.runtime.kotlin.RiveAnimationView;
import app.rive.runtime.kotlin.RiveInitializer;
import app.rive.runtime.kotlin.core.RendererType;
import app.rive.runtime.kotlin.core.Rive;

public class SourceManagerActivity extends AppCompatActivity {
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

        setFragment(new SourceInputFragment());

        findViewById(R.id.sourcemanager_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SourceStatusFragment());
            }
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
}
