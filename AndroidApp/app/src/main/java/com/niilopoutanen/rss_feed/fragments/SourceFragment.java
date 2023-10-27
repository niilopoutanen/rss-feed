package com.niilopoutanen.rss_feed.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.transition.MaterialFadeThrough;
import com.google.android.material.transition.MaterialSharedAxis;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.adapters.SourceAdapter;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.Source;
import com.niilopoutanen.rss_feed.models.WebCallBack;
import com.niilopoutanen.rss_feed.utils.SaveSystem;
import com.niilopoutanen.rss_feed.utils.SourceValidator;

import java.util.List;
import java.util.Objects;

public class SourceFragment extends Fragment implements View.OnLongClickListener {

    private List<Source> sources;
    private SourceAdapter adapter;
    private Context appContext;
    private Preferences preferences;
    private RecyclerView sourcesRecyclerView;

    public SourceFragment(Context context, Preferences preferences) {
        this.appContext = context;
        this.preferences = preferences;
    }

    public SourceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (appContext == null) {
            appContext = getContext();
        }

        setEnterTransition(new MaterialFadeThrough());
        setReenterTransition(new MaterialFadeThrough());
        postponeEnterTransition();

        sources = SaveSystem.loadContent(appContext);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (savedInstanceState != null) {
            preferences = (Preferences) savedInstanceState.getSerializable("preferences");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sources, container, false);
        sourcesRecyclerView = rootView.findViewById(R.id.sources_recyclerview);

        adapter = new SourceAdapter(sources, preferences, sourcesRecyclerView, this);
        sourcesRecyclerView.setAdapter(adapter);
        sourcesRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        startPostponedEnterTransition();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adapter.new SwipeToDeleteCallback(getContext()));
        itemTouchHelper.attachToRecyclerView(sourcesRecyclerView);


        RelativeLayout addBtn = rootView.findViewById(R.id.addNewButton);
        addBtn.setOnClickListener(v -> openSourceDialog(null));
        return rootView;
    }

    public void openSourceDialog(Source source){
        AddSourceFragment addSourceFragment = new AddSourceFragment(source, appContext);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, addSourceFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    /**
     * Dialog for asking user's input when adding a source
     *
     * @param source Leave null if adding a new source. Add a source if updating a existing one.
     */
    public void askForSourceInput(Source source) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(appContext, R.style.BottomSheetStyle);
        bottomSheetDialog.setContentView(R.layout.dialog_addsource);

        TextView sourceAdd = bottomSheetDialog.findViewById(R.id.sourcedialog_add);
        TextView sourceCancel = bottomSheetDialog.findViewById(R.id.sourcedialog_cancel);

        EditText urlInput = bottomSheetDialog.findViewById(R.id.sourcedialog_feedUrl);
        EditText nameInput = bottomSheetDialog.findViewById(R.id.sourcedialog_feedName);

        LinearLayout sheetLayout = bottomSheetDialog.findViewById(R.id.addsource_layout);
        if (source != null) {
            urlInput.setText(source.getFeedUrl());
            nameInput.setText(source.getName());
            sourceAdd.setText(appContext.getString(R.string.update));
            TextView title = bottomSheetDialog.findViewById(R.id.sourcedialog_title);
            title.setText(appContext.getString(R.string.updatesource));
        }
        sourceAdd.setOnClickListener(view -> {

            InputMethodManager imm = (InputMethodManager) appContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(urlInput.getWindowToken(), 0);

            for (int i = 0; i < sheetLayout.getChildCount(); i++) {
                View childView = sheetLayout.getChildAt(i);
                Object tag = childView.getTag();
                if (tag != null && tag.equals("error-message")) {
                    sheetLayout.removeView(childView);
                }
            }
            ProgressBar progress = bottomSheetDialog.findViewById(R.id.sourcedialog_progress);


            String inputUrl = urlInput.getText().toString();
            String inputName = nameInput.getText().toString();
            if (inputUrl.isEmpty()) {
                sheetLayout.addView(SourceValidator.createErrorMessage(appContext, "URL can't be empty"));
                return;
            }
            sourceCancel.setOnClickListener(null);

            sourceAdd.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            bottomSheetDialog.setCancelable(false);
            SourceValidator.validate(inputUrl, inputName, new WebCallBack<Source>() {
                @Override
                public void onResult(Source result) {
                    Activity activity = (Activity) appContext;
                    if (result != null) {
                        if (source == null) {
                            SaveSystem.saveContent(appContext, new Source(result.getName(), result.getFeedUrl(), result.getImageUrl()));
                        } else {
                            sources = SaveSystem.loadContent(appContext);
                            sources.removeIf(oldSource -> Objects.equals(oldSource.getName(), source.getName()));
                            sources.add(new Source(result.getName(), result.getFeedUrl(), result.getImageUrl()));
                            SaveSystem.saveContent(appContext, sources);
                        }
                        sources = SaveSystem.loadContent(appContext);
                        bottomSheetDialog.dismiss();

                        activity.runOnUiThread(() -> adapter.updateSources(sources));
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setVisibility(View.GONE);
                                sourceAdd.setVisibility(View.VISIBLE);
                                bottomSheetDialog.setCancelable(true);
                                sheetLayout.addView(SourceValidator.createErrorMessage(appContext, "Error with adding source. Please try again"));
                            }
                        });

                    }

                }
            }, appContext);
        });

        sourceCancel.setOnClickListener(view -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("preferences", preferences);
    }


    @Override
    public boolean onLongClick(View v) {
        int position = sourcesRecyclerView.getChildAdapterPosition(v);
        Source clickedSource = sources.get(position);
        openSourceDialog(clickedSource);
        return false;
    }
}