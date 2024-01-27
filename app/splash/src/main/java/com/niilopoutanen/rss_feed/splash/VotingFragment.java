package com.niilopoutanen.rss_feed.splash;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

public class VotingFragment extends SplashFragment {

    public VotingFragment(Context context, Runnable finisher) {
        super(context, finisher);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_voting, container, false);
        loadIconPreview(rootView);
        return rootView;
    }



    private void loadIconPreview(View rootView){
        List<String> icons = new ArrayList<>();
        
        CardView container = rootView.findViewById(R.id.splash_voting_icon_container);
    }
}
