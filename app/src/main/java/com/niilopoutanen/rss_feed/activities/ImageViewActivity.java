package com.niilopoutanen.rss_feed.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.palette.graphics.Palette;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.niilopoutanen.rss_feed.common.PreferencesManager;
import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.common.models.Preferences;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageViewActivity extends AppCompatActivity {

    String url;
    Bitmap bitmap;
    TouchImageView imageView;
    RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesManager.setSavedTheme(this, PreferencesManager.loadPreferences(this));
        setContentView(R.layout.activity_image_view);
        EdgeToEdge.enable(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("imageurl");
            if (url == null) {
                Toast.makeText(this, getString(R.string.error_invalid_image), Toast.LENGTH_LONG).show();
                return;
            }
        }
        container = findViewById(R.id.imageview_container);
        imageView = findViewById(R.id.imageview);
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bm, Picasso.LoadedFrom from) {
                bitmap = bm;
                setImage(bm);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}

        };
        if (PreferencesManager.loadPreferences(this).s_imagecache) {
            Picasso.get().load(url).into(target);
        } else {
            Picasso.get().load(url).networkPolicy(NetworkPolicy.NO_STORE).into(target);
        }

        Bundle params = new Bundle();
        params.putString("url", url);
        FirebaseAnalytics.getInstance(this).logEvent("view_image", params);

        LinearLayout saveBtn = findViewById(R.id.saveimg);
        saveBtn.setOnClickListener(v -> fetchFile());

        ViewCompat.setOnApplyWindowInsetsListener(saveBtn, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();

            mlp.bottomMargin = Math.max(PreferencesManager.dpToPx(10, this), insets.bottom);
            v.setLayoutParams(mlp);

            return WindowInsetsCompat.CONSUMED;
        });

    }

    /**
     * Loads the bitmap from internet
     */
    private void fetchFile() {
        String filename = String.format("%s %s", getString(R.string.imagefrom), getString(R.string.app_name));
        try {
            URL imgUrl = new URL(url);
            filename = String.format("%s %s", getString(R.string.imagefrom), imgUrl.getHost());
        } catch (Exception ignored) {
        }

        // Check if the permission has not been granted
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
        }

        if (bitmap == null) {
            String finalFilename = filename;
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    saveToGallery(bitmap, finalFilename);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            if (PreferencesManager.loadPreferences(this).s_imagecache) {
                Picasso.get().load(url).into(target);
            } else {
                Picasso.get().load(url).networkPolicy(NetworkPolicy.NO_STORE).into(target);

            }
        } else {
            saveToGallery(bitmap, filename);
        }

    }

    /**
     * Saves a loaded bitmap to user's gallery
     */
    private void saveToGallery(Bitmap image, String filename) {
        String filepath = MediaStore.Images.Media.insertImage(getContentResolver(), image, filename, null);
        Uri uri = Uri.parse(filepath);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        sendBroadcast(intent);

        Toast.makeText(ImageViewActivity.this, getString(R.string.imagesaved), Toast.LENGTH_SHORT).show();
    }

    private void setImage(Bitmap bm) {
        if (bm == null || imageView == null) return;
        imageView.setImageBitmap(bm);
        if (container == null ) return;
        Preferences preferences = PreferencesManager.loadPreferences(this);
        if(!preferences.s_image_viewer_gradient) return;

        Palette palette = Palette.from(bm).generate();
        List<Palette.Swatch> swatches = palette.getSwatches();
        swatches = new ArrayList<>(swatches);

        // Shuffle for random colors
        Collections.shuffle(swatches);
        List<Palette.Swatch> selectedSwatches = swatches.subList(0, Math.min(2, swatches.size()));
        int[] colors = new int[selectedSwatches.size()];
        for (int i = 0; i < selectedSwatches.size(); i++) {
            colors[i] = selectedSwatches.get(i).getRgb();
        }

        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors);
        gradientDrawable.setCornerRadius(0f);

        gradientDrawable.setAlpha(0);
        container.setBackground(gradientDrawable);

        ValueAnimator fadeIn = ValueAnimator.ofInt(0, 255);
        fadeIn.setDuration(800);

        fadeIn.addUpdateListener(animation -> {
            gradientDrawable.setAlpha((int) animation.getAnimatedValue());
        });

        fadeIn.start();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchFile();
            } else {
                Toast.makeText(this, getString(R.string.error_no_write_access), Toast.LENGTH_SHORT).show();
            }
        }
    }
}