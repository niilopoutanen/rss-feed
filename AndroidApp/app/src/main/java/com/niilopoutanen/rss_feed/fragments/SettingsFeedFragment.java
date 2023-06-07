package com.niilopoutanen.rss_feed.fragments;

import static com.niilopoutanen.rss_feed.models.Preferences.DateStyle;
import static com.niilopoutanen.rss_feed.models.Preferences.PREFS_FUNCTIONALITY;
import static com.niilopoutanen.rss_feed.models.Preferences.PREFS_LANG;
import static com.niilopoutanen.rss_feed.models.Preferences.PREFS_UI;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FEEDCARD_AUTHORNAME;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FEEDCARD_AUTHORNAME_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FEEDCARD_AUTHORVISIBLE;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FEEDCARD_AUTHORVISIBLE_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FEEDCARD_DATESTYLE;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FEEDCARD_DATESTYLE_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FEEDCARD_DATEVISIBLE;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FEEDCARD_DATEVISIBLE_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FEEDCARD_DESCVISIBLE;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FEEDCARD_DESCVISIBLE_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FEEDCARD_STYLE;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FEEDCARD_STYLE_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FEEDCARD_TITLEVISIBLE;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FEEDCARD_TITLEVISIBLE_DEFAULT;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

import java.util.Arrays;
import java.util.List;

public class SettingsFeedFragment extends Fragment {

    List<View> feedcardStyleButtons;
    private SwitchCompat authorSwitch;
    private SwitchCompat authorNameSwitch;
    private SwitchCompat titleSwitch;
    private SwitchCompat descSwitch;
    private SwitchCompat dateSwitch;
    private Spinner dateSpinner;
    private Context appContext;


    public SettingsFeedFragment(Context context) {
        this.appContext = context;
    }

    public SettingsFeedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (appContext == null) {
            appContext = getContext();
        }
        View rootView = inflater.inflate(R.layout.fragment_settings_feed, container, false);
        LinearLayout returnBtn = rootView.findViewById(R.id.feedsettings_return);
        returnBtn.setOnClickListener(view -> {
            PreferencesManager.vibrate(view, PreferencesManager.loadPreferences(appContext), appContext);
            getParentFragmentManager().popBackStack();
        });


        feedcardStyleButtons = Arrays.asList(
                rootView.findViewById(R.id.largeimage_checkbox),
                rootView.findViewById(R.id.smallimage_checkbox),
                rootView.findViewById(R.id.noimage_checkbox)
        );
        List<RelativeLayout> feedcardContainers = Arrays.asList(
                rootView.findViewById(R.id.feedcard_largeimage),
                rootView.findViewById(R.id.feedcard_smallimage),
                rootView.findViewById(R.id.feedcard_noimage)
        );

        authorSwitch = rootView.findViewById(R.id.switch_author);
        authorNameSwitch = rootView.findViewById(R.id.switch_authorname);
        titleSwitch = rootView.findViewById(R.id.switch_title);
        descSwitch = rootView.findViewById(R.id.switch_description);
        dateSwitch = rootView.findViewById(R.id.switch_date);

        String[] dateModes = appContext.getResources().getStringArray(R.array.date_modes);
        dateSpinner = rootView.findViewById(R.id.spinner_date);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(appContext, R.layout.spinner_item, dateModes);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        dateSpinner.setAdapter(adapter);

        loadSavedData();

        for (int i = 0; i < feedcardContainers.size(); i++) {
            int finalI = i;
            feedcardContainers.get(i).setOnClickListener(v -> onCardStyleChange(feedcardStyleButtons.get(finalI), feedcardStyleButtons));
        }

        authorSwitch.setOnCheckedChangeListener((compoundButton, b) -> PreferencesManager.saveBooleanPreference(SP_FEEDCARD_AUTHORVISIBLE, PREFS_UI, b, appContext));
        authorNameSwitch.setOnCheckedChangeListener((compoundButton, b) -> PreferencesManager.saveBooleanPreference(SP_FEEDCARD_AUTHORNAME, PREFS_FUNCTIONALITY, b, appContext));
        titleSwitch.setOnCheckedChangeListener((compoundButton, b) -> PreferencesManager.saveBooleanPreference(SP_FEEDCARD_TITLEVISIBLE, PREFS_UI, b, appContext));
        descSwitch.setOnCheckedChangeListener((compoundButton, b) -> PreferencesManager.saveBooleanPreference(SP_FEEDCARD_DESCVISIBLE, PREFS_UI, b, appContext));
        dateSwitch.setOnCheckedChangeListener((compoundButton, b) -> PreferencesManager.saveBooleanPreference(SP_FEEDCARD_DATEVISIBLE, PREFS_UI, b, appContext));


        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                DateStyle themeMode = DateStyle.values()[position];
                PreferencesManager.saveEnumPreference(SP_FEEDCARD_DATESTYLE, PREFS_LANG, themeMode, appContext);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return rootView;
    }

    private void loadSavedData() {
        // Load saved FeedCardStyle enum value and check the corresponding button
        Preferences.FeedCardStyle savedStyle = PreferencesManager.getEnumPreference(SP_FEEDCARD_STYLE, PREFS_UI, Preferences.FeedCardStyle.class, SP_FEEDCARD_STYLE_DEFAULT, appContext);
        if (savedStyle.ordinal() < feedcardStyleButtons.size()) {
            onCardStyleChange(feedcardStyleButtons.get(savedStyle.ordinal()), feedcardStyleButtons);
        }

        boolean authorVisible = PreferencesManager.getBooleanPreference(SP_FEEDCARD_AUTHORVISIBLE, PREFS_UI, SP_FEEDCARD_AUTHORVISIBLE_DEFAULT, appContext);
        authorSwitch.setChecked(authorVisible);

        boolean authorName = PreferencesManager.getBooleanPreference(SP_FEEDCARD_AUTHORNAME, PREFS_FUNCTIONALITY, SP_FEEDCARD_AUTHORNAME_DEFAULT, appContext);
        authorNameSwitch.setChecked(authorName);

        boolean titleVisible = PreferencesManager.getBooleanPreference(SP_FEEDCARD_TITLEVISIBLE, PREFS_UI, SP_FEEDCARD_TITLEVISIBLE_DEFAULT, appContext);
        titleSwitch.setChecked(titleVisible);

        boolean descVisible = PreferencesManager.getBooleanPreference(SP_FEEDCARD_DESCVISIBLE, PREFS_UI, SP_FEEDCARD_DESCVISIBLE_DEFAULT, appContext);
        descSwitch.setChecked(descVisible);

        boolean dateVisible = PreferencesManager.getBooleanPreference(SP_FEEDCARD_DATEVISIBLE, PREFS_UI, SP_FEEDCARD_DATEVISIBLE_DEFAULT, appContext);
        dateSwitch.setChecked(dateVisible);

        dateSpinner.setSelection(PreferencesManager.getEnumPreference(SP_FEEDCARD_DATESTYLE, PREFS_LANG, DateStyle.class, SP_FEEDCARD_DATESTYLE_DEFAULT, appContext).ordinal());

    }

    private void onCardStyleChange(View button, List<View> buttonCollection) {
        Drawable checkedDrawable = AppCompatResources.getDrawable(appContext, R.drawable.checkbox_checked);

        Drawable uncheckedDrawable = AppCompatResources.getDrawable(appContext, R.drawable.checkbox_unchecked);

        boolean isChecked = Boolean.parseBoolean(button.getTag().toString());
        if (isChecked) {
            return;
        }

        // Uncheck all other buttons
        for (View view : buttonCollection) {
            if (view == button) {
                continue;
            }
            view.setTag(false);
            view.setBackground(uncheckedDrawable);
        }

        // Check the selected button
        button.setTag(true);
        button.setBackground(checkedDrawable);

        // Save the selected style
        Preferences.FeedCardStyle selectedStyle;
        int selectedIndex = buttonCollection.indexOf(button);
        switch (selectedIndex) {
            case 0:
                selectedStyle = Preferences.FeedCardStyle.LARGE;
                break;
            case 1:
                selectedStyle = Preferences.FeedCardStyle.SMALL;
                break;
            default:
                selectedStyle = Preferences.FeedCardStyle.NONE;
                break;
        }
        PreferencesManager.saveEnumPreference(SP_FEEDCARD_STYLE, PREFS_UI, selectedStyle, appContext);
    }
}