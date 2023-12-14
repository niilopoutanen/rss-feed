package com.niilopoutanen.rss_feed.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.activities.FeedActivity;
import com.niilopoutanen.rss_feed.activities.MainActivity;
import com.niilopoutanen.rss_feed.database.AppRepository;
import com.niilopoutanen.rss_feed.models.FeedResult;
import com.niilopoutanen.rss_feed.models.MaskTransformation;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.RecyclerViewInterface;
import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rssparser.WebUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

public class SourceItem extends RecyclerView.ViewHolder{
    private final TextView title;
    private final TextView desc;
    private final ImageView icon;
    private final RelativeLayout button;
    private final View container;

    private final Preferences preferences;
    private final Context context;
    public SourceItem(@NonNull View itemView, Preferences preferences, Context context) {
        super(itemView);
        this.preferences = preferences;
        this.context = context;


        title = itemView.findViewById(R.id.source_item_title);
        desc = itemView.findViewById(R.id.source_item_desc);
        icon = itemView.findViewById(R.id.source_item_icon);
        button = itemView.findViewById(R.id.source_item_button);
        container = itemView;
    }

    public static SourceItem create(ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.source_item, parent, false);
        Preferences preferences = PreferencesManager.loadPreferences(context);

        return new SourceItem(view, preferences, context);
    }

    public void bindData(Source source, FragmentManager manager){
        title.setText(source.title);
        desc.setVisibility(View.GONE);
        loadIcon(source.image);
        initButton(source, manager);
    }
    public void bindData(FeedResult result){
        title.setText(result.title);
        desc.setText(result.description);
        loadIcon(result.visualUrl);
        initButton(result);
    }


    private void loadIcon(String iconUrl){
        if(iconUrl == null || iconUrl.isEmpty()){
            icon.setBackground(AppCompatResources.getDrawable(context, R.drawable.element_background));
        }
        int iconSize = PreferencesManager.dpToPx(60, context);
        Picasso.get().load(iconUrl)
                  .transform(new MaskTransformation(context, R.drawable.element_background))
                  .resize(iconSize, iconSize)
                  .into(icon);
    }

    private void initButton(Source source, FragmentManager manager){
        Drawable edit = AppCompatResources.getDrawable(context, R.drawable.icon_edit);
        createIcon();
        setIcon(edit);

        container.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("source_id", source.id);
            bundle.putSerializable("preferences", preferences);
            Intent feedIntent = new Intent(v.getContext(), FeedActivity.class);
            feedIntent.putExtras(bundle);
            PreferencesManager.vibrate(v);
            v.getContext().startActivity(feedIntent);
        });

        button.setOnClickListener(v -> {
            AddSourceFragment addSourceFragment = new AddSourceFragment(source, context);
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.frame_container, addSourceFragment, "source_fragment");
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }
    private void createIcon(){
        View action = new View(context);
        int size = PreferencesManager.dpToPx(15, context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(size, size);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        action.setLayoutParams(layoutParams);
        action.setBackgroundTintList(ColorStateList.valueOf(PreferencesManager.getAccentColor(context)));

        button.addView(action);
    }
    private void setIcon(Drawable icon){
        View action = button.getChildAt(0);
        if (action == null){
            return;
        }
        action.setBackground(icon);
    }
    private void initButton(FeedResult result){
        createIcon();

        Drawable plus = AppCompatResources.getDrawable(context, R.drawable.icon_plus);

        setIcon(plus);

        button.setOnClickListener(v -> {
            if (!result.alreadyAdded) {
                Source source = new Source();
                source.title = result.title;
                source.url = WebUtils.formatUrl(result.feedId).toString();
                source.image = result.visualUrl;

                AppRepository repository = new AppRepository(context);
                repository.insert(source);
                Toast.makeText(v.getContext(), v.getContext().getString(R.string.sourceadded), Toast.LENGTH_LONG).show();

                Drawable checkmark = AppCompatResources.getDrawable(context, R.drawable.icon_checkmark);
                setIcon(checkmark);
            } else {
                Toast.makeText(v.getContext(), v.getContext().getString(R.string.sourcealreadyadded), Toast.LENGTH_LONG).show();
            }
        });



        container.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FeedActivity.class);
            Source source = new Source();
            source.title = result.title;
            source.url = WebUtils.formatUrl(result.feedId).toString();
            source.image = result.visualUrl;
            intent.putExtra("source", source);
            intent.putExtra("preferences", PreferencesManager.loadPreferences(v.getContext()));
            v.getContext().startActivity(intent);
        });
    }

}