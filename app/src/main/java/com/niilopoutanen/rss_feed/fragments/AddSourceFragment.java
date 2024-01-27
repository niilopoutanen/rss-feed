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

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.transition.MaterialSharedAxis;
import com.niilopoutanen.rss_feed.rss.Source;
import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.database.AppRepository;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rss_feed.parser.IconFinder;
import com.niilopoutanen.rss_feed.parser.Parser;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddSourceFragment extends Fragment {

    private Source source;

    private EditText feedUrl, feedName;
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

        TextView buttonText = (TextView) ((RelativeLayout) addSourceButton).getChildAt(0);
        buttonText.setText(context.getString(R.string.update));
    }

    private void saveData() {
        showError("");
        Activity activity = (Activity) context;
        progressBar.setVisibility(View.VISIBLE);
        Source userInput = new Source();
        userInput.title = feedName.getText().toString();
        userInput.url = feedUrl.getText().toString();
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if (Parser.isValid(userInput)) {
                Parser parser = new Parser();
                parser.load(userInput.url);

                //set id if it exists
                if(source != null){
                    parser.source.id = source.id;
                }

                source = parser.source;
                source.url = userInput.url;

                //set name if not empty
                if(userInput.title != null && !userInput.title.isEmpty()){
                    source.title = userInput.title;
                }

                source.visible = showInFeed.isChecked();

                if (source.image == null || source.image.isEmpty()) {
                    source.image = IconFinder.get(source.url);
                }

                save(source);

                activity.runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    closeFragment(null);
                });
            } else {
                activity.runOnUiThread(() -> {
                    showError(context.getString(R.string.error_adding_source));
                });
            }
        });

    }

    private void save(Source source) {
        AppRepository repository = new AppRepository(context);
        repository.insert(source);
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
        if(context == null) context = rootView.getContext();

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
        if (isAdded()) {
            getParentFragmentManager().popBackStack();
        }
    }
}