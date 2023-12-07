package com.niilopoutanen.rss_feed.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.transition.MaterialSharedAxis;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rss_feed.database.AppDatabase;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rss_feed.utils.SaveSystem;
import com.niilopoutanen.rss_feed.utils.SourceValidator;
import com.niilopoutanen.rssparser.Callback;
import com.niilopoutanen.rssparser.RSSException;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddSourceFragment extends Fragment {

    private Source source;

    private EditText feedUrl;
    private EditText feedName;
    private MaterialSwitch showInFeed;
    private TextView title;
    private LinearLayout bottomContainer;
    private ProgressBar progressBar;
    private View addSourceButton;
    private Context context;

    public AddSourceFragment(Source source, Context context) {
        this.source = source;
        this.context = context;
    }

    public AddSourceFragment() {
    }

    private void loadData() {
        if (source == null) {
            return;
        }
        feedUrl.setText(source.url);
        feedName.setText(source.title);
        showInFeed.setChecked(source.visible);
        title.setText(context.getString(R.string.updatesource));

        TextView buttonText = (TextView) ((RelativeLayout)addSourceButton).getChildAt(0);
        buttonText.setText(context.getString(R.string.update));
    }

    private void saveData() {
        showError("");
        Activity activity = (Activity) context;
        progressBar.setVisibility(View.VISIBLE);
        Source toValidate = new Source();
        toValidate.title = feedName.getText().toString();
        toValidate.url = feedUrl.getText().toString();
        SourceValidator validator = new SourceValidator(toValidate);
        validator.validate(new Callback<Source>() {
            @Override
            public void onResult(Source result) {
                if (result != null) {
                    Source sourceToSave = new Source();
                    sourceToSave.title = result.title;
                    sourceToSave.url = result.url;
                    sourceToSave.image = result.image;
                    sourceToSave.visible = showInFeed.isChecked();
                    if (source != null) {
                        sourceToSave.id = source.id;
                    }
                    save(sourceToSave);

                    activity.runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        closeFragment(null);
                    });
                } else {
                    activity.runOnUiThread(() -> {
                        showError(context.getString(R.string.error_adding_source));
                    });
                }
            }

            @Override
            public void onError(RSSException exception) {
                activity.runOnUiThread(() -> {
                    String msg = context.getString(exception.getErrorType());
                    if(!msg.isEmpty()){
                        showError(msg);
                    }
                    else{
                        showError(context.getString(R.string.error_adding_source));
                    }
                    exception.printStackTrace();
                });

            }
        });
    }

    private void save(Source source){
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase database = AppDatabase.getInstance(context);
            database.sourceDao().insert(source);
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_source, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.addsource_header), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        LinearLayout returnBtn = rootView.findViewById(R.id.addsource_return);
        returnBtn.setOnClickListener(view -> closeFragment(returnBtn));
        title = rootView.findViewById(R.id.addsource_title);
        addSourceButton = rootView.findViewById(R.id.addsource_continue);

        bottomContainer = rootView.findViewById(R.id.sourceadd_bottomlayout);
        progressBar = rootView.findViewById(R.id.addsource_progress);

        feedUrl = rootView.findViewById(R.id.sourceadd_feedUrl);
        feedName = rootView.findViewById(R.id.sourceadd_feedName);
        showInFeed = rootView.findViewById(R.id.switch_showInFeed);

        loadData();

        addSourceButton.setOnClickListener(view -> saveData());
        addSourceButton.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                addSourceButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_down));
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                addSourceButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                addSourceButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up));
                view.performClick();
            }
            return true;
        });
        return rootView;
    }

    private void showError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        for (int i = 0; i < bottomContainer.getChildCount(); i++) {
            View childView = bottomContainer.getChildAt(i);
            if (childView != null && childView.getTag() != null && childView.getTag().equals("error-message")) {
                bottomContainer.removeViewAt(i);
            }
        }

        if (errorMessage.equals("")) {
            return;
        }

        TextView errorText = new TextView(context);
        errorText.setText(errorMessage);
        errorText.setTextColor(context.getColor(R.color.textSecondary));
        errorText.setTag("error-message");
        errorText.setGravity(Gravity.CENTER);


        bottomContainer.addView(errorText, 0);
    }

    private void closeFragment(View view) {
        if (view != null) {
            PreferencesManager.vibrate(view);
        }
        if(isAdded()){
            getParentFragmentManager().popBackStack();
        }
    }
}