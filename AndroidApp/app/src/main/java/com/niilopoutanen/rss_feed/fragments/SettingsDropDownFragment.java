package com.niilopoutanen.rss_feed.fragments;

import static com.niilopoutanen.rss_feed.models.Preferences.ArticleColor;
import static com.niilopoutanen.rss_feed.models.Preferences.Font;
import static com.niilopoutanen.rss_feed.models.Preferences.LaunchWindow;
import static com.niilopoutanen.rss_feed.models.Preferences.PREFS_FUNCTIONALITY;
import static com.niilopoutanen.rss_feed.models.Preferences.PREFS_LANG;
import static com.niilopoutanen.rss_feed.models.Preferences.PREFS_UI;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_ARTICLECOLOR;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_ARTICLECOLOR_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FONT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FONT_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_LAUNCHWINDOW;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_LAUNCHWINDOW_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_THEME;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_THEME_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.ThemeMode;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

public class SettingsDropDownFragment extends Fragment {

    LinearLayout optionsContainer;
    Context context;
    Class<?> type;
    String title;
    String additionalMessage;

    public SettingsDropDownFragment(String optionTitle, String additionalMessage, Class<?> type, Context context) {
        this.context = context;
        this.type = type;
        this.title = optionTitle;
        this.additionalMessage = additionalMessage;
    }

    public SettingsDropDownFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            title = savedInstanceState.getString("title");
            additionalMessage = savedInstanceState.getString("additionalMessage");
            type = (Class<?>) savedInstanceState.getSerializable("class");
        }
        if(context == null){
            context = getContext();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings_dropdown, container, false);
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
        if (ArticleColor.class.equals(type)) {
            ArticleColor selected = PreferencesManager.getEnumPreference(SP_ARTICLECOLOR, PREFS_UI, ArticleColor.class, SP_ARTICLECOLOR_DEFAULT, context);
            return index == selected.ordinal();
        }

        return false;
    }

    private void closeFragment(View view) {
        getParentFragmentManager().popBackStack();
        PreferencesManager.vibrate(view, PreferencesManager.loadPreferences(context), context);
    }

    private void addOptions(Class<?> type) {
        if (LaunchWindow.class.equals(type)) {
            String[] windowNames = context.getResources().getStringArray(R.array.launch_windows);
            for (int i = 0; i < windowNames.length; i++) {
                boolean isLast = i == windowNames.length - 1;
                RelativeLayout item = addViews(windowNames[i], verifySelected(i, LaunchWindow.class), isLast, null);
                final int index = i;
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LaunchWindow selectedWindow = Preferences.LaunchWindow.values()[index];
                        PreferencesManager.saveEnumPreference(SP_LAUNCHWINDOW, PREFS_FUNCTIONALITY, selectedWindow, context);
                        closeFragment(view);
                    }
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
                } else if (selectedFont == Font.ROBOTO_SANS) {
                    font = ResourcesCompat.getFont(context, R.font.roboto_serif);
                }

                RelativeLayout item = addViews(fontNames[i], verifySelected(i, Font.class), isLast, font);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PreferencesManager.saveEnumPreference(SP_FONT, PREFS_LANG, selectedFont, context);
                        closeFragment(view);
                    }
                });
            }
        } else if (Preferences.ThemeMode.class.equals(type)) {
            String[] themeNames = context.getResources().getStringArray(R.array.theme_modes);
            for (int i = 0; i < themeNames.length; i++) {
                boolean isLast = i == themeNames.length - 1;
                RelativeLayout item = addViews(themeNames[i], verifySelected(i, ThemeMode.class), isLast, null);
                final int index = i;
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Preferences.ThemeMode selectedTheme = Preferences.ThemeMode.values()[index];
                        PreferencesManager.saveEnumPreference(SP_THEME, PREFS_UI, selectedTheme, context);
                        closeFragment(view);
                    }
                });
            }
        } else if (Preferences.ArticleColor.class.equals(type)) {
            String[] articleColors = context.getResources().getStringArray(R.array.article_backgrounds);
            for (int i = 0; i < articleColors.length; i++) {
                boolean isLast = i == articleColors.length - 1;
                RelativeLayout item = addViews(articleColors[i], verifySelected(i, ArticleColor.class), isLast, null);
                final int index = i;
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Preferences.ArticleColor selectedColor = Preferences.ArticleColor.values()[index];
                        PreferencesManager.saveEnumPreference(SP_ARTICLECOLOR, PREFS_UI, selectedColor, context);
                        closeFragment(view);
                    }
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
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("additionalMessage", additionalMessage);
        outState.putString("title", title);
        outState.putSerializable("class", type);
    }
}