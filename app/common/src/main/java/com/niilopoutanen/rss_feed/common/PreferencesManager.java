package com.niilopoutanen.rss_feed.common;

import static com.niilopoutanen.rss_feed.common.models.Preferences.*;
import static com.niilopoutanen.rss_feed.common.models.Preferences.ColorAccent;
import static com.niilopoutanen.rss_feed.common.models.Preferences.ColorAccent;
import static com.niilopoutanen.rss_feed.common.models.Preferences.DateStyle;
import static com.niilopoutanen.rss_feed.common.models.Preferences.DateStyle.*;
import static com.niilopoutanen.rss_feed.common.models.Preferences.FeedCardStyle;
import static com.niilopoutanen.rss_feed.common.models.Preferences.Font;
import static com.niilopoutanen.rss_feed.common.models.Preferences.Font.*;
import static com.niilopoutanen.rss_feed.common.models.Preferences.HeaderSize.*;
import static com.niilopoutanen.rss_feed.common.models.Preferences.HeaderType.*;
import static com.niilopoutanen.rss_feed.common.models.Preferences.LaunchWindow;
import static com.niilopoutanen.rss_feed.common.models.Preferences.ThemeMode;
import static com.niilopoutanen.rss_feed.common.models.Preferences.ThemeMode.*;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;

import com.niilopoutanen.rss_feed.common.models.Preferences;
import com.niilopoutanen.rss_feed.common.models.Category;
import com.niilopoutanen.rss_feed.common.models.Preferences;

import java.text.DateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;


public class PreferencesManager {

    public static final int FEED_IMAGE_LARGE = 1;
    public static final int FEED_IMAGE_LARGE_FULLSCREEN = 2;
    public static final int FEED_IMAGE_SMALL = 3;
    public static final int ARTICLE_IMAGE = 4;

    /**
     * Loads the saved themes. Required before each activity's setContentView()
     *
     * @param activity    Required to set the theme
     * @param preferences Required to get the selected theme
     */
    public static void setSavedTheme(Activity activity, Preferences preferences) {
        if (preferences == null) {
            preferences = PreferencesManager.loadPreferences(activity.getApplicationContext());
        }
        //Accent color for devices that don't support dark mode
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            if (preferences.s_coloraccent == null) {
                activity.setTheme((R.style.AccentBlue));
                return;
            }
            switch (preferences.s_coloraccent) {
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
        } else {
            //> Android 10
            activity.setTheme(R.style.RSSFeedStyle);
        }

        //Dark-Light theme for devices that support it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            switch (preferences.s_ThemeMode) {
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

    /**
     * Returns the user's selected font
     *
     * @return Typeface object of the font
     */
    public static Typeface getSavedFont(Preferences preferences, Context context) {
        switch (preferences.s_font) {
            case ROBOTO_SERIF:
                return ResourcesCompat.getFont(context, R.font.roboto_serif);
            case POPPINS:
                return ResourcesCompat.getFont(context, R.font.poppins);
            case ROBOTO_MONO:
                return ResourcesCompat.getFont(context, R.font.roboto_mono);
            default:
                return ResourcesCompat.getFont(context, R.font.inter);

        }
    }

    public static Category.Country getUserLocale() {
        String locale = Locale.getDefault().getLanguage();
        Category.Country userLocale;
        if ("fi".equals(locale)) {
            userLocale = Category.Country.FI;
        } else {
            userLocale = Category.Country.EN;
        }
        return userLocale;
    }

    /**
     * Performs device vibration
     *
     * @param view View to target
     */
    public static void vibrate(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
    }

    /**
     * Returns the last version of the app user has accessed
     *
     * @return int code of the version
     */
    private static int getLastVersionUsed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FUNCTIONALITY, Context.MODE_PRIVATE);
        return prefs.getInt(SP_VERSION, 0);
    }

    /**
     * Sets the latest version when user accesses it.
     */
    public static void setLatestVersion(Context context) {
        int versionCode = getVersionCode(context);
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FUNCTIONALITY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(SP_VERSION, versionCode);
        editor.apply();
    }

    public static int getVersionCode(Context context){
        try{
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    public static String getVersionName(Context context){
        try{
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Checks if the user is launching the version for the first time
     *
     * @return true if is, false if not
     */
    public static boolean isFirstLaunch(Context context) {
        int currentVersion = getVersionCode(context);
        int lastVersionUsed = getLastVersionUsed(context);
        if(lastVersionUsed == 23){
            return false;
        }
        return currentVersion > lastVersionUsed;
    }

    public static void setHeader(Context context, TextView header){
        Preferences preferences = loadPreferences(context);
        switch (preferences.s_headertype){
            case FAT:
                header.setTypeface(ResourcesCompat.getFont(context, R.font.inter_black));
                break;
            case BOLD:
                header.setTypeface(ResourcesCompat.getFont(context, R.font.inter_bold));
                break;
            case MEDIUM:
                header.setTypeface(ResourcesCompat.getFont(context, R.font.inter_medium));
                break;
            case LIGHT:
                header.setTypeface(ResourcesCompat.getFont(context, R.font.inter_regular));
                break;
        }
        switch (preferences.s_headersize){
            case SMALL:
                header.setTextSize(20);
                break;

            case NORMAL:
                header.setTextSize(30);
                break;

            case LARGE:
                header.setTextSize(40);
                break;
        }
    }
    /**
     * Loads the saved preferences from disk
     *
     * @return Preferences object with parsed data
     */
    public static Preferences loadPreferences(Context context) {
        Preferences preferences = new Preferences();

        preferences.s_haptics = getBooleanPreference(SP_HAPTICS, PREFS_FUNCTIONALITY, SP_HAPTICS_DEFAULT, context);
        preferences.s_ThemeMode = getEnumPreference(SP_THEME, PREFS_UI, ThemeMode.class, SP_THEME_DEFAULT, context);
        preferences.s_coloraccent = getEnumPreference(SP_COLORACCENT, PREFS_UI, ColorAccent.class, SP_COLORACCENT_DEFAULT, context);
        preferences.s_feedcardstyle = getEnumPreference(SP_FEEDCARD_STYLE, PREFS_UI, FeedCardStyle.class, SP_FEEDCARD_STYLE_DEFAULT, context);
        preferences.s_font = getEnumPreference(SP_FONT, PREFS_LANG, Font.class, SP_FONT_DEFAULT, context);
        preferences.s_launchwindow = getEnumPreference(SP_LAUNCHWINDOW, PREFS_FUNCTIONALITY, LaunchWindow.class, SP_LAUNCHWINDOW_DEFAULT, context);
        preferences.s_articlesinbrowser = getBooleanPreference(SP_ARTICLESINBROWSER, PREFS_FUNCTIONALITY, SP_ARTICLESINBROWSER_DEFAULT, context);
        preferences.s_article_show_categories = getBooleanPreference(SP_ARTICLE_SHOW_CATEGORIES, PREFS_UI, SP_ARTICLE_SHOW_CATEGORIES_DEFAULT, context);
        preferences.s_articlefullscreen = getBooleanPreference(SP_ARTICLEFULLSCREEN, PREFS_FUNCTIONALITY, SP_ARTICLEFULLSCREEN_DEFAULT, context);
        preferences.s_article_show_controls = getBooleanPreference(SP_ARTICLE_SHOW_CONTROLS, PREFS_UI, SP_ARTICLE_SHOW_CONTROLS_DEFAULT, context);
        preferences.s_imagecache = getBooleanPreference(SP_IMAGECACHE, PREFS_FUNCTIONALITY, SP_IMAGECACHE_DEFAULT, context);
        preferences.s_hide_sourcealert = getBooleanPreference(SP_HIDE_SOURCE_ALERT, PREFS_FUNCTIONALITY, SP_HIDE_SOURCE_ALERT_DEFAULT, context);
        preferences.s_fontsize = getIntPreference(SP_FONTSIZE, PREFS_FUNCTIONALITY, SP_FONTSIZE_DEFAULT, context);
        preferences.s_animateclicks = getBooleanPreference(SP_ANIMATE_CLICKS, PREFS_FUNCTIONALITY, SP_ANIMATE_CLICKS_DEFAULT, context);
        preferences.s_showchangelog = getBooleanPreference(SP_SHOW_CHANGELOG, PREFS_FUNCTIONALITY, SP_SHOW_CHANGELOG_DEFAULT, context);
        preferences.s_headertype = getEnumPreference(SP_HEADERTYPE, PREFS_UI, HeaderType.class, SP_HEADERTYPE_DEFAULT, context);
        preferences.s_headersize = getEnumPreference(SP_HEADERSIZE, PREFS_UI, HeaderSize.class, SP_HEADERSIZE_DEFAULT, context);

        preferences.s_feedcard_authorvisible = getBooleanPreference(SP_FEEDCARD_AUTHORVISIBLE, PREFS_UI, SP_FEEDCARD_AUTHORVISIBLE_DEFAULT, context);
        preferences.s_feedcard_titlevisible = getBooleanPreference(SP_FEEDCARD_TITLEVISIBLE, PREFS_UI, SP_FEEDCARD_TITLEVISIBLE_DEFAULT, context);
        preferences.s_feedcard_descvisible = getBooleanPreference(SP_FEEDCARD_DESCVISIBLE, PREFS_UI, SP_FEEDCARD_DESCVISIBLE_DEFAULT, context);
        preferences.s_feedcard_datevisible = getBooleanPreference(SP_FEEDCARD_DATEVISIBLE, PREFS_UI, SP_FEEDCARD_DATEVISIBLE_DEFAULT, context);
        preferences.s_feedcard_datestyle = getEnumPreference(SP_FEEDCARD_DATESTYLE, PREFS_LANG, DateStyle.class, SP_FEEDCARD_DATESTYLE_DEFAULT, context);

        return preferences;
    }

    /**
     * Saves a ENUM type preference to disk
     *
     * @param key      Key of the preference to be edited
     * @param category SharedPreference category of the key
     * @param value    ENUM value that gets saved
     */
    public static void saveEnumPreference(String key, String category, Enum<?> value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value.name());

        editor.apply();
    }

    /**
     * Loads a ENUM type preference from disk
     *
     * @param key       Key of the preference to be loaded
     * @param category  SharedPreference category of the key
     * @param enumClass ENUM class for parsing the loaded data
     * @param defValue  Default value if nothing is saved yet
     * @return ENUM object with loaded data
     */
    public static <T extends Enum<T>> T getEnumPreference(String key, String category, Class<T> enumClass, T defValue, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        String valueName = prefs.getString(key, defValue.name());

        return Enum.valueOf(enumClass, valueName);
    }

    /**
     * Loads a int type preference from disk
     *
     * @param key      Key of the preference to be loaded
     * @param category SharedPreference category of the key
     * @param defValue Default value if nothing is saved yet
     * @return int value with loaded data
     */
    public static int getIntPreference(String key, String category, int defValue, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        return prefs.getInt(key, defValue);
    }

    /**
     * Saves a int number preference to disk
     *
     * @param key      Key of the preference to be edited
     * @param category SharedPreference category of the key
     * @param value    int value that gets saved
     */
    public static void saveIntPreference(String key, String category, int value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Saves a boolean type preference to disk
     *
     * @param key      Key of the preference to be edited
     * @param category SharedPreference category of the key
     * @param value    boolean value that gets saved
     */
    public static void saveBooleanPreference(String key, String category, boolean value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(key, value);

        editor.apply();
    }

    /**
     * Loads a boolean type preference from disk
     *
     * @param key      Key of the preference to be loaded
     * @param category SharedPreference category of the key
     * @param defValue Default value if nothing is saved yet
     * @return boolean object with loaded data
     */
    public static boolean getBooleanPreference(String key, String category, boolean defValue, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, defValue);
    }

    /**
     * Calculates image width for feed & article elements
     *
     * @return pixel value that can be used in code
     */
    public static int getImageWidth(int imageType, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        final int columnCount = context.getResources().getInteger(R.integer.feed_columns);


        int totalWidth = displayMetrics.widthPixels / columnCount;
        if (imageType == FEED_IMAGE_LARGE) {
            return totalWidth - dpToPx(40 * columnCount, context);
        }
        if (imageType == FEED_IMAGE_LARGE_FULLSCREEN) {
            if (columnCount > 1) {
                return totalWidth - dpToPx(20 * columnCount, context);
            } else {
                // Keep the same values if vertical
                return totalWidth - dpToPx(40 * columnCount, context);
            }
        } else if (imageType == FEED_IMAGE_SMALL) {
            return dpToPx(100, context);
        } else if (imageType == ARTICLE_IMAGE) {
            int excessValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, displayMetrics);
            return totalWidth - (excessValue * columnCount);
        } else {
            //fallback value
            return 1000;
        }
    }

    /**
     * Loads the accent color user has selected
     *
     * @return TypedValue object that can be used in code
     */
    public static int getAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }

    /**
     * Formats Date object to user's preferred format
     *
     * @return String representation of the date
     */
    public static String formatDate(Date date, DateStyle dateStyle, Context context) {
        if (date == null || dateStyle == null || context == null) {
            return "";
        }
        Instant now = Instant.now();
        Instant postTime = date.toInstant();

        String formattedTime = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(date);
        String formattedDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(date);

        switch (dateStyle) {
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
                    return context.getResources().getQuantityString(R.plurals.minutes_ago, (int) diffInMinutes, diffInMinutes);
                } else if (duration.toHours() < 24) {
                    long diffInHours = duration.toHours();
                    return context.getResources().getQuantityString(R.plurals.hours_ago, (int) diffInHours, diffInHours);
                } else {
                    return formattedDate + " " + formattedTime;
                }
            case TIME:
                return formattedTime;
        }
        return "";
    }

    /**
     * Calculates DP value from PX(pixel)
     *
     * @return Pixel representation of the DP value
     */
    public static int dpToPx(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float scaleFactor = metrics.density;
        return (int) (dp * scaleFactor + 0.5f);
    }
}
