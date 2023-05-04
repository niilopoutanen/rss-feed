package com.niilopoutanen.rss_feed.customization;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.niilopoutanen.rss_feed.R;

import static com.niilopoutanen.rss_feed.customization.Preferences.*;


import java.util.Arrays;
import java.util.List;

public class SettingsFragment extends Fragment {

    private Context appContext;
    TextView themeSelected;
    TextView fontSelected;
    TextView launchwindowSelected;
    TextView articleColorSelected;
    SwitchCompat articlesInBrowser;
    SwitchCompat articleFullScreen;
    SwitchCompat imagecache;
    SwitchCompat haptics;

    List<RelativeLayout> colorAccentButtons;
    public SettingsFragment(Context context) {
        this.appContext = context;
    }
    public SettingsFragment(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(appContext == null){
            appContext = getContext();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        initializeElements(rootView);
        return rootView;
    }
    private void initializeElements(View rootView) {
        rootView.findViewById(R.id.copyright).setOnClickListener(v -> {
            String url = "https://github.com/niilopoutanen";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
        rootView.findViewById(R.id.settings_appicon).setOnClickListener(v -> {
            String url = "https://github.com/niilopoutanen/RSS-Feed";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });


        RelativeLayout feedSettings = rootView.findViewById(R.id.settings_openFeedSettings);
        feedSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsFeedFragment settingsFeedFragment = new SettingsFeedFragment(appContext);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                transaction.replace(R.id.frame_container, settingsFeedFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                PreferencesManager.vibrate(view, PreferencesManager.loadPreferences(appContext), appContext);
            }
        });
        ((RelativeLayout)rootView.findViewById(R.id.troubleshoot_haptics)).setOnLongClickListener(new View.OnLongClickListener() {
              @Override
              public boolean onLongClick(View v) {
                  SettingsHapticsFragment hapticsFragment = new SettingsHapticsFragment(appContext);
                  FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                  transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                  transaction.replace(R.id.frame_container, hapticsFragment);
                  transaction.addToBackStack(null);
                  PreferencesManager.vibrate(v, PreferencesManager.loadPreferences(appContext), HapticFeedbackConstants.LONG_PRESS, appContext);
                  transaction.commit();

                  return true;
              }
          });

                colorAccentButtons = Arrays.asList(
                        rootView.findViewById(R.id.checkboxblue_accentcolor),
                        rootView.findViewById(R.id.checkboxviolet_accentcolor),
                        rootView.findViewById(R.id.checkboxpink_accentcolor),
                        rootView.findViewById(R.id.checkboxred_accentcolor),
                        rootView.findViewById(R.id.checkboxorange_accentcolor),
                        rootView.findViewById(R.id.checkboxyellow_accentcolor),
                        rootView.findViewById(R.id.checkboxgreen_accentcolor)
                );



        for (int i = 0; i < colorAccentButtons.size(); i++) {
            int finalI = i;
            colorAccentButtons.get(i).setOnClickListener(v -> onColorAccentChange(colorAccentButtons.get(finalI), colorAccentButtons));
        }

        articlesInBrowser = rootView.findViewById(R.id.switch_articleinbrowser);
        articleFullScreen = rootView.findViewById(R.id.switch_articlefullscreen);
        haptics = rootView.findViewById(R.id.switch_haptics);
        imagecache = rootView.findViewById(R.id.switch_cache);

        themeSelected = rootView.findViewById(R.id.theme_selected);
        RelativeLayout themeSettings = rootView.findViewById(R.id.settings_themesettings);
        themeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDropDownSettings(ThemeMode.class, getString(R.string.settings_theme), appContext.getString(R.string.settings_theme_additional));
                PreferencesManager.vibrate(v, PreferencesManager.loadPreferences(appContext), appContext);
            }
        });



        fontSelected = rootView.findViewById(R.id.fontsettings_selected);
        RelativeLayout fontSettings = rootView.findViewById(R.id.settings_fontsettings);
        fontSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDropDownSettings(Font.class, getString(R.string.settings_font), "");
                PreferencesManager.vibrate(v, PreferencesManager.loadPreferences(appContext), appContext);
            }
        });


        launchwindowSelected = rootView.findViewById(R.id.launchwindow_selected);
        RelativeLayout launchWindowSettings = rootView.findViewById(R.id.settings_launchwindowsettings);
        launchWindowSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDropDownSettings(LaunchWindow.class, getString(R.string.settings_launchwindow), "");
                PreferencesManager.vibrate(v, PreferencesManager.loadPreferences(appContext), appContext);
            }
        });

        articleColorSelected = rootView.findViewById(R.id.articlecolor_selected);
        RelativeLayout articleColorSettings = rootView.findViewById(R.id.settings_articlecolorsettings);
        articleColorSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDropDownSettings(ArticleColor.class, getString(R.string.article_background_color), getString(R.string.article_background_color_desc));
                PreferencesManager.vibrate(v, PreferencesManager.loadPreferences(appContext), appContext);
            }
        });

        setSavedData();

        articlesInBrowser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.saveBooleanPreference(SP_ARTICLESINBROWSER, PREFS_FUNCTIONALITY, isChecked, appContext);
                PreferencesManager.vibrate(buttonView, PreferencesManager.loadPreferences(appContext), appContext);
            }
        });
        articleFullScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.saveBooleanPreference(SP_ARTICLEFULLSCREEN, PREFS_FUNCTIONALITY, isChecked, appContext);
                PreferencesManager.vibrate(buttonView, PreferencesManager.loadPreferences(appContext), appContext);
            }
        });
        haptics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.saveBooleanPreference(SP_HAPTICS, PREFS_FUNCTIONALITY, isChecked, appContext);
                PreferencesManager.vibrate(buttonView, PreferencesManager.loadPreferences(appContext), appContext);
            }
        });
        imagecache.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.saveBooleanPreference(SP_IMAGECACHE, PREFS_FUNCTIONALITY, isChecked, appContext);
                PreferencesManager.vibrate(buttonView, PreferencesManager.loadPreferences(appContext), appContext);
            }
        });

        //material you
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.S){
            rootView.findViewById(R.id.settings_colortile).setVisibility(View.GONE);
        }
        //no dark theme support
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            rootView.findViewById(R.id.settings_themesettings).setVisibility(View.GONE);
        }
    }
    private <T extends Enum<T>> void openDropDownSettings(Class<?> type, String title, String additionalMessage){
        SettingsDropDownFragment dropDownFragment = new SettingsDropDownFragment(title, additionalMessage, type, appContext);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.frame_container, dropDownFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void setSavedData() {

        // Load saved ColorAccent enum value and check the corresponding button
        ColorAccent savedAccent = PreferencesManager.getEnumPreference(SP_COLORACCENT, PREFS_UI, ColorAccent.class, SP_COLORACCENT_DEFAULT, appContext);
        if (savedAccent.ordinal() < colorAccentButtons.size()) {
            onColorAccentChange(colorAccentButtons.get(savedAccent.ordinal()), colorAccentButtons);
        }

        LaunchWindow selectedWindow = PreferencesManager.getEnumPreference(SP_LAUNCHWINDOW, PREFS_FUNCTIONALITY, LaunchWindow.class, SP_LAUNCHWINDOW_DEFAULT, appContext);
        launchwindowSelected.setText(getResources().getStringArray(R.array.launch_windows)[selectedWindow.ordinal()]);

        Font selectedFont = PreferencesManager.getEnumPreference(SP_FONT, PREFS_LANG, Font.class, SP_FONT_DEFAULT, appContext);
        fontSelected.setText(getResources().getStringArray(R.array.fonts)[selectedFont.ordinal()]);

        ThemeMode selectedTheme = PreferencesManager.getEnumPreference(SP_THEME, PREFS_UI, ThemeMode.class, SP_THEME_DEFAULT, appContext);
        themeSelected.setText(getResources().getStringArray(R.array.theme_modes)[selectedTheme.ordinal()]);

        ArticleColor selectedColor = PreferencesManager.getEnumPreference(SP_ARTICLECOLOR, PREFS_UI, ArticleColor.class, SP_ARTICLECOLOR_DEFAULT, appContext);
        articleColorSelected.setText(getResources().getStringArray(R.array.article_backgrounds)[selectedColor.ordinal()]);

        articlesInBrowser.setChecked(PreferencesManager.getBooleanPreference(SP_ARTICLESINBROWSER, PREFS_FUNCTIONALITY, SP_ARTICLESINBROWSER_DEFAULT, appContext));

        articleFullScreen.setChecked(PreferencesManager.getBooleanPreference(SP_ARTICLEFULLSCREEN, PREFS_FUNCTIONALITY, SP_ARTICLEFULLSCREEN_DEFAULT, appContext));

        haptics.setChecked(PreferencesManager.getBooleanPreference(SP_HAPTICS, PREFS_FUNCTIONALITY, SP_HAPTICS_DEFAULT, appContext));

        imagecache.setChecked(PreferencesManager.getBooleanPreference(SP_IMAGECACHE, PREFS_FUNCTIONALITY, SP_IMAGECACHE_DEFAULT, appContext));

    }

    private void onColorAccentChange(RelativeLayout button, List<RelativeLayout> buttonCollection) {
        Drawable circle = AppCompatResources.getDrawable(appContext, R.drawable.icon_checkmark);

        boolean isChecked = Boolean.parseBoolean(button.getTag().toString());
        if (isChecked) {
            return;
        }

        // Uncheck all other buttons
        for (RelativeLayout otherButton : buttonCollection) {
            if (otherButton == button) {
                continue;
            }
            otherButton.setTag(false);
            otherButton.removeAllViews();
        }

        // Check the selected button
        button.setTag(true);
        View view = new View(appContext);
        view.setBackground(circle);
        button.addView(view);

        // Save the selected style
        ColorAccent selectedColor;
        int selectedIndex = buttonCollection.indexOf(button);
        switch (selectedIndex) {
            default:
                selectedColor = ColorAccent.BLUE;
                break;
            case 1:
                selectedColor = ColorAccent.VIOLET;
                break;
            case 2:
                selectedColor = ColorAccent.PINK;
                break;
            case 3:
                selectedColor = ColorAccent.RED;
                break;
            case 4:
                selectedColor = ColorAccent.ORANGE;
                break;
            case 5:
                selectedColor = ColorAccent.YELLOW;
                break;
            case 6:
                selectedColor = ColorAccent.GREEN;
                break;
        }
        PreferencesManager.saveEnumPreference(SP_COLORACCENT, PREFS_UI, selectedColor, appContext);
    }

}