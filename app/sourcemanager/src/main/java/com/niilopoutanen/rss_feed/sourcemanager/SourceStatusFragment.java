package com.niilopoutanen.rss_feed.sourcemanager;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.transition.MaterialSharedAxis;
import com.niilopoutanen.rss_feed.parser.Parser;
import com.niilopoutanen.rss_feed.rss.Source;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SourceStatusFragment extends Fragment {
    private StateListener stateListener;
    private Source input;
    private TextView statusText;
    public void setStateListener(StateListener stateListener){
        this.stateListener = stateListener;
    }
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
        stateListener.setContinueAllowed(false);
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            stateListener.setContinueAllowed(true);
            stateListener.allowFinish();
        }, 3000);

    }

    public void load(Source input){
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Parser parser = new Parser();
            parser.load(input.url);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_source_status, container, false);
        statusText = rootView.findViewById(R.id.source_status_text);

        load(input);
        return rootView;
    }
}
