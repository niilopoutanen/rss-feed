package com.niilopoutanen.rss_feed.customization;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        Preferences.HapticTypes selected = PreferencesManager.getEnumPreference(Preferences.SP_HAPTICS_TYPE, Preferences.PREFS_FUNCTIONALITY, Preferences.HapticTypes.class, Preferences.SP_HAPTICS_TYPE_DEFAULT, appContext);
        TextView type1 = rootView.findViewById(R.id.vibration_type1);
        TextView type2 = rootView.findViewById(R.id.vibration_type2);
        TextView type3 = rootView.findViewById(R.id.vibration_type3);
        return rootView;
    }

}