package com.niilopoutanen.rss_feed.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.URL;

public class ImageViewActivity extends AppCompatActivity {

    String url;
    Bitmap bitmap;
    TouchImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesManager.setSavedTheme(this, PreferencesManager.loadPreferences(this));
        setContentView(R.layout.activity_image_view);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("imageurl");
        }

        imageView = findViewById(R.id.imageview);
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bm, Picasso.LoadedFrom from) {
                bitmap = bm;
                imageView.setImageBitmap(bitmap);
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


        LinearLayout saveBtn = findViewById(R.id.saveimg);
        saveBtn.setOnClickListener(v -> saveImage());

    }

    /**
     * Loads the bitmap from internet
     */
    private void saveImage() {
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
     * Saves a bitmap to user's gallery
     */
    private void saveToGallery(Bitmap image, String filename) {
        String filepath = MediaStore.Images.Media.insertImage(getContentResolver(), image, filename, null);
        Uri uri = Uri.parse(filepath);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        sendBroadcast(intent);

        Toast.makeText(ImageViewActivity.this, getString(R.string.imagesaved), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else {
                Toast.makeText(this, getString(R.string.nowriteaccess), Toast.LENGTH_SHORT).show();
            }
        }
    }
}