package com.niilopoutanen.rss_feed.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Preferences;

public class DiscoverFragment extends Fragment {

    Context appContext;
    Preferences preferences;
    public DiscoverFragment(Context context, Preferences preferences) {
        this.appContext = context;
        this.preferences = preferences;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discover, container, false);
        return rootView;
    }
}