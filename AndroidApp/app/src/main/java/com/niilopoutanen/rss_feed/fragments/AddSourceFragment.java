package com.niilopoutanen.rss_feed.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.transition.MaterialSharedAxis;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Source;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rss_feed.utils.SaveSystem;
import com.niilopoutanen.rss_feed.utils.SourceValidator;

public class AddSourceFragment extends Fragment {

    private final Source source;

    private EditText feedUrl;
    private EditText feedName;
    private TextView title;
    private LinearLayout viewContainer;
    private final Context appContext;

    public AddSourceFragment(Source source, Context context) {
        this.source = source;
        this.appContext = context;
    }

    private void loadData(){
        if(source == null){
            return;
        }
        feedUrl.setText(source.getFeedUrl());
        feedName.setText(source.getName());
        title.setText(appContext.getString(R.string.updatesource));
    }

    private void saveData(){
        SourceValidator.validate(feedUrl.getText().toString(), feedName.getText().toString(), result -> {

            if (result != null) {
                SaveSystem.saveContent(appContext, new Source(result.getName(), result.getFeedUrl(), result.getImageUrl()));
            }
            else {
                viewContainer.addView(SourceValidator.createErrorMessage(appContext, "Error with adding source. Please try again"));
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
        feedUrl = rootView.findViewById(R.id.sourceadd_feedUrl);
        feedName = rootView.findViewById(R.id.sourceadd_feedName);
        loadData();

        rootView.findViewById(R.id.addsource_continue).setOnClickListener(view -> saveData());

        return rootView;
    }

    private void closeFragment(View view) {
        getParentFragmentManager().popBackStack();
        PreferencesManager.vibrate(view);
    }


}