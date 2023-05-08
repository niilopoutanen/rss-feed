package com.niilopoutanen.rss_feed;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.niilopoutanen.rss_feed.customization.Preferences;
import com.niilopoutanen.rss_feed.customization.PreferencesManager;
import com.niilopoutanen.rss_feed.sources.Source;

public class FeedActivity extends AppCompatActivity {

    private Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Source source = (Source) bundle.getSerializable("source");
            preferences = (Preferences) bundle.getSerializable("preferences");
            if (source != null && preferences != null) {
                FeedFragment myFragment = new FeedFragment(source, preferences);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.feedactivity_container, myFragment);
                transaction.commit();
            }
        }

        PreferencesManager.setSavedTheme(this, preferences);

        setContentView(R.layout.activity_feed);
        getWindow().setNavigationBarColor(getColor(android.R.color.transparent));
        FrameLayout frame = findViewById(R.id.feedactivity_container);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(frame, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top;
            v.setLayoutParams(mlp);
            
            return WindowInsetsCompat.CONSUMED;
        });
    }
}