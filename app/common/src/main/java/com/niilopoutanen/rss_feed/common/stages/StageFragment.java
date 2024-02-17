package com.niilopoutanen.rss_feed.common.stages;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.transition.MaterialSharedAxis;

import java.io.Serializable;
import java.util.function.Consumer;

public abstract class StageFragment extends Fragment {
    protected Serializable data;
    protected StageBridge stageBridge;
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

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                stageBridge.userInvokedReturn();
                getParentFragmentManager().popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public abstract void canContinue(Consumer<Boolean> result);
    public abstract Serializable getState();
    public abstract boolean canReturn();


    public void setStageBridge(StageBridge stageBridge){
        this.stageBridge = stageBridge;
    }
}
