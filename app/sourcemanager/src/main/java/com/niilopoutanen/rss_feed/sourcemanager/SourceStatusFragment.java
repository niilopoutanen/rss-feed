package com.niilopoutanen.rss_feed.sourcemanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.transition.MaterialFadeThrough;
import com.google.android.material.transition.MaterialSharedAxis;
import com.niilopoutanen.rss_feed.rss.Source;

public class SourceStatusFragment extends Fragment {

    private Source input;
    public static SourceStatusFragment newInstance(Source input) {
        Bundle args = new Bundle();
        args.putSerializable("input", input);
        SourceStatusFragment fragment = new SourceStatusFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            input = (Source) getArguments().getSerializable("input");
        }
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_source_status, container, false);
        TextView status = rootView.findViewById(R.id.source_status_text);

        return rootView;
    }
}
