package com.niilopoutanen.rss_feed.common.stages;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;
import java.util.List;

public abstract class StageHostActivity extends AppCompatActivity implements StageBridge {
    protected StageFragment currentFragment;
    private int currentStage = -1;

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
        currentStage++;
        StageFragment next = getStages().get(currentStage);
        if(next != null){
            if(currentFragment != null){
                Serializable data = currentFragment.getState();
                Bundle args = new Bundle();
                args.putSerializable("data", data);
                next.setArguments(args);
            }

            next.setStageBridge(this);
            setFragment(next);
            currentFragment = next;
        }
    }
    @Override
    public void userInvokedReturn() {
        currentStage--;
        currentFragment = getStages().get(currentStage);
    }
    public void setFragment(StageFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(fragment.canReturn()){
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.replace(getFragmentFrame(), fragment);
        fragmentTransaction.commit();
    }
}
