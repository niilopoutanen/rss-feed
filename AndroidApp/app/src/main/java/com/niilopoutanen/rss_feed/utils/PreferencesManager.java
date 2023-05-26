package com.niilopoutanen.rss_feed.utils;

import static com.niilopoutanen.rss_feed.models.Preferences.ArticleColor;
import static com.niilopoutanen.rss_feed.models.Preferences.ColorAccent;
import static com.niilopoutanen.rss_feed.models.Preferences.DateStyle;
import static com.niilopoutanen.rss_feed.models.Preferences.FeedCardStyle;
import static com.niilopoutanen.rss_feed.models.Preferences.Font;
import static com.niilopoutanen.rss_feed.models.Preferences.HapticTypes;
import static com.niilopoutanen.rss_feed.models.Preferences.LaunchWindow;
import static com.niilopoutanen.rss_feed.models.Preferences.PREFS_FUNCTIONALITY;
import static com.niilopoutanen.rss_feed.models.Preferences.PREFS_LANG;
import static com.niilopoutanen.rss_feed.models.Preferences.PREFS_UI;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_ARTICLECOLOR;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_ARTICLECOLOR_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_ARTICLEFULLSCREEN;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_ARTICLEFULLSCREEN_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_ARTICLESINBROWSER;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_ARTICLESINBROWSER_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_COLORACCENT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_COLORACCENT_DEFAULT;
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
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FONT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_FONT_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_HAPTICS;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_HAPTICS_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_HAPTICS_TYPE;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_HAPTICS_TYPE_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_IMAGECACHE;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_IMAGECACHE_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_LAUNCHWINDOW;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_LAUNCHWINDOW_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_THEME;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_THEME_DEFAULT;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_VERSION;
import static com.niilopoutanen.rss_feed.models.Preferences.ThemeMode;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;

import com.niilopoutanen.rss_feed.BuildConfig;
import com.niilopoutanen.rss_feed.fragments.FeedFragment;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Preferences;

import java.text.DateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;


public class PreferencesManager {

    public static final int FEED_IMAGE_LARGE = 1;
    public static final int FEED_IMAGE_SMALL = 3;
    public static final int ARTICLE_IMAGE = 2;

    /**
     * Loads the saved themes. Required before each activity's setContentView()
     * @param activity Required to set the theme
     * @param preferences Required to get the selected theme
     */
    public static void setSavedTheme(Activity activity, Preferences preferences) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
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
            activity.setTheme(R.style.RSSFeedStyle);
        }

        //dark/light
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
     * @return Typeface object of the font
     */
    public static Typeface getSavedFont(Preferences preferences, Context context) {
        switch (preferences.s_font) {
            case ROBOTO_SANS:
                return ResourcesCompat.getFont(context, R.font.roboto_serif);

            case ROBOTO_MONO:
                return ResourcesCompat.getFont(context, R.font.roboto_mono);
            default:
                return ResourcesCompat.getFont(context, R.font.inter);

        }
    }

    /**
     * Call performVibrate() with parameters
     * @param view View to vibrate
     * @param preferences required for loading the preferred haptic type
     */
    public static void vibrate(View view, Preferences preferences, Context context) {
        performVibrate(view, preferences, HapticFeedbackConstants.KEYBOARD_TAP, context);
    }

    /**
     * Call performVibrate() with parameters & stage parameter
     * @param view View to vibrate
     * @param preferences required for loading the preferred haptic type
     * @param stage Stage of the vibration
     */
    public static void vibrate(View view, Preferences preferences, int stage, Context context) {
        performVibrate(view, preferences, stage, context);
    }

    /**
     * Performs the vibration. Can be called through vibrate() methods
     * @param view View to vibrate
     * @param preferences required for loading the preferred haptic type
     * @param stage Stage of the vibration
     */
    private static void performVibrate(View view, Preferences preferences, int stage, Context context) {
        if (!preferences.s_haptics || view == null) {
            return;
        }
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        switch (preferences.s_hapticstype) {
            case VIEW:
                view.performHapticFeedback(stage);
                break;
            case VIBRATE:
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                break;
            case FALLBACK:
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.EFFECT_CLICK));
                    }
                } catch (Exception ignored) {
                }
                break;
        }

    }

    /**
     * Returns the last version of the app user has accessed
     * @return int code of the version
     */
    public static int getLastVersionUsed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FUNCTIONALITY, Context.MODE_PRIVATE);
        return prefs.getInt(SP_VERSION, 0);
    }

    /**
     * Sets the latest version when user accesses it.
     */
    public static void setLatestVersion(Context context) {
        int versionCode = BuildConfig.VERSION_CODE;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FUNCTIONALITY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(SP_VERSION, versionCode);
        editor.apply();
    }

    /**
     * Checks if the user is launching the version for the first time
     * @return true if is, false if not
     */
    public static boolean isFirstLaunch(Context context) {
        int currentVersion = BuildConfig.VERSION_CODE;
        int lastVersionUsed = getLastVersionUsed(context);

        return currentVersion > lastVersionUsed;
    }

    /**
     * Loads the saved preferences from disk
     * @return Preferences object with parsed data
     */
    public static Preferences loadPreferences(Context context) {
        Preferences preferences = new Preferences();

        preferences.s_haptics = getBooleanPreference(SP_HAPTICS, PREFS_FUNCTIONALITY, SP_HAPTICS_DEFAULT, context);
        preferences.s_hapticstype = getEnumPreference(SP_HAPTICS_TYPE, PREFS_FUNCTIONALITY, HapticTypes.class, SP_HAPTICS_TYPE_DEFAULT, context);
        preferences.s_ThemeMode = getEnumPreference(SP_THEME, PREFS_UI, ThemeMode.class, SP_THEME_DEFAULT, context);
        preferences.s_coloraccent = getEnumPreference(SP_COLORACCENT, PREFS_UI, ColorAccent.class, SP_COLORACCENT_DEFAULT, context);
        preferences.s_feedcardstyle = getEnumPreference(SP_FEEDCARD_STYLE, PREFS_UI, FeedCardStyle.class, SP_FEEDCARD_STYLE_DEFAULT, context);
        preferences.s_font = getEnumPreference(SP_FONT, PREFS_LANG, Font.class, SP_FONT_DEFAULT, context);
        preferences.s_launchwindow = getEnumPreference(SP_LAUNCHWINDOW, PREFS_FUNCTIONALITY, LaunchWindow.class, SP_LAUNCHWINDOW_DEFAULT, context);
        preferences.s_articlesinbrowser = getBooleanPreference(SP_ARTICLESINBROWSER, PREFS_FUNCTIONALITY, SP_ARTICLESINBROWSER_DEFAULT, context);
        preferences.s_articlecolor = getEnumPreference(SP_ARTICLECOLOR, PREFS_UI, ArticleColor.class, SP_ARTICLECOLOR_DEFAULT, context);
        preferences.s_articlefullscreen = getBooleanPreference(SP_ARTICLEFULLSCREEN, PREFS_FUNCTIONALITY, SP_ARTICLEFULLSCREEN_DEFAULT, context);
        preferences.s_imagecache = getBooleanPreference(SP_IMAGECACHE, PREFS_FUNCTIONALITY, SP_IMAGECACHE_DEFAULT, context);

        preferences.s_feedcard_authorvisible = getBooleanPreference(SP_FEEDCARD_AUTHORVISIBLE, PREFS_UI, SP_FEEDCARD_AUTHORVISIBLE_DEFAULT, context);
        preferences.s_feedcard_authorname = getBooleanPreference(SP_FEEDCARD_AUTHORNAME, PREFS_FUNCTIONALITY, SP_FEEDCARD_AUTHORNAME_DEFAULT, context);
        preferences.s_feedcard_titlevisible = getBooleanPreference(SP_FEEDCARD_TITLEVISIBLE, PREFS_UI, SP_FEEDCARD_TITLEVISIBLE_DEFAULT, context);
        preferences.s_feedcard_descvisible = getBooleanPreference(SP_FEEDCARD_DESCVISIBLE, PREFS_UI, SP_FEEDCARD_DESCVISIBLE_DEFAULT, context);
        preferences.s_feedcard_datevisible = getBooleanPreference(SP_FEEDCARD_DATEVISIBLE, PREFS_UI, SP_FEEDCARD_DATEVISIBLE_DEFAULT, context);
        preferences.s_feedcard_datestyle = getEnumPreference(SP_FEEDCARD_DATESTYLE, PREFS_LANG, DateStyle.class, SP_FEEDCARD_DATESTYLE_DEFAULT, context);

        return preferences;
    }

    /**
     * Saves a ENUM type preference to disk
     * @param key Key of the preference to be edited
     * @param category SharedPreference category of the key
     * @param value ENUM value that gets saved
     */
    public static void saveEnumPreference(String key, String category, Enum<?> value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value.name());

        editor.apply();
    }

    /**
     * Loads a ENUM type preference from disk
     * @param key Key of the preference to be loaded
     * @param category SharedPreference category of the key
     * @param enumClass ENUM class for parsing the loaded data
     * @param defValue Default value if nothing is saved yet
     * @return ENUM object with loaded data
     */
    public static <T extends Enum<T>> T getEnumPreference(String key, String category, Class<T> enumClass, T defValue, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        String valueName = prefs.getString(key, defValue.name());

        return Enum.valueOf(enumClass, valueName);
    }

    /**
     * Saves a boolean type preference to disk
     * @param key Key of the preference to be edited
     * @param category SharedPreference category of the key
     * @param value boolean value that gets saved
     */
    public static void saveBooleanPreference(String key, String category, boolean value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(key, value);

        editor.apply();
    }

    /**
     * Loads a boolean type preference from disk
     * @param key Key of the preference to be loaded
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
     * @return pixel value that can be used in code
     */
    public static int getImageWidth(int imageType, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        if (imageType == FEED_IMAGE_LARGE) {
            int excessValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, displayMetrics);
            return displayMetrics.widthPixels - excessValue - dpToPx(FeedFragment.CARDMARGIN_DP, context);
        } else if (imageType == FEED_IMAGE_SMALL) {
            return dpToPx(100, context);
        } else if (imageType == ARTICLE_IMAGE) {
            int excessValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, displayMetrics);
            return displayMetrics.widthPixels - excessValue;
        } else {
            //fallback value
            return 1000;
        }
    }

    /**
     * Loads the accent color user has selected
     * @return TypedValue object that can be used in code
     */
    public static int getAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }

    /**
     * Formats Date object to user's preferred format
     * @return String representation of the date
     */
    public static String formatDate(Date date, DateStyle dateStyle, Context context) {
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
     * @return Pixel representation of the DP value
     */
    public static int dpToPx(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float scaleFactor = metrics.density;
        return (int) (dp * scaleFactor + 0.5f);
    }
}
