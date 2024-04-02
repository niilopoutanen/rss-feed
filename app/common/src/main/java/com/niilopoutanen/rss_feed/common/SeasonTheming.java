package com.niilopoutanen.rss_feed.common;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeasonTheming {

    public boolean isSeason() {
        Date today = Date.from(Instant.now());

        Map<Date, Date> seasons = getSeasons(Calendar.getInstance().get(Calendar.YEAR));

        for (Map.Entry<Date, Date> entry : seasons.entrySet()) {
            Date startDate = entry.getKey();
            Date endDate = entry.getValue();
            if (today.after(startDate) && today.before(endDate)) {
                return true;
            }
        }

        return false;
    }



    public Map<Date, Date> getSeasons(int year) {
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
}
