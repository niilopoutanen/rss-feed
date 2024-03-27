package com.niilopoutanen.rss_feed.settings;

import android.app.Application;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.preference.PreferenceDataStore;

import com.niilopoutanen.rss_feed.database.AppViewModel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PreferenceStore extends PreferenceDataStore {

    AppViewModel appViewModel;

    public PreferenceStore(ViewModelStoreOwner storeOwner){
        appViewModel = new ViewModelProvider(storeOwner).get(AppViewModel.class);
    }
    @Override
    public void putBoolean(String key, boolean value) {
        try {
            Field field = appViewModel.getPreferences().getClass().getField(key);
            field.setBoolean(appViewModel.getPreferences(), value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        try {
            Field field = appViewModel.getPreferences().getClass().getField(key);
            return field.getBoolean(appViewModel.getPreferences());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return defValue;
        }
    }

}
