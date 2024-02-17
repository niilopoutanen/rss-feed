package com.niilopoutanen.rss_feed.sourcemanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.niilopoutanen.rss_feed.common.stages.StageFragment;
import com.niilopoutanen.rss_feed.parser.Parser;
import com.niilopoutanen.rss_feed.rss.Source;

import java.io.Serializable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SourceStatusFragment extends StageFragment {
    private TextView statusText;
    private ProgressBar progressBar;

    public void load(){
        if(data != null && data instanceof Source){
            Source input = (Source) data;
            stageBridge.onProgressLocked(false);
            setLoadingState(true);
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                Parser parser = new Parser();
                parser.load(input.url);
                if(isAdded() && getActivity() != null){
                    getActivity().runOnUiThread(() -> {
                        if(parser.source == null){
                            showMessage(getActivity().getString(com.niilopoutanen.rss_feed.common.R.string.error_invalid_url), true);
                        }
                        else{
                            showMessage("Found " + parser.posts.size() + " posts", true);
                        }
                    });
                }

            });
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_source_status, container, false);
        statusText = rootView.findViewById(R.id.source_status_text);
        progressBar = rootView.findViewById(R.id.source_status_progress);

        load();
        return rootView;
    }

    private void setLoadingState(boolean isLoading){
        if(progressBar != null){
            if(isLoading){
                progressBar.setVisibility(View.VISIBLE);
            }
            else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void showMessage(String msg, boolean didSucceed){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        stageBridge.onProgressLocked(true);
        setLoadingState(false);
    }
    @Override
    public void canContinue(Consumer<Boolean> result) {
        result.accept(false);
    }

    @Override
    public Serializable getState() {
        return null;
    }

    @Override
    public boolean canReturn() {
        return true;
    }
}
