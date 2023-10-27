package com.niilopoutanen.rss_feed.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.transition.MaterialSharedAxis;
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
    private LinearLayout viewContainer;
    private ProgressBar progressBar;
    private Context appContext;

    public AddSourceFragment(Source source, Context context) {
        this.source = source;
        this.appContext = context;
    }
    public AddSourceFragment() {}

    private void loadData(){
        if(source == null){
            return;
        }
        feedUrl.setText(source.getFeedUrl());
        feedName.setText(source.getName());
        showInFeed.setChecked(source.isVisibleInFeed());
        title.setText(appContext.getString(R.string.updatesource));
    }

    private void saveData(){
        Activity activity = (Activity) appContext;
        progressBar.setVisibility(View.VISIBLE);
        SourceValidator.validate(feedUrl.getText().toString(), feedName.getText().toString(), result -> {

            if (result != null) {
                if(this.source != null){
                    SaveSystem.saveContent(appContext, new Source(result.getName(), result.getFeedUrl(), result.getImageUrl(), showInFeed.isChecked(), source.getId()));
                }
                else{
                    SaveSystem.saveContent(appContext, new Source(result.getName(), result.getFeedUrl(), result.getImageUrl(), showInFeed.isChecked()));
                }
                activity.runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    closeFragment(null);
                });
            }
            else {
                viewContainer.addView(SourceValidator.createErrorMessage(appContext, "Error with adding source. Please try again"));
                activity.runOnUiThread(() -> progressBar.setVisibility(View.GONE));
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

        viewContainer = rootView.findViewById(R.id.sourceadd_layout);
        progressBar = rootView.findViewById(R.id.addsource_progress);

        feedUrl = rootView.findViewById(R.id.sourceadd_feedUrl);
        feedName = rootView.findViewById(R.id.sourceadd_feedName);
        showInFeed = rootView.findViewById(R.id.switch_showInFeed);

        loadData();

        rootView.findViewById(R.id.addsource_continue).setOnClickListener(view -> saveData());

        return rootView;
    }

    private void closeFragment(View view) {
        getParentFragmentManager().popBackStack();
        if(view != null){
            PreferencesManager.vibrate(view);
        }
    }


}