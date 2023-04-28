package com.niilopoutanen.rss_feed.customization;

import java.io.Serializable;

public class Preferences implements Serializable {
    //saved data
    public ThemeMode s_ThemeMode;
    public ColorAccent s_coloraccent;
    public FeedCardStyle s_feedcardstyle;
    public Font s_font;
    public LaunchWindow s_launchwindow;
    public boolean s_articlesinbrowser;
    public boolean s_feedcard_authorname;
    public boolean s_feedcard_authorvisible;
    public boolean s_feedcard_titlevisible;
    public boolean s_feedcard_descvisible;
    public boolean s_feedcard_datevisible;
    public boolean s_reducedglare;

    public DateStyle s_feedcard_datestyle;
    public static final String SP_SETTINGS = "Settings";
    public static final String SP_THEME = "theme";
    public static final ThemeMode SP_THEME_DEFAULT = ThemeMode.DARK;
    public static final String SP_FONT = "font";
    public static final Font SP_FONT_DEFAULT = Font.Inter;
    public static final String SP_ARTICLESINBROWSER = "articles_openinbrowser";
    public static final boolean SP_ARTICLESINBROWSER_DEFAULT = false;
    public static final String SP_REDUCEDGLARE = "ui_reducedglare";
    public static final boolean SP_REDUCEDGLARE_DEFAULT = false;
    public static final String SP_LAUNCHWINDOW= "launch_window";
    public static final LaunchWindow SP_LAUNCHWINDOW_DEFAULT = LaunchWindow.FEED;
    public static final String SP_FEEDCARD_STYLE = "feedcard_style";
    public static final FeedCardStyle SP_FEEDCARD_STYLE_DEFAULT = FeedCardStyle.LARGE;

    public static final String SP_FEEDCARD_AUTHORVISIBLE = "feedcard_author_visible";
    public static final boolean SP_FEEDCARD_AUTHORVISIBLE_DEFAULT = true;

    public static final String SP_FEEDCARD_AUTHORNAME = "feedcard_author_name";
    public static final boolean SP_FEEDCARD_AUTHORNAME_DEFAULT = false;

    public static final String SP_FEEDCARD_TITLEVISIBLE = "feedcard_title_visible";
    public static final boolean SP_FEEDCARD_TITLEVISIBLE_DEFAULT = true;

    public static final String SP_FEEDCARD_DESCVISIBLE = "feedcard_description_visible";
    public static final boolean SP_FEEDCARD_DESCVISIBLE_DEFAULT = true;

    public static final String SP_FEEDCARD_DATEVISIBLE = "feedcard_date_visible";
    public static final boolean SP_FEEDCARD_DATEVISIBLE_DEFAULT = true;

    public static final String SP_FEEDCARD_DATESTYLE = "feedcard_date_style";
    public static final DateStyle SP_FEEDCARD_DATESTYLE_DEFAULT = DateStyle.TIMESINCE;
    public static final String SP_COLORACCENT = "coloraccent";
    public static final ColorAccent SP_COLORACCENT_DEFAULT = ColorAccent.GREEN;
    public static final String PREFS_UI = "preferences_ui";
    public static final String PREFS_LANG = "preferences_language";
    public static final String PREFS_FUNCTIONALITY = "preferences_functionality";
    public Preferences(){
    }
    public enum ThemeMode {
        LIGHT, DARK, FOLLOWSYSTEM
    }
    public enum Font{
        Inter, RobotoSans
    }
    public enum LaunchWindow{
        SETTINGS, FEED, SOURCES
    }
    public enum DateStyle{
        LONG, SHORT, TIMESINCE, TIME
    }
    public enum FeedCardStyle {
        LARGE, SMALL, NONE
    }
    public enum ColorAccent{
        BLUE, VIOLET, PINK, RED, ORANGE, YELLOW, GREEN
    }
}
