package com.niilopoutanen.rss_feed.fragments;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.niilopoutanen.rss_feed.BuildConfig;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

/**
 * Dialog that is shown on launch if user launches the version for first time
 */
public class UpdateDialog {
    private final Context context;
    public BottomSheetDialog sheet;

    public UpdateDialog(Context context) {
        this.context = context;
        initializeSheet();
    }


    /**
     * Initializes the sheet and it's elements. Automatically called in constructor
     */
    private void initializeSheet() {
        PreferencesManager.setSavedTheme((Activity) context, PreferencesManager.loadPreferences(context));
        sheet = new BottomSheetDialog(context);

        sheet.setContentView(R.layout.dialog_update);
        sheet.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        TextView header = sheet.findViewById(R.id.updatedialog_header);
        String headerText = context.getString(R.string.whatsnew) + " v" + BuildConfig.VERSION_NAME + "?";
        header.setText(headerText);

        sheet.setOnCancelListener(dialog -> PreferencesManager.setLatestVersion(context));
        sheet.setOnDismissListener(dialog -> PreferencesManager.setLatestVersion(context));

        View continueButton = sheet.findViewById(R.id.updatedialog_continue);
        continueButton.setOnClickListener(v -> sheet.dismiss());
        continueButton.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                continueButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_down));
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                continueButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                continueButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up));
                view.performClick();
            }
            return true;
        });
    }


    public void show() {
        sheet.show();
    }
    public void dismiss(){
        sheet.dismiss();
    }
    public boolean isShowing(){
        return sheet.isShowing();
    }
}
