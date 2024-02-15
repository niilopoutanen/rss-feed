package com.niilopoutanen.rss_feed.common;

import androidx.fragment.app.Fragment;

public abstract class StageFragment extends Fragment {
    protected Object data;
    private Runnable stageEvent;
    public StageFragment() {

    }
    public abstract boolean canContinue();
    public abstract Object getState();

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
