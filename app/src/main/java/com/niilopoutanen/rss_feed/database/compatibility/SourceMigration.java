package com.niilopoutanen.rss_feed.database.compatibility;

import android.content.Context;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.niilopoutanen.rss_feed.database.AppRepository;
import com.niilopoutanen.rss_feed.rss.Source;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SourceMigration {
    private final static String filename = "rssfeed.content";
    public static boolean needed(Context context){
        File file = context.getFileStreamPath(filename);
        return file != null && file.exists();
    }


    public static void migrate(Context context){
        List<Compatibility> oldData = loadOldData(context);
        if(oldData != null){
            insertToDatabase(oldData, context);
            deleteOldData(context);
        }
    }

    private static List<Compatibility> loadOldData(Context context){
        List<Compatibility> oldData = new ArrayList<>();
        try {
            File file = context.getFileStreamPath(filename);
            if (file != null && file.exists()) {
                FileInputStream fis = context.openFileInput(filename);
                HackedObjectInputStream ois = new HackedObjectInputStream(fis);
                oldData = (List<Compatibility>) ois.readObject();
                ois.close();
                fis.close();
            }
            else{
                return null;
            }

        } catch (IOException | ClassNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        return oldData;
    }
    private static void insertToDatabase(List<Compatibility> oldData, Context context){
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

    private static void deleteOldData(Context context){
        File file = context.getFileStreamPath(filename);
        if (file != null && file.exists()) {
            file.delete();
        }
    }
    private static class Compatibility implements Serializable {
        private static final long serialVersionUID = 1L;
        private UUID id;
        private String name;
        private String feedUrl;
        private String imageUrl;
        private Boolean showInFeed;
    }

    private static class HackedObjectInputStream extends ObjectInputStream {

        public HackedObjectInputStream(InputStream in) throws IOException {
            super(in);
        }

        @Override
        protected ObjectStreamClass readClassDescriptor() {
            try{
                ObjectStreamClass resultClassDescriptor = super.readClassDescriptor();

                if (resultClassDescriptor.getName().equals("com.niilopoutanen.rss_feed.models.Source")){
                    resultClassDescriptor = ObjectStreamClass.lookup(Compatibility.class);

                }

                return resultClassDescriptor;
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
    }
}
