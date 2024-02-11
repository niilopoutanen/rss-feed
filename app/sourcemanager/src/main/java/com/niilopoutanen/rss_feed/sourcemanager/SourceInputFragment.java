package com.niilopoutanen.rss_feed.sourcemanager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.transition.MaterialSharedAxis;
import com.niilopoutanen.rss_feed.rss.Source;

public class SourceInputFragment extends Fragment {
    private StateListener stateListener;
    private final Source input = new Source();

    public Source getInput(){
        return this.input;
    }

    public void setStateListener(StateListener stateListener){
        this.stateListener = stateListener;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_source_input, container, false);
        EditText name = rootView.findViewById(R.id.sourceadd_feedName);
        EditText url = rootView.findViewById(R.id.sourceadd_feedUrl);
        SwitchCompat visible = rootView.findViewById(R.id.switch_showInFeed);

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
}
