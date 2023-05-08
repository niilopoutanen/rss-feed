package com.niilopoutanen.rss_feed.customization;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.niilopoutanen.rss_feed.BuildConfig;
import com.niilopoutanen.rss_feed.R;

public class UpdateDialog {
    private final Context appContext;
    public BottomSheetDialog sheet;

    public UpdateDialog(Context context){
        this.appContext = context;
        initializeSheet();
    }
    private void initializeSheet(){
        sheet = new BottomSheetDialog(appContext, R.style.BottomSheetStyle);

        sheet.setContentView(R.layout.dialog_update);
        sheet.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        TextView header = sheet.findViewById(R.id.updatedialog_header);
        String headerText = appContext.getString(R.string.whatsnew) + " v" + BuildConfig.VERSION_NAME + "?";
        header.setText(headerText);

        sheet.setOnCancelListener(dialog -> PreferencesManager.setLatestVersion(appContext));
        sheet.setOnDismissListener(dialog -> PreferencesManager.setLatestVersion(appContext));

        ((RelativeLayout) sheet.findViewById(R.id.updatedialog_continue)).setOnClickListener(v -> sheet.dismiss());
    }
    public void show(){
        sheet.show();
    }
}
