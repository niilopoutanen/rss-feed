package com.niilopoutanen.rss_feed;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.customization.PreferencesManager;

public class SettingsHapticsFragment extends Fragment {
    private Context appContext;
    public SettingsHapticsFragment() {}
    public SettingsHapticsFragment(Context context){
        this.appContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings_haptics, container, false);

        ((LinearLayout)rootView.findViewById(R.id.feedsettings_return)).setOnClickListener(v -> {
            PreferencesManager.vibrate(v, appContext);
            getParentFragmentManager().popBackStack();
        });

        return rootView;
    }
}