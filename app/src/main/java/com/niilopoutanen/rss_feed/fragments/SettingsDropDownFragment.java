package com.niilopoutanen.rss_feed.fragments;

import static com.niilopoutanen.rss_feed.common.models.Preferences.Font;
import static com.niilopoutanen.rss_feed.common.models.Preferences.LaunchWindow;
import static com.niilopoutanen.rss_feed.common.models.Preferences.PREFS_FUNCTIONALITY;
import static com.niilopoutanen.rss_feed.common.models.Preferences.PREFS_LANG;
import static com.niilopoutanen.rss_feed.common.models.Preferences.PREFS_UI;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FONT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_FONT_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_HEADERSIZE;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_HEADERSIZE_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_HEADERTYPE;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_HEADERTYPE_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_LAUNCHWINDOW;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_LAUNCHWINDOW_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_SORTING_MODE;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_SORTING_MODE_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_THEME;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_THEME_DEFAULT;
import static com.niilopoutanen.rss_feed.common.models.Preferences.ThemeMode;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.transition.MaterialSharedAxis;
import com.niilopoutanen.rss_feed.common.PreferencesManager;
import com.niilopoutanen.rss_feed.resources.R;
import com.niilopoutanen.rss_feed.common.models.Preferences;

public class SettingsDropDownFragment extends Fragment {

    LinearLayout optionsContainer;
    Context context;
    Class<?> type;
    String title;
    String additionalMessage;

    public static SettingsDropDownFragment newInstance(String optionTitle, String additionalMessage, Class<?> type) {
        Bundle args = new Bundle();
        args.putString("title", optionTitle);
        args.putString("message", additionalMessage);
        args.putSerializable("class", type);
        SettingsDropDownFragment fragment = new SettingsDropDownFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public SettingsDropDownFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            title = getArguments().getString("title");
            additionalMessage = getArguments().getString("message");
            type = (Class<?>) getArguments().getSerializable("class");
        }
        if (context == null) {
            context = getContext();
        }
        super.onCreate(savedInstanceState);

        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings_dropdown, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.dropdownsettings_base), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });


        optionsContainer = rootView.findViewById(R.id.settings_dropdown_options);
        addOptions(type);

        ((TextView) rootView.findViewById(R.id.settings_dropdown_title)).setText(title);
        ((TextView) rootView.findViewById(R.id.dropdownsettings_details)).setText(additionalMessage);

        LinearLayout returnBtn = rootView.findViewById(R.id.dropdownsettings_return);

        returnBtn.setOnClickListener(view -> closeFragment(returnBtn));
        return rootView;
    }

    private boolean verifySelected(int index, Class<?> type) {
        if (LaunchWindow.class.equals(type)) {
            LaunchWindow selected = PreferencesManager.getEnumPreference(SP_LAUNCHWINDOW, PREFS_FUNCTIONALITY, LaunchWindow.class, SP_LAUNCHWINDOW_DEFAULT, context);
            return index == selected.ordinal();
        }
        if (Font.class.equals(type)) {
            Font selected = PreferencesManager.getEnumPreference(SP_FONT, PREFS_LANG, Font.class, SP_FONT_DEFAULT, context);
            return index == selected.ordinal();
        }
        if (Preferences.HeaderType.class.equals(type)) {
            Preferences.HeaderType selected = PreferencesManager.getEnumPreference(SP_HEADERTYPE, PREFS_UI, Preferences.HeaderType.class, SP_HEADERTYPE_DEFAULT, context);
            return index == selected.ordinal();
        }
        if (Preferences.HeaderSize.class.equals(type)) {
            Preferences.HeaderSize selected = PreferencesManager.getEnumPreference(SP_HEADERSIZE, PREFS_UI, Preferences.HeaderSize.class, SP_HEADERSIZE_DEFAULT, context);
            return index == selected.ordinal();
        }
        if (Preferences.SortingMode.class.equals(type)) {
            Preferences.SortingMode selected = PreferencesManager.getEnumPreference(SP_SORTING_MODE, PREFS_FUNCTIONALITY, Preferences.SortingMode.class, SP_SORTING_MODE_DEFAULT, context);
            return index == selected.ordinal();
        }
        if (ThemeMode.class.equals(type)) {
            ThemeMode selected = PreferencesManager.getEnumPreference(SP_THEME, PREFS_UI, ThemeMode.class, SP_THEME_DEFAULT, context);
            switch (selected) {
                case DARK:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                case LIGHT:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case FOLLOWSYSTEM:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
            }
            return index == selected.ordinal();
        }

        return false;
    }

    private void closeFragment(View view) {
        getParentFragmentManager().popBackStack();
        PreferencesManager.vibrate(view);
    }

    private void addOptions(Class<?> type) {
        if (LaunchWindow.class.equals(type)) {
            String[] windowNames = context.getResources().getStringArray(R.array.launch_windows);
            for (int i = 0; i < windowNames.length; i++) {
                boolean isLast = i == windowNames.length - 1;
                RelativeLayout item = addViews(windowNames[i], verifySelected(i, LaunchWindow.class), isLast, null);
                final int index = i;

                item.setOnClickListener(view -> {
                    LaunchWindow selectedWindow = LaunchWindow.values()[index];
                    PreferencesManager.saveEnumPreference(SP_LAUNCHWINDOW, PREFS_FUNCTIONALITY, selectedWindow, context);
                    closeFragment(view);
                });
            }
        } else if (Preferences.Font.class.equals(type)) {
            String[] fontNames = context.getResources().getStringArray(R.array.fonts);
            for (int i = 0; i < fontNames.length; i++) {
                boolean isLast = i == fontNames.length - 1;

                Preferences.Font selectedFont = Preferences.Font.values()[i];
                Typeface font = ResourcesCompat.getFont(context, R.font.inter);
                if (selectedFont == Font.ROBOTO_MONO) {
                    font = ResourcesCompat.getFont(context, R.font.roboto_mono);
                } else if (selectedFont == Font.ROBOTO_SERIF) {
                    font = ResourcesCompat.getFont(context, R.font.roboto_serif);
                } else if (selectedFont == Font.POPPINS) {
                    font = ResourcesCompat.getFont(context, R.font.poppins);
                }

                RelativeLayout item = addViews(fontNames[i], verifySelected(i, Font.class), isLast, font);

                item.setOnClickListener(view -> {
                    PreferencesManager.saveEnumPreference(SP_FONT, PREFS_LANG, selectedFont, context);
                    closeFragment(view);
                });
            }
        } else if (Preferences.ThemeMode.class.equals(type)) {
            String[] themeNames = context.getResources().getStringArray(R.array.theme_modes);
            for (int i = 0; i < themeNames.length; i++) {
                boolean isLast = i == themeNames.length - 1;
                RelativeLayout item = addViews(themeNames[i], verifySelected(i, ThemeMode.class), isLast, null);
                final int index = i;

                item.setOnClickListener(view -> {
                    ThemeMode selectedTheme = ThemeMode.values()[index];
                    PreferencesManager.saveEnumPreference(SP_THEME, PREFS_UI, selectedTheme, context);
                    closeFragment(view);
                });
            }
        }
        else if (Preferences.SortingMode.class.equals(type)) {
            String[] sortingModes = context.getResources().getStringArray(R.array.sorting_modes);
            for (int i = 0; i < sortingModes.length; i++) {
                boolean isLast = i == sortingModes.length - 1;
                RelativeLayout item = addViews(sortingModes[i], verifySelected(i, Preferences.SortingMode.class), isLast, null);
                final int index = i;

                item.setOnClickListener(view -> {
                    Preferences.SortingMode selectedSorting = Preferences.SortingMode.values()[index];
                    PreferencesManager.saveEnumPreference(SP_SORTING_MODE, PREFS_FUNCTIONALITY, selectedSorting, context);
                    closeFragment(view);
                });
            }
        }else if(Preferences.HeaderType.class.equals(type)){
            String[] headerNames = context.getResources().getStringArray(R.array.header_types);
            for (int i = 0; i < headerNames.length; i++) {
                boolean isLast = i == headerNames.length - 1;
                RelativeLayout item = addViews(headerNames[i], verifySelected(i, Preferences.HeaderType.class), isLast, null);
                final int index = i;

                item.setOnClickListener(view -> {
                    Preferences.HeaderType selectedHeader = Preferences.HeaderType.values()[index];
                    PreferencesManager.saveEnumPreference(SP_HEADERTYPE, PREFS_UI, selectedHeader, context);
                    closeFragment(view);
                });
            }
        } else if(Preferences.HeaderSize.class.equals(type)){
            String[] headerNames = context.getResources().getStringArray(R.array.header_sizes);
            for (int i = 0; i < headerNames.length; i++) {
                boolean isLast = i == headerNames.length - 1;
                RelativeLayout item = addViews(headerNames[i], verifySelected(i, Preferences.HeaderSize.class), isLast, null);
                final int index = i;

                item.setOnClickListener(view -> {
                    Preferences.HeaderSize selectedHeaderSize = Preferences.HeaderSize.values()[index];
                    PreferencesManager.saveEnumPreference(SP_HEADERSIZE, PREFS_UI, selectedHeaderSize, context);
                    closeFragment(view);
                });
            }
        }
    }

    private RelativeLayout addViews(String name, boolean selected, boolean lastView, Typeface font) {
        RelativeLayout parent = new RelativeLayout(context);


        TextView textView = new TextView(context);
        textView.setText(name);
        textView.setTypeface(font);
        int px = PreferencesManager.dpToPx(10, context);
        textView.setPadding(px, px, px, px);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setTextColor(context.getColor(R.color.textPrimary));
        parent.addView(textView);

        RelativeLayout.LayoutParams textparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textparams.addRule(RelativeLayout.ALIGN_PARENT_START);
        textView.setLayoutParams(textparams);

        if (!lastView) {
            View separator = new View(context);
            separator.setBackgroundColor(context.getColor(R.color.element_border));
            RelativeLayout.LayoutParams separatorparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, PreferencesManager.dpToPx(1, context));
            separatorparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            int margin = PreferencesManager.dpToPx(10, context);
            separatorparams.setMargins(margin, 0, margin, 0);
            separator.setLayoutParams(separatorparams);
            parent.addView(separator);
        }


        if (selected) {
            View icon = new View(context);
            icon.setBackground(AppCompatResources.getDrawable(context, R.drawable.icon_checkmark));
            int iconSize = PreferencesManager.dpToPx(15, context);
            RelativeLayout.LayoutParams iconparams = new RelativeLayout.LayoutParams(iconSize, iconSize);
            iconparams.addRule(RelativeLayout.ALIGN_PARENT_END);
            iconparams.addRule(RelativeLayout.CENTER_VERTICAL);
            iconparams.setMarginEnd(iconSize);
            icon.setLayoutParams(iconparams);
            icon.setBackgroundTintList(ColorStateList.valueOf(PreferencesManager.getAccentColor(context)));
            parent.addView(icon);
        }

        optionsContainer.addView(parent);
        return parent;
    }
}