package com.niilopoutanen.rss_feed.database;

import android.content.Context;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.niilopoutanen.rss.Source;

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

public class Migrations {
    public static void Migrate0_1(Context context) {
        String filename = "rssfeed.content";
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
                return;
            }

        } catch (IOException | ClassNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        List<com.niilopoutanen.rss.Source> sources = new ArrayList<>();
        for(Compatibility old : oldData){
            com.niilopoutanen.rss.Source source = new com.niilopoutanen.rss.Source();
            source.title = old.name;
            source.url = old.feedUrl;
            source.image = old.imageUrl;
            sources.add(source);
        }

        AppRepository repository = new AppRepository(context);
        for (com.niilopoutanen.rss.Source source : sources) {
            repository.insert(source);
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
