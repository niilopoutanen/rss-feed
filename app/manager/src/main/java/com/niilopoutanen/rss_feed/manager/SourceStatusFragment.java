package com.niilopoutanen.rss_feed.manager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.niilopoutanen.rss_feed.common.PrimaryButton;
import com.niilopoutanen.rss_feed.manager.databinding.FragmentSourceStatusBinding;
import com.niilopoutanen.rss_feed.rss.Source;

public class SourceStatusFragment extends Fragment {
    private FragmentSourceStatusBinding binding;
    private final Source source = new Source();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseBundle();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSourceStatusBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.sourceManagerNext.setOnClickListener(v -> requireActivity().finish());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onResult(boolean isSuccess){
        if(!isSuccess){
            binding.sourceManagerNext.setText(com.niilopoutanen.rss_feed.common.R.string.error_adding_source);
            binding.sourceManagerNext.setOnClickListener(v ->
                      NavHostFragment.findNavController(SourceStatusFragment.this)
                        .navigate(R.id.action_SourceStatusFragment_to_SourceInputFragment));
        }
        else{
            binding.sourceManagerNext.setText(com.niilopoutanen.rss_feed.common.R.string.continua);
            binding.sourceManagerNext.setOnClickListener(v -> requireActivity().finish());
        }
    }

    private void parseBundle(){
        Bundle inputs = getArguments();
        if(inputs == null) return;

        source.title = inputs.getString("name");
        source.url = inputs.getString("url");
        source.visible = inputs.getBoolean("isVisible", true);
    }

}