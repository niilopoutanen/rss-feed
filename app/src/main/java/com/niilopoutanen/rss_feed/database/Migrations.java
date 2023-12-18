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
import java.util.UUID;

public class Migrations {
    public static void Migrate0_1(Context context) {
        String filename = "rssfeed.content";
        List<Compatibility> oldData = new ArrayList<>();
        try {
            File file = context.getFileStreamPath(filename);
            if (file != null && file.exists()) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream ois = new ObjectInputStream(fis);
                oldData = (List<Compatibility>) ois.readObject();
                ois.close();
                fis.close();
            }
            else{
                return;
            }

        } catch (IOException | ClassNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        List<Source> sources = new ArrayList<>();
        for(Compatibility old : oldData){
            Source source = new Source();
            source.title = old.name;
            source.url = old.feedUrl;
            source.image = old.imageUrl;
            sources.add(source);
        }

        AppRepository repository = new AppRepository(context);
        for (Source source : sources) {
            repository.insert(source);
        }

    }

    private static class Compatibility{
        private static final long serialVersionUID = 1L;
        private UUID id;
        private String name;
        private String feedUrl;
        private String imageUrl;
        private Boolean showInFeed;
    }
}
