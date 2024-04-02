package com.niilopoutanen.rss_feed.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SeasonTheming {

    private final static String baseUrl = "https://raw.githubusercontent.com/niilopoutanen/rss-feed/app-resources/seasons/";


    public static boolean isSeason() {
        Date today = Date.from(Instant.now());

        Map<Date, Date> seasons = getSeasons(Calendar.getInstance().get(Calendar.YEAR));

        for (Map.Entry<Date, Date> entry : seasons.entrySet()) {
            Date startDate = entry.getKey();
            Date endDate = entry.getValue();
            if (today.after(startDate) && today.before(endDate)) {
                return true;
            }
        }

        return true;
    }

    private static String getActiveSeasonName(){
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        switch (currentMonth) {
            case Calendar.DECEMBER:
                return "christmas.png";
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
                return "halloween.png";
            case Calendar.MARCH:
                return "easter.png";
            default:
                return "easter.png";
        }
    }

    public static Map<Date, Date> getSeasons(int year) {
        Map<Date, Date> seasons = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);

        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        Date easterStart = calendar.getTime();

        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        Date easterEnd = calendar.getTime();
        seasons.put(easterStart, easterEnd);


        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 23);
        Date christmasStart = calendar.getTime();

        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 25);
        Date christmasEnd = calendar.getTime();
        seasons.put(christmasStart, christmasEnd);


        calendar.set(Calendar.MONTH, Calendar.OCTOBER);
        calendar.set(Calendar.DAY_OF_MONTH, 30);
        Date halloweenStart = calendar.getTime();

        calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date halloweenEnd = calendar.getTime();
        seasons.put(halloweenStart, halloweenEnd);

        return seasons;
    }

    public static ImageView inflate(Context context){
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ImageView resource = new ImageView(context);
        resource.setAdjustViewBounds(true);
        resource.setScaleType(ImageView.ScaleType.CENTER_CROP);
        resource.setLayoutParams(imageParams);
        Picasso.get().load(baseUrl + getActiveSeasonName()).into(resource);
        return resource;
    }
}
