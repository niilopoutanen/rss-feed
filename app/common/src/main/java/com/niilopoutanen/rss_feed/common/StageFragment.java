package com.niilopoutanen.rss_feed.common;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.transition.MaterialSharedAxis;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public abstract class StageFragment extends Fragment {
    protected Serializable data;
    private Runnable stageEvent;
    public StageFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
        if(getArguments() != null){
            data = getArguments().getSerializable("data");
        }
    }

    public abstract void canContinue(Consumer<Boolean> result);
    public abstract Serializable getState();

    public void setStageEvent(Runnable stageEvent){
        this.stageEvent = stageEvent;
    }

    protected void nextStage(){
        if(stageEvent != null){
            stageEvent.run();
        }
    }

    public void setState(Object data){

    }
}
