package com.niilopoutanen.rss_feed.common;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;
import java.util.List;

public abstract class StageHostActivity extends AppCompatActivity {
    protected StageFragment currentFragment;
    private int currentStage = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getStages().size() == 0){
            throw new IllegalArgumentException("No stages set");
        }
        nextFragment();
    }

    protected abstract List<StageFragment> getStages();
    @IdRes
    protected abstract int getFragmentFrame();
    protected void nextFragment(){
        StageFragment next = getStages().get(currentStage);
        if(next != null){
            if(currentFragment != null){
                Serializable data = currentFragment.getState();
                Bundle args = new Bundle();
                args.putSerializable("data", data);
                next.setArguments(args);
            }


            setFragment(next);
            currentStage++;
            currentFragment = next;
        }
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(getFragmentFrame(), fragment);
        fragmentTransaction.commit();
    }
}
