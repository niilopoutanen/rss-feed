package com.niilopoutanen.rss_feed.fragments;

import static com.niilopoutanen.rss_feed.common.models.Preferences.DateStyle;
import static com.niilopoutanen.rss_feed.common.models.Preferences.PREFS_LANG;
import static com.niilopoutanen.rss_feed.common.models.Preferences.PREFS_UI;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FEEDCARD_AUTHORVISIBLE;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FEEDCARD_AUTHORVISIBLE_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FEEDCARD_DATESTYLE;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FEEDCARD_DATESTYLE_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FEEDCARD_DATEVISIBLE;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FEEDCARD_DATEVISIBLE_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FEEDCARD_DESCVISIBLE;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FEEDCARD_DESCVISIBLE_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FEEDCARD_FULL_TITLEVISIBLE;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FEEDCARD_FULL_TITLEVISIBLE_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FEEDCARD_STYLE;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FEEDCARD_STYLE_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FEEDCARD_TITLEVISIBLE;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FEEDCARD_TITLEVISIBLE_DEFAULT;

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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.transition.MaterialSharedAxis;
import com.niilopoutanen.rss_feed.common.PreferencesManager;
import com.niilopoutanen.rss_feed.resources.R;
import com.niilopoutanen.rss_feed.common.models.Preferences;

import java.util.Arrays;
import java.util.List;

public class SettingsFeedFragment extends Fragment {

    List<View> feedcardStyleButtons;
    private SwitchCompat authorSwitch, titleSwitch, fullTitleSwitch, descSwitch, dateSwitch;
    private Spinner dateSpinner;
    private Context context;

    public SettingsFeedFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings_feed, container, false);
        context = rootView.getContext();

        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.feedsettings_base), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        LinearLayout returnBtn = rootView.findViewById(R.id.feedsettings_return);
        returnBtn.setOnClickListener(view -> {
            PreferencesManager.vibrate(view);
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
        titleSwitch = rootView.findViewById(R.id.switch_title);
        fullTitleSwitch = rootView.findViewById(R.id.switch_full_title);
        descSwitch = rootView.findViewById(R.id.switch_description);
        dateSwitch = rootView.findViewById(R.id.switch_date);

        String[] dateModes = context.getResources().getStringArray(com.niilopoutanen.rss_feed.resources.R.array.date_modes);
        dateSpinner = rootView.findViewById(R.id.spinner_date);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, dateModes);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        dateSpinner.setAdapter(adapter);

        loadSavedData();

        for (int i = 0; i < feedcardContainers.size(); i++) {
            int finalI = i;
            feedcardContainers.get(i).setOnClickListener(v -> onCardStyleChange(feedcardStyleButtons.get(finalI), feedcardStyleButtons));
        }

        authorSwitch.setOnCheckedChangeListener((compoundButton, b) -> PreferencesManager.saveBooleanPreference(SP_FEEDCARD_AUTHORVISIBLE, PREFS_UI, b, context));
        titleSwitch.setOnCheckedChangeListener((compoundButton, b) -> PreferencesManager.saveBooleanPreference(SP_FEEDCARD_TITLEVISIBLE, PREFS_UI, b, context));
        fullTitleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> PreferencesManager.saveBooleanPreference(SP_FEEDCARD_FULL_TITLEVISIBLE, PREFS_UI, isChecked, context));
        descSwitch.setOnCheckedChangeListener((compoundButton, b) -> PreferencesManager.saveBooleanPreference(SP_FEEDCARD_DESCVISIBLE, PREFS_UI, b, context));
        dateSwitch.setOnCheckedChangeListener((compoundButton, b) -> PreferencesManager.saveBooleanPreference(SP_FEEDCARD_DATEVISIBLE, PREFS_UI, b, context));


        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                DateStyle themeMode = DateStyle.values()[position];
                PreferencesManager.saveEnumPreference(SP_FEEDCARD_DATESTYLE, PREFS_LANG, themeMode, context);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return rootView;
    }

    private void loadSavedData() {
        // Load saved FeedCardStyle enum value and check the corresponding button
        Preferences.FeedCardStyle savedStyle = PreferencesManager.getEnumPreference(SP_FEEDCARD_STYLE, PREFS_UI, Preferences.FeedCardStyle.class, SP_FEEDCARD_STYLE_DEFAULT, context);
        if (savedStyle.ordinal() < feedcardStyleButtons.size()) {
            onCardStyleChange(feedcardStyleButtons.get(savedStyle.ordinal()), feedcardStyleButtons);
        }

        boolean authorVisible = PreferencesManager.getBooleanPreference(SP_FEEDCARD_AUTHORVISIBLE, PREFS_UI, SP_FEEDCARD_AUTHORVISIBLE_DEFAULT, context);
        authorSwitch.setChecked(authorVisible);

        boolean titleVisible = PreferencesManager.getBooleanPreference(SP_FEEDCARD_TITLEVISIBLE, PREFS_UI, SP_FEEDCARD_TITLEVISIBLE_DEFAULT, context);
        titleSwitch.setChecked(titleVisible);

        boolean fullTitleVisible = PreferencesManager.getBooleanPreference(SP_FEEDCARD_FULL_TITLEVISIBLE, PREFS_UI, SP_FEEDCARD_FULL_TITLEVISIBLE_DEFAULT, context);
        fullTitleSwitch.setChecked(fullTitleVisible);

        boolean descVisible = PreferencesManager.getBooleanPreference(SP_FEEDCARD_DESCVISIBLE, PREFS_UI, SP_FEEDCARD_DESCVISIBLE_DEFAULT, context);
        descSwitch.setChecked(descVisible);

        boolean dateVisible = PreferencesManager.getBooleanPreference(SP_FEEDCARD_DATEVISIBLE, PREFS_UI, SP_FEEDCARD_DATEVISIBLE_DEFAULT, context);
        dateSwitch.setChecked(dateVisible);

        dateSpinner.setSelection(PreferencesManager.getEnumPreference(SP_FEEDCARD_DATESTYLE, PREFS_LANG, DateStyle.class, SP_FEEDCARD_DATESTYLE_DEFAULT, context).ordinal());

    }

    private void onCardStyleChange(View button, List<View> buttonCollection) {
        Drawable checkedDrawable = AppCompatResources.getDrawable(context, com.niilopoutanen.rss_feed.resources.R.drawable.checkbox_checked);

        Drawable uncheckedDrawable = AppCompatResources.getDrawable(context, com.niilopoutanen.rss_feed.resources.R.drawable.checkbox_unchecked);

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
        PreferencesManager.saveEnumPreference(SP_FEEDCARD_STYLE, PREFS_UI, selectedStyle, context);
    }
}