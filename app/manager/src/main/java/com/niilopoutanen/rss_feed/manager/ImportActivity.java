package com.niilopoutanen.rss_feed.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.niilopoutanen.rss_feed.common.PrimaryButton;
import com.niilopoutanen.rss_feed.common.StatusView;
import com.niilopoutanen.rss_feed.database.AppRepository;
import com.niilopoutanen.rss_feed.rss.Opml;
import com.niilopoutanen.rss_feed.rss.Source;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImportActivity extends AppCompatActivity {

    private StatusView statusView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opmlactivity);


        statusView = findViewById(R.id.activity_import_status_view);
        PrimaryButton primaryButton = findViewById(R.id.activity_import_primary_button);
        primaryButton.setOnClickListener(v -> finish());

        ActivityResultLauncher<Intent> fileHandler = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                statusView.addStatus(getString(com.niilopoutanen.rss_feed.common.R.string.import_starting));

                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        List<Source> sources = Opml.loadData(result, this);
                        if (sources != null && !sources.isEmpty()) {
                            AppRepository repository = new AppRepository(this);
                            for (Source source : sources) {
                                repository.insert(source);
                            }
                            runOnUiThread(() -> {
                                statusView.addStatus(getResources().getQuantityString(com.niilopoutanen.rss_feed.common.R.plurals.imported_sources, sources.size(), sources.size()),
                                        StatusView.Status.Type.SUCCESS);
                                statusView.addFinalEvent(() -> primaryButton.setIsEnabled(true));
                            });
                        }
                    } catch (IOException e) {
                        statusView.addStatus(getString(com.niilopoutanen.rss_feed.common.R.string.error_adding_source), StatusView.Status.Type.FAILURE);
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                });
            }
            else if (result.getResultCode() == Activity.RESULT_CANCELED){
                statusView.clearMessages();
            }
        });


        LinearLayout filePickButton = findViewById(R.id.activity_import_file_picker);
        filePickButton.setOnClickListener(v -> {
            Intent filePicker = new Intent(Intent.ACTION_GET_CONTENT);
            filePicker.setType("*/*");
            filePicker = Intent.createChooser(filePicker, getString(com.niilopoutanen.rss_feed.common.R.string.select_file_import));
            fileHandler.launch(filePicker);
        });


    }

}