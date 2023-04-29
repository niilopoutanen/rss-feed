package com.niilopoutanen.rss_feed;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.niilopoutanen.rss_feed.customization.PreferencesManager;
import com.niilopoutanen.rss_feed.rss.MaskTransformation;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            url = extras.getString("imageurl");
        }
        
        ImageView imageView = findViewById(R.id.imageview);

        Picasso.get().load(url).into(imageView);
    }
}