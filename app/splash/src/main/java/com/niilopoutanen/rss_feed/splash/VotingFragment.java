package com.niilopoutanen.rss_feed.splash;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.niilopoutanen.rss_feed.common.FadeContainer;
import com.squareup.picasso.Picasso;

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
        String baseUrl = "https://raw.githubusercontent.com/niilopoutanen/rss-feed/app-resources/voting-icons/";
        List<String> icons = new ArrayList<>();
        icons.add("icon1.png");
        icons.add("icon2.png");
        icons.add("icon3.png");
        icons.add("icon4.png");

        CardView container = rootView.findViewById(R.id.splash_voting_icon_container);
        FadeContainer fadeContainer = new FadeContainer(context);

        for(String url : icons){
            ImageView icon = new ImageView(context);
            Picasso.get().load(baseUrl + url).into(icon);
            fadeContainer.add(icon);
        }

        container.addView(fadeContainer);
        fadeContainer.start();

    }

}
