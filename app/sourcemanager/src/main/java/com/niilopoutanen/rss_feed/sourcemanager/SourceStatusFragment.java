package com.niilopoutanen.rss_feed.sourcemanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.niilopoutanen.rss_feed.common.StageFragment;
import com.niilopoutanen.rss_feed.parser.Parser;
import com.niilopoutanen.rss_feed.rss.Source;

import java.io.Serializable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SourceStatusFragment extends StageFragment {
    private TextView statusText;


    public void load(){
        if(data != null && data instanceof Source){
            Source input = (Source) data;
            setProgressAllowed(false);
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                Parser parser = new Parser();
                parser.load(input.url);
                if(isAdded() && getActivity() != null){
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Found " + parser.posts.size() + " posts", Toast.LENGTH_SHORT).show();
                        setProgressAllowed(true);
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

        load();
        return rootView;
    }


    @Override
    public void canContinue(Consumer<Boolean> result) {
        result.accept(false);
    }

    @Override
    public Serializable getState() {
        return null;
    }
}
