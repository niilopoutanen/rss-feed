package com.niilopoutanen.rss_feed.models;

import java.io.Serializable;

/**
 * Data model with all user customizable preferences
 */
public class Preferences implements Serializable {
    public static final String SP_THEME = "theme";
    public static final ThemeMode SP_THEME_DEFAULT = ThemeMode.FOLLOWSYSTEM;
    public static final String SP_FONT = "font";
    public static final Font SP_FONT_DEFAULT = Font.INTER;
    public static final String SP_ARTICLESINBROWSER = "articles_openinbrowser";
    public static final boolean SP_ARTICLESINBROWSER_DEFAULT = false;
    public static final String SP_HAPTICS = "haptics";
    public static final boolean SP_HAPTICS_DEFAULT = true;
    public static final String SP_IMAGECACHE = "imagecache";
    public static final boolean SP_IMAGECACHE_DEFAULT = true;
    public static final String SP_HIDE_SOURCE_ALERT = "hide_source_alert";
    public static final boolean SP_HIDE_SOURCE_ALERT_DEFAULT = false;
    public static final String SP_ARTICLEFULLSCREEN = "article_fullscreen";
    public static final boolean SP_ARTICLEFULLSCREEN_DEFAULT = false;
    public static final String SP_ARTICLE_SHOW_CONTROLS = "article_show_controls";
    public static final boolean SP_ARTICLE_SHOW_CONTROLS_DEFAULT = true;
    public static final String SP_ARTICLE_SHOW_CATEGORIES = "article_show_categories";
    public static final boolean SP_ARTICLE_SHOW_CATEGORIES_DEFAULT = true;
    public static final String SP_LAUNCHWINDOW = "launch_window";
    public static final LaunchWindow SP_LAUNCHWINDOW_DEFAULT = LaunchWindow.FEED;
    public static final String SP_FEEDCARD_STYLE = "feedcard_style";
    public static final FeedCardStyle SP_FEEDCARD_STYLE_DEFAULT = FeedCardStyle.LARGE;
    public static final String SP_FEEDCARD_AUTHORVISIBLE = "feedcard_author_visible";
    public static final boolean SP_FEEDCARD_AUTHORVISIBLE_DEFAULT = true;
    public static final String SP_FEEDCARD_TITLEVISIBLE = "feedcard_title_visible";
    public static final boolean SP_FEEDCARD_TITLEVISIBLE_DEFAULT = true;
    public static final String SP_FEEDCARD_DESCVISIBLE = "feedcard_description_visible";
    public static final boolean SP_FEEDCARD_DESCVISIBLE_DEFAULT = true;
    public static final String SP_FEEDCARD_DATEVISIBLE = "feedcard_date_visible";
    public static final boolean SP_FEEDCARD_DATEVISIBLE_DEFAULT = true;
    public static final String SP_FEEDCARD_DATESTYLE = "feedcard_date_style";
    public static final DateStyle SP_FEEDCARD_DATESTYLE_DEFAULT = DateStyle.TIMESINCE;
    public static final String SP_ANIMATE_CLICKS = "animate_clicks";
    public static final boolean SP_ANIMATE_CLICKS_DEFAULT = true;
    public static final String SP_COLORACCENT = "coloraccent";
    public static final ColorAccent SP_COLORACCENT_DEFAULT = ColorAccent.BLUE;
    public static final String SP_HEADERTYPE = "header_type";
    public static final HeaderType SP_HEADERTYPE_DEFAULT = HeaderType.FAT;
    public static final String SP_FONTSIZE = "font_size";
    public static final int SP_FONTSIZE_DEFAULT = 15;
    public static final String SP_VERSION = "version";
    public static final String PREFS_UI = "preferences_ui";
    public static final String PREFS_LANG = "preferences_language";
    public static final String PREFS_FUNCTIONALITY = "preferences_functionality";

    //saved data
    public ThemeMode s_ThemeMode;
    public ColorAccent s_coloraccent;
    public FeedCardStyle s_feedcardstyle;
    public Font s_font;
    public LaunchWindow s_launchwindow;
    public HeaderType s_headertype;
    public boolean s_articlesinbrowser;
    public boolean s_feedcard_authorvisible;
    public boolean s_feedcard_titlevisible;
    public boolean s_feedcard_descvisible;
    public boolean s_feedcard_datevisible;
    public boolean s_articlefullscreen;
    public boolean s_article_show_controls;
    public boolean s_article_show_categories;
    public boolean s_hide_sourcealert;
    public boolean s_imagecache;
    public boolean s_haptics;
    public boolean s_animateclicks;
    public int s_fontsize;
    public DateStyle s_feedcard_datestyle;

    public Preferences() {
    }

    public enum ThemeMode {
        LIGHT, DARK, FOLLOWSYSTEM
    }

    public enum Font {
        INTER, ROBOTO_SERIF, ROBOTO_MONO, POPPINS
    }

    public enum LaunchWindow {
        SETTINGS, FEED, SOURCES, DISCOVER
    }

    public enum DateStyle {
        LONG, SHORT, TIMESINCE, TIME
    }

    public enum FeedCardStyle {
        LARGE, SMALL, NONE
    }

    public enum ColorAccent {
        BLUE, VIOLET, PINK, RED, ORANGE, YELLOW, GREEN
    }

    public enum HeaderType {
        FAT, BOLD, MEDIUM, LIGHT
    }
}
