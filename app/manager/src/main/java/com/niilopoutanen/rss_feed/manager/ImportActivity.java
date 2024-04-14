package com.niilopoutanen.rss_feed.manager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.niilopoutanen.rss_feed.common.PrimaryButton;
import com.niilopoutanen.rss_feed.common.StatusView;
import com.niilopoutanen.rss_feed.database.AppRepository;
import com.niilopoutanen.rss_feed.parser.IconFinder;
import com.niilopoutanen.rss_feed.rss.Opml;
import com.niilopoutanen.rss_feed.rss.Source;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImportActivity extends AppCompatActivity {

    private StatusView statusView;
    private  LinearLayout filePickButton;
    private ActivityResultLauncher<Intent> fileHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);


        statusView = findViewById(R.id.activity_import_status_view);
        filePickButton = findViewById(R.id.activity_import_file_picker);

        PrimaryButton primaryButton = findViewById(R.id.activity_import_primary_button);
        primaryButton.setOnClickListener(v -> finish());

        fileHandler = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                onFileSelected(result);
                statusView.addStatus(getString(com.niilopoutanen.rss_feed.common.R.string.import_starting));

                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        List<Source> sources = Opml.loadData(result, this);
                        if (sources != null && !sources.isEmpty()) {
                            AppRepository repository = new AppRepository(this);
                            statusView.addStatus(getString(com.niilopoutanen.rss_feed.common.R.string.loading_icons));
                            for (Source source : sources) {
                                if(source.image == null || source.image.isEmpty()){
                                    source.image = IconFinder.get(source.url);
                                }
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
                        reloadFilePicker();
                    }
                });
            }
            else if (result.getResultCode() == Activity.RESULT_CANCELED){
                statusView.clearMessages();
            }
        });


        reloadFilePicker();
    }

    private void onFileSelected(ActivityResult result) {
        if (filePickButton == null) return;
        filePickButton.setOnClickListener(null);

        Intent data = result.getData();
        if (data == null || data.getData() == null) {
            filePickButton.setVisibility(View.GONE);
            return;
        }

        Uri uri = data.getData();
        TextView fileNameView = filePickButton.findViewById(R.id.activity_import_file_picker_name);
        String fileName = getFileNameFromUri(uri);

        if (fileName != null && fileNameView != null) {
            fileNameView.setText(fileName);
        } else {
            filePickButton.setVisibility(View.GONE);
        }
    }


    private String getFileNameFromUri(Uri uri) {
        if(uri == null || uri.getScheme() == null) return null;
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            }
            catch (Exception ignored) {}
        } else if (uri.getScheme().equals("file")) {
            fileName = uri.getLastPathSegment();
        }
        return fileName;
    }

    private void reloadFilePicker(){
        filePickButton.setOnClickListener(v -> {
            Intent filePicker = new Intent(Intent.ACTION_GET_CONTENT);
            filePicker.setType("*/*");
            filePicker = Intent.createChooser(filePicker, getString(com.niilopoutanen.rss_feed.common.R.string.select_file_import));
            if(fileHandler != null){
                fileHandler.launch(filePicker);
            }
        });
    }
}