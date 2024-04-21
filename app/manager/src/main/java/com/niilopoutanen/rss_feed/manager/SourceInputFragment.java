package com.niilopoutanen.rss_feed.manager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.niilopoutanen.rss_feed.manager.databinding.FragmentSourceInputBinding;

public class SourceInputFragment extends Fragment {

    private FragmentSourceInputBinding binding;

    @Override
    public View onCreateView(
              @NonNull LayoutInflater inflater, ViewGroup container,
              Bundle savedInstanceState
    ) {

        binding = FragmentSourceInputBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(v ->
                  NavHostFragment.findNavController(SourceInputFragment.this)
                            .navigate(R.id.action_SourceInputFragment_to_SourceStatusFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}