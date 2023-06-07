package com.niilopoutanen.rss_feed.fragments;

import static com.niilopoutanen.rss_feed.models.Preferences.ColorAccent;
import static com.niilopoutanen.rss_feed.models.Preferences.Font;
import static com.niilopoutanen.rss_feed.models.Preferences.LaunchWindow;
import static com.niilopoutanen.rss_feed.models.Preferences.PREFS_FUNCTIONALITY;
import static com.niilopoutanen.rss_feed.models.Preferences.PREFS_LANG;
import static com.niilopoutanen.rss_feed.models.Preferences.PREFS_UI;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_ARTICLEFULLSCREEN;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_ARTICLEFULLSCREEN_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_ARTICLESINBROWSER;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_ARTICLESINBROWSER_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_COLORACCENT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_COLORACCENT_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FONT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FONT_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_HAPTICS;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_HAPTICS_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_IMAGECACHE;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_IMAGECACHE_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_LAUNCHWINDOW;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_LAUNCHWINDOW_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_THEME;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_THEME_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.ThemeMode;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.niilopoutanen.rss_feed.BuildConfig;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

import java.util.Arrays;
import java.util.List;

public class SettingsFragment extends Fragment {

    TextView themeSelected;
    TextView fontSelected;
    TextView launchwindowSelected;
    SwitchCompat articlesInBrowser;
    SwitchCompat articleFullScreen;
    SwitchCompat imagecache;
    SwitchCompat haptics;
    List<RelativeLayout> colorAccentButtons;
    private Context appContext;

    public SettingsFragment(Context context) {
        this.appContext = context;
    }

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (appContext == null) {
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
        rootView.findViewById(R.id.settings_createIssue).setOnClickListener(v -> {
            String url = "https://github.com/niilopoutanen/RSS-Feed/issues/new";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
        rootView.findViewById(R.id.settings_sendMail).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"niilo.poutanen@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback from RSS-Feed");
            intent.putExtra(Intent.EXTRA_TEXT, appContext.getString(R.string.emailquide) + "\n \n App version: " + BuildConfig.VERSION_NAME + "\n Android version: " + Build.VERSION.SDK_INT);
            try {
                startActivity(Intent.createChooser(intent, appContext.getString(R.string.sendmail)));
            } catch (Exception e) {
                Toast.makeText(appContext, appContext.getString(R.string.noemailfound), Toast.LENGTH_LONG).show();
            }
        });


        RelativeLayout feedSettings = rootView.findViewById(R.id.settings_openFeedSettings);
        feedSettings.setOnClickListener(view -> {
            SettingsFeedFragment settingsFeedFragment = new SettingsFeedFragment(appContext);
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.replace(R.id.frame_container, settingsFeedFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            PreferencesManager.vibrate(view, PreferencesManager.loadPreferences(appContext), appContext);
        });
        rootView.findViewById(R.id.troubleshoot_haptics).setOnLongClickListener(v -> {
            SettingsHapticsFragment hapticsFragment = new SettingsHapticsFragment(appContext);
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.replace(R.id.frame_container, hapticsFragment);
            transaction.addToBackStack(null);
            PreferencesManager.vibrate(v, PreferencesManager.loadPreferences(appContext), HapticFeedbackConstants.LONG_PRESS, appContext);
            transaction.commit();

            return true;
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
        themeSettings.setOnClickListener(v -> {
            openDropDownSettings(ThemeMode.class, getString(R.string.settings_theme), appContext.getString(R.string.settings_theme_additional));
            PreferencesManager.vibrate(v, PreferencesManager.loadPreferences(appContext), appContext);
        });


        fontSelected = rootView.findViewById(R.id.fontsettings_selected);
        RelativeLayout fontSettings = rootView.findViewById(R.id.settings_fontsettings);
        fontSettings.setOnClickListener(v -> {
            openDropDownSettings(Font.class, getString(R.string.settings_font), "");
            PreferencesManager.vibrate(v, PreferencesManager.loadPreferences(appContext), appContext);
        });


        launchwindowSelected = rootView.findViewById(R.id.launchwindow_selected);
        RelativeLayout launchWindowSettings = rootView.findViewById(R.id.settings_launchwindowsettings);
        launchWindowSettings.setOnClickListener(v -> {
            openDropDownSettings(LaunchWindow.class, getString(R.string.settings_launchwindow), "");
            PreferencesManager.vibrate(v, PreferencesManager.loadPreferences(appContext), appContext);
        });

        setSavedData();

        articlesInBrowser.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesManager.saveBooleanPreference(SP_ARTICLESINBROWSER, PREFS_FUNCTIONALITY, isChecked, appContext);
            PreferencesManager.vibrate(buttonView, PreferencesManager.loadPreferences(appContext), appContext);
        });
        articleFullScreen.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesManager.saveBooleanPreference(SP_ARTICLEFULLSCREEN, PREFS_FUNCTIONALITY, isChecked, appContext);
            PreferencesManager.vibrate(buttonView, PreferencesManager.loadPreferences(appContext), appContext);
        });
        haptics.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesManager.saveBooleanPreference(SP_HAPTICS, PREFS_FUNCTIONALITY, isChecked, appContext);
            PreferencesManager.vibrate(buttonView, PreferencesManager.loadPreferences(appContext), appContext);
        });
        imagecache.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesManager.saveBooleanPreference(SP_IMAGECACHE, PREFS_FUNCTIONALITY, isChecked, appContext);
            PreferencesManager.vibrate(buttonView, PreferencesManager.loadPreferences(appContext), appContext);
        });

        //material you
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
            rootView.findViewById(R.id.settings_colortile).setVisibility(View.GONE);
        }
        //no dark theme support
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            rootView.findViewById(R.id.settings_themesettings).setVisibility(View.GONE);
        }
    }

    private <T extends Enum<T>> void openDropDownSettings(Class<?> type, String title, String additionalMessage) {
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