package com.niilopoutanen.rss_feed.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.transition.MaterialSharedAxis;
import com.niilopoutanen.RSSParser.Callback;
import com.niilopoutanen.RSSParser.RSSException;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Source;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rss_feed.utils.SaveSystem;
import com.niilopoutanen.rss_feed.utils.SourceValidator;

public class AddSourceFragment extends Fragment {

    private Source source;

    private EditText feedUrl;
    private EditText feedName;
    private MaterialSwitch showInFeed;
    private TextView title;
    private LinearLayout bottomContainer;
    private ProgressBar progressBar;
    private Context appContext;

    public AddSourceFragment(Source source, Context context) {
        this.source = source;
        this.appContext = context;
    }

    public AddSourceFragment() {
    }

    private void loadData() {
        if (source == null) {
            return;
        }
        feedUrl.setText(source.getFeedUrl());
        feedName.setText(source.getName());
        showInFeed.setChecked(source.isVisibleInFeed());
        title.setText(appContext.getString(R.string.updatesource));
    }

    private void saveData() {
        showError("");
        Activity activity = (Activity) appContext;
        progressBar.setVisibility(View.VISIBLE);
        SourceValidator.validate(feedUrl.getText().toString(), feedName.getText().toString(), new Callback<Source>() {
            @Override
            public void onResult(Source result) {
                if (result != null) {
                    if (source != null) {
                        SaveSystem.saveContent(appContext, new Source(result.getName(), result.getFeedUrl(), result.getImageUrl(), showInFeed.isChecked(), source.getId()));
                    } else {
                        SaveSystem.saveContent(appContext, new Source(result.getName(), result.getFeedUrl(), result.getImageUrl(), showInFeed.isChecked()));
                    }
                    activity.runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        closeFragment(null);
                    });
                } else {
                    activity.runOnUiThread(() -> {
                        showError("Error with adding source. Please try again");
                        activity.runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                    });

                }

            }

            @Override
            public void onError(RSSException e) {

            }
        }, appContext);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_source, container, false);

        LinearLayout returnBtn = rootView.findViewById(R.id.addsource_return);
        returnBtn.setOnClickListener(view -> closeFragment(returnBtn));
        title = rootView.findViewById(R.id.addsource_title);

        bottomContainer = rootView.findViewById(R.id.sourceadd_bottomlayout);
        progressBar = rootView.findViewById(R.id.addsource_progress);

        feedUrl = rootView.findViewById(R.id.sourceadd_feedUrl);
        feedName = rootView.findViewById(R.id.sourceadd_feedName);
        showInFeed = rootView.findViewById(R.id.switch_showInFeed);

        loadData();

        View addSourceButton = rootView.findViewById(R.id.addsource_continue);
        addSourceButton.setOnClickListener(view -> saveData());
        addSourceButton.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                addSourceButton.startAnimation(AnimationUtils.loadAnimation(appContext, R.anim.scale_down));
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                addSourceButton.startAnimation(AnimationUtils.loadAnimation(appContext, R.anim.scale_up));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                addSourceButton.startAnimation(AnimationUtils.loadAnimation(appContext, R.anim.scale_up));
                view.performClick();
            }
            return true;
        });
        return rootView;
    }

    private void showError(String errorMessage) {
        for (int i = 0; i < bottomContainer.getChildCount(); i++) {
            View childView = bottomContainer.getChildAt(i);
            if (childView != null && childView.getTag() != null && childView.getTag().equals("error-message")) {
                bottomContainer.removeViewAt(i);
            }
        }

        if (errorMessage.equals("")) {
            return;
        }

        TextView errorText = new TextView(appContext);
        errorText.setText(errorMessage);
        errorText.setTextColor(appContext.getColor(R.color.textSecondary));
        errorText.setTag("error-message");
        errorText.setGravity(Gravity.CENTER);


        bottomContainer.addView(errorText, 0);
    }

    private void closeFragment(View view) {
        getParentFragmentManager().popBackStack();
        if (view != null) {
            PreferencesManager.vibrate(view);
        }
    }


}