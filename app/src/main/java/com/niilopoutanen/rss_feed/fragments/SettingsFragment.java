package com.niilopoutanen.rss_feed.fragments;

import static com.niilopoutanen.rss_feed.common.models.Preferences.Font;
import static com.niilopoutanen.rss_feed.common.models.Preferences.LaunchWindow;
import static com.niilopoutanen.rss_feed.common.models.Preferences.PREFS_FUNCTIONALITY;
import static com.niilopoutanen.rss_feed.common.models.Preferences.PREFS_LANG;
import static com.niilopoutanen.rss_feed.common.models.Preferences.PREFS_UI;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_ANIMATE_CLICKS;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_ANIMATE_CLICKS_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_ARTICLEFULLSCREEN;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_ARTICLEFULLSCREEN_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_ARTICLESINBROWSER;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_ARTICLESINBROWSER_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_ARTICLE_SHOW_CATEGORIES;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_ARTICLE_SHOW_CATEGORIES_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_ARTICLE_SHOW_CONTROLS;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_ARTICLE_SHOW_CONTROLS_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FONT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FONTSIZE;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FONTSIZE_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FONT_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_HAPTICS;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_HAPTICS_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_HEADERSIZE;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_HEADERSIZE_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_HEADERTYPE;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_HEADERTYPE_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_IMAGECACHE;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_IMAGECACHE_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_IMAGE_VIEWER_GRADIENT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_IMAGE_VIEWER_GRADIENT_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_LAUNCHWINDOW;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_LAUNCHWINDOW_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_REMEMBER_SORTING;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_REMEMBER_SORTING_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_SHOW_CHANGELOG;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_SHOW_CHANGELOG_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_SHOW_SEARCH;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_SHOW_SEARCH_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_SORTING_MODE;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_SORTING_MODE_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_THEME;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_THEME_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.ThemeMode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.google.android.material.slider.Slider;
import com.google.android.material.transition.MaterialFadeThrough;
import com.google.android.material.transition.MaterialSharedAxis;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.niilopoutanen.rss_feed.BuildConfig;
import com.niilopoutanen.rss_feed.activities.DebugActivity;
import com.niilopoutanen.rss_feed.common.PreferencesManager;
import com.niilopoutanen.rss_feed.resources.R;
import com.niilopoutanen.rss_feed.common.models.Preferences;
import com.niilopoutanen.rss_feed.database.AppRepository;
import com.niilopoutanen.rss_feed.manager.ImportActivity;
import com.niilopoutanen.rss_feed.rss.Opml;
import com.niilopoutanen.rss_feed.rss.Source;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class SettingsFragment extends Fragment {

    TextView themeSelected, fontSelected, launchWindowSelected, headerTypeSelected, headerSizeSelected, sortingModeSelected;
    SwitchCompat articlesInBrowser,
            searchVisible, articleFullScreen, articleShowControls,
            articleShowCategories, imageViewerGradient, rememberSortingMode,
            imageCache, animateClicks, haptics, showChangelog;
    Slider fontSizeSlider;
    private Context context;
    private ActivityResultLauncher<Intent> createDocumentLauncher;


    public SettingsFragment() {}

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        setEnterTransition(new MaterialFadeThrough());
        setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));


        createDocumentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK){
                    AppRepository repository = new AppRepository(context);
                    repository.getAllSources().observe(SettingsFragment.this.getViewLifecycleOwner(), new Observer<List<Source>>() {
                        @Override
                        public void onChanged(List<Source> sources) {
                            String content = Opml.encode(sources);
                            Intent data = result.getData();
                            if (data == null) return;

                            Uri uri = data.getData();
                            if (uri == null) return;

                            try {
                                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                                if (outputStream != null) {
                                    outputStream.write(content.getBytes());
                                    outputStream.close();
                                    Toast.makeText(context, getResources().getQuantityString(com.niilopoutanen.rss_feed.resources.R.plurals.exported_sources, sources.size(), sources.size()), Toast.LENGTH_SHORT).show();
                                    repository.getAllSources().removeObserver(this);
                                }
                            } catch (IOException e) {
                                Toast.makeText(context, R.string.error_export_sources, Toast.LENGTH_SHORT).show();
                                FirebaseCrashlytics.getInstance().recordException(e);
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        initializeElements(rootView);
        return rootView;
    }

    private void initializeElements(View rootView) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.settings_container), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            int defaultPadding = PreferencesManager.dpToPx(10, context);
            v.setPadding(defaultPadding, insets.top, defaultPadding, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        PreferencesManager.setHeader(context, rootView.findViewById(R.id.settings_header));

        String versionText = "v" + BuildConfig.VERSION_NAME;
        ((TextView) rootView.findViewById(R.id.settings_version)).setText(versionText);

        View copyright = rootView.findViewById(R.id.copyright);
        copyright.setOnClickListener(v -> {
            String url = "https://github.com/niilopoutanen";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
        copyright.setOnLongClickListener(v -> {
            Intent debugIntent = new Intent(context, DebugActivity.class);
            context.startActivity(debugIntent);
            return true;
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
            intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.emailquide) + "\n \n App version: " + BuildConfig.VERSION_NAME + "\n Android version: " + Build.VERSION.SDK_INT);
            try {
                startActivity(Intent.createChooser(intent, context.getString(R.string.sendmail)));
            } catch (Exception e) {
                Toast.makeText(context, context.getString(R.string.error_no_email_found), Toast.LENGTH_LONG).show();
            }
        });


        RelativeLayout feedSettings = rootView.findViewById(R.id.settings_openFeedSettings);
        feedSettings.setOnClickListener(view -> {
            SettingsFeedFragment settingsFeedFragment = new SettingsFeedFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, settingsFeedFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            PreferencesManager.vibrate(view);
        });


        articlesInBrowser = rootView.findViewById(R.id.switch_articleinbrowser);
        articleFullScreen = rootView.findViewById(R.id.switch_articlefullscreen);
        articleShowControls = rootView.findViewById(R.id.switch_article_showcontrols);
        articleShowCategories = rootView.findViewById(R.id.switch_article_showcategories);
        imageViewerGradient = rootView.findViewById(R.id.switch_image_viewer_gradient);
        rememberSortingMode = rootView.findViewById(R.id.switch_remember_sorting);
        searchVisible = rootView.findViewById(R.id.switch_show_search);
        haptics = rootView.findViewById(R.id.switch_haptics);
        imageCache = rootView.findViewById(R.id.switch_cache);
        animateClicks = rootView.findViewById(R.id.switch_animateclicks);
        showChangelog = rootView.findViewById(R.id.switch_showchangelog);
        fontSizeSlider = rootView.findViewById(R.id.slider_fontsize);

        themeSelected = rootView.findViewById(R.id.theme_selected);
        RelativeLayout themeSettings = rootView.findViewById(R.id.settings_themesettings);
        themeSettings.setOnClickListener(v -> {
            openDropDownSettings(ThemeMode.class, getString(R.string.settings_theme), context.getString(R.string.settings_theme_additional));
            PreferencesManager.vibrate(v);
        });


        fontSelected = rootView.findViewById(R.id.fontsettings_selected);
        RelativeLayout fontSettings = rootView.findViewById(R.id.settings_fontsettings);
        fontSettings.setOnClickListener(v -> {
            openDropDownSettings(Font.class, getString(R.string.settings_font), "");
            PreferencesManager.vibrate(v);
        });


        launchWindowSelected = rootView.findViewById(R.id.launchwindow_selected);
        RelativeLayout launchWindowSettings = rootView.findViewById(R.id.settings_launchwindowsettings);
        launchWindowSettings.setOnClickListener(v -> {
            openDropDownSettings(LaunchWindow.class, getString(R.string.settings_launchwindow), getString(R.string.settings_launchwindow_additional));
            PreferencesManager.vibrate(v);
        });

        headerTypeSelected = rootView.findViewById(R.id.headertype_selected);
        RelativeLayout headerTypeSettings = rootView.findViewById(R.id.settings_headertypesettings);
        headerTypeSettings.setOnClickListener(v -> {
            openDropDownSettings(Preferences.HeaderType.class, getString(R.string.settings_headertype), "");
            PreferencesManager.vibrate(v);
        });

        headerSizeSelected = rootView.findViewById(R.id.headersize_selected);
        RelativeLayout headerSizeSettings = rootView.findViewById(R.id.settings_headersizesettings);
        headerSizeSettings.setOnClickListener(v -> {
            openDropDownSettings(Preferences.HeaderSize.class, context.getString(R.string.settings_headersize), "");
            PreferencesManager.vibrate(v);
        });

        sortingModeSelected = rootView.findViewById(R.id.sorting_mode_selected);
        RelativeLayout sortingModeSettings = rootView.findViewById(R.id.settings_sorting_mode);
        sortingModeSettings.setOnClickListener(v -> {
            openDropDownSettings(Preferences.SortingMode.class, context.getString(R.string.sorting_mode), "");
            PreferencesManager.vibrate(v);
        });




        RelativeLayout export = rootView.findViewById(R.id.settings_export);
        export.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TITLE, context.getString(R.string.rssfeed_sources));
            if(createDocumentLauncher != null){
                createDocumentLauncher.launch(intent);
            }
        });



        RelativeLayout imp = rootView.findViewById(R.id.settings_import);
        imp.setOnClickListener(v -> {
            Intent importIntent = new Intent(context, ImportActivity.class);
            startActivity(importIntent);
        });

        setSavedData();

        articlesInBrowser.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesManager.saveBooleanPreference(SP_ARTICLESINBROWSER, PREFS_FUNCTIONALITY, isChecked, context);
            PreferencesManager.vibrate(buttonView);
        });
        articleFullScreen.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesManager.saveBooleanPreference(SP_ARTICLEFULLSCREEN, PREFS_FUNCTIONALITY, isChecked, context);
            PreferencesManager.vibrate(buttonView);
        });
        articleShowControls.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesManager.saveBooleanPreference(SP_ARTICLE_SHOW_CONTROLS, PREFS_UI, isChecked, context);
            PreferencesManager.vibrate(buttonView);
        });
        articleShowCategories.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesManager.saveBooleanPreference(SP_ARTICLE_SHOW_CATEGORIES, PREFS_UI, isChecked, context);
            PreferencesManager.vibrate(buttonView);
        });
        imageViewerGradient.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesManager.saveBooleanPreference(SP_IMAGE_VIEWER_GRADIENT, PREFS_UI, isChecked, context);
            PreferencesManager.vibrate(buttonView);
        });
        searchVisible.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesManager.saveBooleanPreference(SP_SHOW_SEARCH, PREFS_UI, isChecked, context);
            PreferencesManager.vibrate(buttonView);
        });
        rememberSortingMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesManager.saveBooleanPreference(SP_REMEMBER_SORTING, PREFS_FUNCTIONALITY, isChecked, context);
            PreferencesManager.vibrate(buttonView);
        });
        haptics.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesManager.saveBooleanPreference(SP_HAPTICS, PREFS_FUNCTIONALITY, isChecked, context);
            PreferencesManager.vibrate(buttonView);
        });
        imageCache.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesManager.saveBooleanPreference(SP_IMAGECACHE, PREFS_FUNCTIONALITY, isChecked, context);
            PreferencesManager.vibrate(buttonView);
        });
        animateClicks.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesManager.saveBooleanPreference(SP_ANIMATE_CLICKS, PREFS_FUNCTIONALITY, isChecked, context);
            PreferencesManager.vibrate(buttonView);
        });
        showChangelog.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesManager.saveBooleanPreference(SP_SHOW_CHANGELOG, PREFS_FUNCTIONALITY, isChecked, context);
            PreferencesManager.vibrate(buttonView);
        });
        fontSizeSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                PreferencesManager.saveIntPreference(SP_FONTSIZE, PREFS_FUNCTIONALITY, (int) slider.getValue(), context);
            }
        });

        //no dark theme support
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            rootView.findViewById(R.id.settings_themesettings).setVisibility(View.GONE);
        }
    }

    private void openDropDownSettings(Class<?> type, String title, String additionalMessage) {
        SettingsDropDownFragment dropDownFragment = SettingsDropDownFragment.newInstance(title, additionalMessage, type);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, dropDownFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setSavedData() {
        LaunchWindow selectedWindow = PreferencesManager.getEnumPreference(SP_LAUNCHWINDOW, PREFS_FUNCTIONALITY, LaunchWindow.class, SP_LAUNCHWINDOW_DEFAULT, context);
        launchWindowSelected.setText(getResources().getStringArray(R.array.launch_windows)[selectedWindow.ordinal()]);

        Font selectedFont = PreferencesManager.getEnumPreference(SP_FONT, PREFS_LANG, Font.class, SP_FONT_DEFAULT, context);
        fontSelected.setText(getResources().getStringArray(R.array.fonts)[selectedFont.ordinal()]);

        ThemeMode selectedTheme = PreferencesManager.getEnumPreference(SP_THEME, PREFS_UI, ThemeMode.class, SP_THEME_DEFAULT, context);
        themeSelected.setText(getResources().getStringArray(R.array.theme_modes)[selectedTheme.ordinal()]);

        Preferences.HeaderType selectedHeader = PreferencesManager.getEnumPreference(SP_HEADERTYPE, PREFS_UI, Preferences.HeaderType.class, SP_HEADERTYPE_DEFAULT, context);
        headerTypeSelected.setText(getResources().getStringArray(R.array.header_types)[selectedHeader.ordinal()]);

        Preferences.HeaderSize selectedHeaderSize = PreferencesManager.getEnumPreference(SP_HEADERSIZE, PREFS_UI, Preferences.HeaderSize.class, SP_HEADERSIZE_DEFAULT, context);
        headerSizeSelected.setText(getResources().getStringArray(R.array.header_sizes)[selectedHeaderSize.ordinal()]);

        Preferences.SortingMode selectedSorting = PreferencesManager.getEnumPreference(SP_SORTING_MODE, PREFS_FUNCTIONALITY, Preferences.SortingMode.class, SP_SORTING_MODE_DEFAULT, context);
        sortingModeSelected.setText(getResources().getStringArray(R.array.sorting_modes)[selectedSorting.ordinal()]);

        articlesInBrowser.setChecked(PreferencesManager.getBooleanPreference(SP_ARTICLESINBROWSER, PREFS_FUNCTIONALITY, SP_ARTICLESINBROWSER_DEFAULT, context));

        articleFullScreen.setChecked(PreferencesManager.getBooleanPreference(SP_ARTICLEFULLSCREEN, PREFS_FUNCTIONALITY, SP_ARTICLEFULLSCREEN_DEFAULT, context));

        articleShowControls.setChecked(PreferencesManager.getBooleanPreference(SP_ARTICLE_SHOW_CONTROLS, PREFS_UI, SP_ARTICLE_SHOW_CONTROLS_DEFAULT, context));

        articleShowCategories.setChecked(PreferencesManager.getBooleanPreference(SP_ARTICLE_SHOW_CATEGORIES, PREFS_UI, SP_ARTICLE_SHOW_CATEGORIES_DEFAULT, context));

        imageViewerGradient.setChecked(PreferencesManager.getBooleanPreference(SP_IMAGE_VIEWER_GRADIENT, PREFS_UI, SP_IMAGE_VIEWER_GRADIENT_DEFAULT, context));

        rememberSortingMode.setChecked(PreferencesManager.getBooleanPreference(SP_REMEMBER_SORTING, PREFS_FUNCTIONALITY, SP_REMEMBER_SORTING_DEFAULT, context));

        searchVisible.setChecked(PreferencesManager.getBooleanPreference(SP_SHOW_SEARCH, PREFS_UI, SP_SHOW_SEARCH_DEFAULT, context));

        haptics.setChecked(PreferencesManager.getBooleanPreference(SP_HAPTICS, PREFS_FUNCTIONALITY, SP_HAPTICS_DEFAULT, context));

        imageCache.setChecked(PreferencesManager.getBooleanPreference(SP_IMAGECACHE, PREFS_FUNCTIONALITY, SP_IMAGECACHE_DEFAULT, context));

        animateClicks.setChecked(PreferencesManager.getBooleanPreference(SP_ANIMATE_CLICKS, PREFS_FUNCTIONALITY, SP_ANIMATE_CLICKS_DEFAULT, context));

        showChangelog.setChecked(PreferencesManager.getBooleanPreference(SP_SHOW_CHANGELOG, PREFS_FUNCTIONALITY, SP_SHOW_CHANGELOG_DEFAULT, context));

        fontSizeSlider.setValue(PreferencesManager.getIntPreference(SP_FONTSIZE, PREFS_FUNCTIONALITY, SP_FONTSIZE_DEFAULT, context));

    }
}