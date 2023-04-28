package com.niilopoutanen.rss_feed.customization;

import static com.niilopoutanen.rss_feed.customization.Preferences.*;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;

import com.niilopoutanen.rss_feed.R;

import java.text.DateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;


public class PreferencesManager {

    public static final int FEED_IMAGE_LARGE = 1;
    public static final int FEED_IMAGE_SMALL = 3;
    public static final int ARTICLE_IMAGE = 2;

    public static void setSavedTheme(Activity activity, Preferences preferences){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
            switch(preferences.s_coloraccent) {
                case BLUE:
                    activity.setTheme(R.style.AccentBlue);
                    break;
                case VIOLET:
                    activity.setTheme(R.style.AccentViolet);
                    break;
                case PINK:
                    activity.setTheme(R.style.AccentPink);
                    break;
                case RED:
                    activity.setTheme(R.style.AccentRed);
                    break;
                case ORANGE:
                    activity.setTheme(R.style.AccentOrange);
                    break;
                case YELLOW:
                    activity.setTheme(R.style.AccentYellow);
                    break;
                case GREEN:
                    activity.setTheme(R.style.AccentGreen);
                    break;
            }
        }
        else{
            activity.setTheme(R.style.RSSFeedStyle);
        }

        //dark/light
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            switch (preferences.s_ThemeMode){
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
        }

    }
    public static Typeface getSavedFont(Preferences preferences, Context context){
        switch (preferences.s_font){
            case RobotoSans:
                return ResourcesCompat.getFont(context, R.font.roboto_serif);
            default:
                return ResourcesCompat.getFont(context, R.font.inter);

        }
    }
    public static Preferences loadPreferences(Context context){
        Preferences preferences = new Preferences();

        preferences.s_ThemeMode = getEnumPreference(SP_THEME, PREFS_UI, ThemeMode.class, SP_THEME_DEFAULT, context);
        preferences.s_coloraccent = getEnumPreference(SP_COLORACCENT, PREFS_UI, ColorAccent.class, SP_COLORACCENT_DEFAULT, context);
        preferences.s_feedcardstyle = getEnumPreference(SP_FEEDCARD_STYLE, PREFS_UI, FeedCardStyle.class, SP_FEEDCARD_STYLE_DEFAULT, context);
        preferences.s_font = getEnumPreference(SP_FONT, PREFS_LANG, Font.class, SP_FONT_DEFAULT, context);
        preferences.s_launchwindow = getEnumPreference(SP_LAUNCHWINDOW, PREFS_FUNCTIONALITY, LaunchWindow.class, SP_LAUNCHWINDOW_DEFAULT, context);
        preferences.s_articlesinbrowser = getBooleanPreference(SP_ARTICLESINBROWSER, PREFS_FUNCTIONALITY, SP_ARTICLESINBROWSER_DEFAULT, context);

        preferences.s_feedcard_authorvisible = getBooleanPreference(SP_FEEDCARD_AUTHORVISIBLE, PREFS_UI, SP_FEEDCARD_AUTHORVISIBLE_DEFAULT, context);
        preferences.s_feedcard_authorname = getBooleanPreference(SP_FEEDCARD_AUTHORNAME, PREFS_FUNCTIONALITY, SP_FEEDCARD_AUTHORNAME_DEFAULT, context);
        preferences.s_feedcard_titlevisible = getBooleanPreference(SP_FEEDCARD_TITLEVISIBLE, PREFS_UI, SP_FEEDCARD_TITLEVISIBLE_DEFAULT, context);
        preferences.s_feedcard_descvisible = getBooleanPreference(SP_FEEDCARD_DESCVISIBLE, PREFS_UI, SP_FEEDCARD_DESCVISIBLE_DEFAULT, context);
        preferences.s_feedcard_datevisible = getBooleanPreference(SP_FEEDCARD_DATEVISIBLE, PREFS_UI, SP_FEEDCARD_DATEVISIBLE_DEFAULT, context);
        preferences.s_feedcard_datestyle = getEnumPreference(SP_FEEDCARD_DATESTYLE, PREFS_LANG,DateStyle.class, SP_FEEDCARD_DATESTYLE_DEFAULT, context);

        return preferences;
    }
    public static void saveEnumPreference(String key, String category, Enum<?> value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value.name());

        editor.apply();
    }
    public static <T extends Enum<T>> T getEnumPreference(String key, String category, Class<T> enumClass, T defValue, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        String valueName = prefs.getString(key, defValue.name());

        return Enum.valueOf(enumClass, valueName);
    }
    public static void saveBooleanPreference(String key,String category, boolean value, Context context){
        SharedPreferences prefs = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(key, value);

        editor.apply();
    }
    public static boolean getBooleanPreference(String key, String category, boolean defValue, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, defValue);
    }

    public static int getImageWidth(int imageType, Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        if(imageType == FEED_IMAGE_LARGE){
            int excessValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, displayMetrics);
            return displayMetrics.widthPixels - excessValue- 80;
        }
        else if(imageType == FEED_IMAGE_SMALL){
            return dpToPx(100, context);
        }
        else if(imageType == ARTICLE_IMAGE){
            int excessValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, displayMetrics);
            return displayMetrics.widthPixels - excessValue;
        }
        else{
            //fallback value
            return 1000;
        }
    }
    public static int getAccentColor(Context context){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }

    public static String formatDate(Date date,DateStyle dateStyle, Context context) {
        Instant now = Instant.now();
        Instant postTime = date.toInstant();

        String formattedTime = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(date);
        String formattedDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(date);

        switch (dateStyle){
            case LONG:
                return formattedDate + " " + formattedTime;
            case SHORT:
                return formattedDate;

            case TIMESINCE:
                Duration duration = Duration.between(postTime, now);

                if (duration.getSeconds() < 60) {
                    return context.getString(R.string.justnow);
                } else if (duration.toMinutes() < 60) {
                    long diffInMinutes = duration.toMinutes();
                    return context.getResources().getQuantityString(R.plurals.minutes_ago, (int)diffInMinutes, diffInMinutes);
                } else if (duration.toHours() < 24) {
                    long diffInHours = duration.toHours();
                    return context.getResources().getQuantityString(R.plurals.hours_ago, (int)diffInHours, diffInHours);
                } else {
                    return formattedDate + " " + formattedTime;
                }
            case TIME:
                return formattedTime;
        }
        return "";
    }
    public static int dpToPx(int dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float scaleFactor = metrics.density;
        return (int) (dp * scaleFactor + 0.5f);
    }
}
