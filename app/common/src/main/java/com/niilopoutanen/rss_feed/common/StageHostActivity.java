package com.niilopoutanen.rss_feed.common;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public abstract class StageHostActivity extends AppCompatActivity {
    protected StageFragment currentFragment;
    private int currentStage = 0;
    protected abstract List<StageFragment> getStages();
    @IdRes
    protected abstract int getFragmentFrame();
    protected void nextFragment(){
        StageFragment next = getStages().get(currentStage);
        Object arguments = currentFragment.getState();
        next.setState(arguments);
        setFragment(next);
        currentStage++;
        currentFragment = next;
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(getFragmentFrame(), fragment);
        fragmentTransaction.commit();
    }
}
