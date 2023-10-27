package com.niilopoutanen.rss_feed.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.transition.MaterialSharedAxis;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

public class AddSourceFragment extends Fragment {

    public AddSourceFragment() { }

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
        return rootView;
    }

    private void closeFragment(View view) {
        getParentFragmentManager().popBackStack();
        PreferencesManager.vibrate(view);
    }
}