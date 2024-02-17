package com.niilopoutanen.rss_feed.sourcemanager;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.niilopoutanen.rss_feed.common.stages.StageFragment;
import com.niilopoutanen.rss_feed.parser.Parser;
import com.niilopoutanen.rss_feed.rss.Source;

import java.io.Serializable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SourceInputFragment extends StageFragment {
    private final Source input = new Source();
    private EditText url, name;
    private TextView notice;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_source_input, container, false);
        name = rootView.findViewById(R.id.sourceadd_feedName);
        url = rootView.findViewById(R.id.sourceadd_feedUrl);
        SwitchCompat visible = rootView.findViewById(R.id.switch_showInFeed);
        notice = rootView.findViewById(R.id.sourceadd_notice);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                input.title = s.toString();
            }
        });

        url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                input.url = s.toString();
            }
        });

        visible.setOnCheckedChangeListener((buttonView, isChecked) -> input.visible = isChecked);
        return rootView;
    }


    @Override
    public void canContinue(Consumer<Boolean> result) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if(url == null) {
                result.accept(false);
                showMessage(getString(com.niilopoutanen.rss_feed.common.R.string.error_empty_url));
                return;
            }
            else if(url.getText().toString().isEmpty()){
                result.accept(false);
                showMessage(getString(com.niilopoutanen.rss_feed.common.R.string.error_empty_url));
                return;
            }
            result.accept(true);
        });
    }

    private void showMessage(String msg){
        Activity activity = getActivity();
        if(activity == null) return;
        if(notice == null) return;

        activity.runOnUiThread(() -> {
            notice.setVisibility(View.VISIBLE);
            notice.setText(msg);
        });

    }

    @Override
    public Serializable getState() {
        return input;
    }

    @Override
    public boolean canReturn() {
        return true;
    }
}
