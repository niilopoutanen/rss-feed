package com.niilopoutanen.rss_feed.fragments.components;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.niilopoutanen.rss_feed.rss.Source;
import com.niilopoutanen.rss_feed.common.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ExtendedHeader extends FeedItem{
    public ExtendedHeader(Context context) {
        super(context);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.feed_extendedheader;
    }

    @Override
    public void onClick(Object data) {
        if(data instanceof Source){
            Source source = (Source) data;
            if(source.home == null || source.home.isEmpty()) return;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(source.home));
            context.startActivity(browserIntent);
        }
    }

    @Override
    public void bind(Object data) {
        if(data instanceof Source){
            Source source = (Source) data;
            TextView title = getContent().findViewById(R.id.extended_header_title);
            TextView desc = getContent().findViewById(R.id.extended_header_desc);
            ImageView icon = getContent().findViewById(R.id.extended_header_icon);

            if(source.title != null && !source.title.isEmpty()){
                title.setText(source.title);
            }
            else{
                title.setVisibility(View.GONE);
            }

            if(source.description != null && !source.description.isEmpty()){
                desc.setText(source.description);
            }
            else{
                desc.setVisibility(View.GONE);
            }

            if(source.image != null && !source.image.isEmpty()){
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        icon.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        icon.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                Picasso.get().load(source.image).into(target);
            }
            else{
                icon.setVisibility(View.GONE);
            }

        }
    }
}
