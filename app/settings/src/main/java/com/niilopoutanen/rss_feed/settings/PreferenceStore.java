package com.niilopoutanen.rss_feed.settings;

import android.app.Application;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.preference.PreferenceDataStore;

import com.niilopoutanen.rss_feed.database.AppViewModel;

public class PreferenceStore extends PreferenceDataStore {

    AppViewModel appViewModel;

    public PreferenceStore(ViewModelStoreOwner storeOwner){
        appViewModel = new ViewModelProvider(storeOwner).get(AppViewModel.class);
    }
    @Override
    public void putBoolean(String key, boolean value) {


    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return super.getBoolean(key, defValue);
    }
}
