package com.niilopoutanen.rss_feed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class ImageViewActivity extends AppCompatActivity {

    String url;
    Bitmap bitmap;
    TouchImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            url = extras.getString("imageurl");
            try{
                byte[] byteArray = getIntent().getByteArrayExtra("image");
                bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            }
            catch (Exception e){
                //image decode failed
            }
        }

        imageView = findViewById(R.id.imageview);

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }
        else{
            Picasso.get().load(url).into(imageView);
        }

        new Handler().postDelayed(() -> {
            Thread loadThread = new Thread(this::tryLoadHDimg);
            loadThread.start();
        }, 300);

        Button saveBtn = findViewById(R.id.saveimg);
        saveBtn.setOnClickListener(v -> saveImage());

    }
    private void saveImage(){
        String filename = String.format("%s %s",getString(R.string.imagefrom), getString(R.string.app_name));
        try{
            URL imgUrl = new URL(url);
            filename = String.format("%s %s",getString(R.string.imagefrom), imgUrl.getHost());
        }
        catch (Exception ignored){}

        String filepath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, filename, null);
        Uri uri = Uri.parse(filepath);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        sendBroadcast(intent);

        Toast.makeText(ImageViewActivity.this, getString(R.string.imagesaved), Toast.LENGTH_SHORT).show();
    }
    private void tryLoadHDimg() {
        Bitmap HDbitmap = null;
        try {
            URL imgUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            HDbitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(HDbitmap != null){
            Bitmap finalHDbitmap = HDbitmap;
            runOnUiThread(() -> {
                this.bitmap = finalHDbitmap;
                imageView.setImageBitmap(finalHDbitmap);
            });
        }
    }

}