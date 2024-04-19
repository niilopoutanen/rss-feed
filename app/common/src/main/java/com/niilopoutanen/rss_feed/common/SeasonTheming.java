package com.niilopoutanen.rss_feed.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeasonTheming {

    private final static String baseUrl = "https://raw.githubusercontent.com/niilopoutanen/rss-feed/app-resources/seasons/";
    private static final Date christmasDate;
    private static final Date halloweenDate;
    private static final Date easterDate;
    private static final Date newYearsDate;
    private static final Date stPatricksDate;
    static {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        calendar.set(year, Calendar.DECEMBER, 24);
        christmasDate = calendar.getTime();

        calendar.set(year, Calendar.OCTOBER, 31);
        halloweenDate = calendar.getTime();

        calendar.set(year, Calendar.DECEMBER, 31);
        newYearsDate = calendar.getTime();

        calendar.set(year, Calendar.MARCH, 17);
        stPatricksDate = calendar.getTime();
        
        easterDate = calculateEaster(year);
    }

    public static boolean isSeason() {
        Date today = Date.from(Instant.now());
        return getSeasons().contains(today);
    }

    private static String getActiveSeasonName() {
        Date today = Date.from(Instant.now());
        if (today.equals(christmasDate)) {
            return "christmas.png";
        } else if (today.equals(halloweenDate)) {
            return "halloween.png";
        } else if (today.equals(easterDate)) {
            return "easter.png";
        } else if(today.equals(newYearsDate)){
            return "newyear.png";
        } else if(today.equals(stPatricksDate)){
            return "st.patricks.png";
        } else {
            return null;
        }
    }

    public static List<Date> getSeasons() {
        List<Date> seasons = new ArrayList<>();

        seasons.add(christmasDate);
        seasons.add(halloweenDate);
        seasons.add(easterDate);

        return seasons;
    }

    public static ImageView inflate(Context context){
        if(getActiveSeasonName() == null) return null;
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ImageView resource = new ImageView(context);
        resource.setAdjustViewBounds(true);
        resource.setScaleType(ImageView.ScaleType.CENTER_CROP);
        resource.setLayoutParams(imageParams);
        Picasso.get().load(baseUrl + getActiveSeasonName()).into(resource);
        return resource;
    }


    private static Date calculateEaster(int year) {
        int goldenNumber = (year % 19) + 1;
        int century = year / 100 + 1;
        int x = (3 * century / 4) - 12;
        int z = ((8 * century + 5) / 25) - 5;
        int d = (5 * year / 4) - x - 10;
        int epact = (11 * goldenNumber + 20 + z - x) % 30;
        if ((epact == 25 && goldenNumber > 11) || epact == 24) {
            epact++;
        }
        int n = 44 - epact;
        if (n < 21) {
            n += 30;
        }
        n += 7 - ((d + n) % 7);
        int month = (n > 31) ? 4 : 3;
        int day = (n > 31) ? n - 31 : n;

        Calendar easterCalendar = Calendar.getInstance();
        easterCalendar.set(year, month - 1, day);
        return easterCalendar.getTime();
    }
}
