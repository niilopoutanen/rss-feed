package com.niilopoutanen.rss_feed.manager;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.niilopoutanen.rss_feed.database.AppRepository;
import com.niilopoutanen.rss_feed.manager.databinding.FragmentSourceInputBinding;
import com.niilopoutanen.rss_feed.rss.Source;

public class SourceInputFragment extends Fragment {

    private FragmentSourceInputBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSourceInputBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.sourceManagerNext.setOnClickListener(v -> {
            if(isValid()){
                NavHostFragment.findNavController(SourceInputFragment.this)
                          .navigate(R.id.action_SourceInputFragment_to_SourceStatusFragment, bundleInputs());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private boolean isValid(){
        Editable name = binding.sourceaddFeedName.getText();
        Editable url = binding.sourceaddFeedUrl.getText();
        if(name == null){
            return false;
        }
        else if(url == null || url.toString().isEmpty()){
            return false;
        }
        return true;
    }

    private Bundle bundleInputs(){
        Bundle bundle = new Bundle();
        bundle.putString("name", binding.sourceaddFeedName.getText().toString());
        bundle.putString("url", binding.sourceaddFeedUrl.getText().toString());
        bundle.putBoolean("isVisible", binding.switchShowInFeed.isChecked());
        return bundle;
    }



}