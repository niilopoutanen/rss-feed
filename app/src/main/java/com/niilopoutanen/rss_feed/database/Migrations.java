package com.niilopoutanen.rss_feed.database;

import android.content.Context;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.niilopoutanen.rss.Source;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class Migrations {
    public static void Migrate0_1(Context context){
        String filename = "rssfeed.content";
        List<Source> sources = new ArrayList<>();
        try {
            File file = context.getFileStreamPath(filename);
            if (file != null && file.exists()) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream ois = new ObjectInputStream(fis);
                sources = (List<Source>) ois.readObject();
                ois.close();
                fis.close();
            }

        } catch (IOException | ClassNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        AppRepository repository = new AppRepository(context);
        for(Source source : sources){
            repository.insert(source);
        }

    }
}
