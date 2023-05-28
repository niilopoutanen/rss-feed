package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.activities.FeedActivity;
import com.niilopoutanen.rss_feed.models.MaskTransformation;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.Source;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rss_feed.utils.SaveSystem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.ViewHolder> {
    private final Preferences preferences;
    private final RecyclerView recyclerView;
    private List<Source> sources;
    private Context context;
    private Source tempSource;
    private final Runnable undoDelete = new Runnable() {
        @Override
        public void run() {
            if (tempSource != null) {
                sources = SaveSystem.loadContent(context);
                sources.add(tempSource);
                SaveSystem.saveContent(context, sources);
                notifyItemChanged(sources.size());
                tempSource = null;
            }
        }
    };
    private final View.OnLongClickListener onLongClickListener;


    public SourceAdapter(List<Source> sources, Preferences preferences, RecyclerView recyclerView, View.OnLongClickListener onClickListener) {
        this.sources = sources;
        this.preferences = preferences;
        this.recyclerView = recyclerView;
        this.onLongClickListener = onClickListener;
    }

    @NonNull
    @Override
    public SourceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View sourceItemView = inflater.inflate(R.layout.source_item, parent, false);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(40, 0, 40, 40);
        sourceItemView.setLayoutParams(layoutParams);

        return new SourceAdapter.ViewHolder(sourceItemView);
    }

    public void updateSources(List<Source> sources) {
        this.sources = sources;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull SourceAdapter.ViewHolder holder, int position) {
        Source source = sources.get(position);

        TextView sourceName = holder.sourceName;
        ImageView sourceImage = holder.sourceImage;
        View container = holder.itemView;

        container.setOnLongClickListener(onLongClickListener);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("source", source);
                bundle.putSerializable("preferences", preferences);
                Intent feedIntent = new Intent(v.getContext(), FeedActivity.class);
                feedIntent.putExtras(bundle);
                PreferencesManager.vibrate(v, preferences, context);
                v.getContext().startActivity(feedIntent);
            }
        });

        sourceName.setText(source.getName());
        if (source.getImageUrl() != null) {
            sourceImage.setVisibility(View.VISIBLE);
            Picasso.get().load(source.getImageUrl()).resize(70, 70).transform(new MaskTransformation(context, R.drawable.image_rounded)).into(sourceImage);
        } else {
            sourceImage.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return sources.size();
    }


    public Source removeItem(int position) {
        List<Source> sourcesTemp = SaveSystem.loadContent(context);
        Source sourceToRemove = sourcesTemp.get(position);
        sourcesTemp.remove(sourceToRemove);
        SaveSystem.saveContent(context, sourcesTemp);
        sources.remove(position);
        notifyItemRemoved(position);

        return sourceToRemove;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView sourceName;
        public ImageView sourceImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sourceName = itemView.findViewById(R.id.source_name);
            sourceImage = itemView.findViewById(R.id.source_image);
        }
    }

    public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private final Context context;

        public SwipeToDeleteCallback(Context context) {
            super(0, ItemTouchHelper.LEFT);
            this.context = context;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            tempSource = removeItem(position);

            Snackbar snackbar = Snackbar.make(recyclerView, context.getString(R.string.sourceremoved), Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.cancel, v -> undoDelete.run());

            TextView snackbarActionTextView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_action);
            snackbarActionTextView.setAllCaps(false);
            snackbarActionTextView.setTypeface(ResourcesCompat.getFont(context, R.font.inter));
            snackbarActionTextView.setTextColor(ContextCompat.getColor(context, R.color.surface));
            snackbarActionTextView.setLetterSpacing(0.0f);

            snackbar.show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View itemView = viewHolder.itemView;
                if (dX < 0) {
                    // Swiping left
                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.icon_trash);

                    int intrinsicHeight = drawable.getIntrinsicHeight();
                    int iconMargin = 30;
                    int left = itemView.getRight() - drawable.getIntrinsicWidth() - iconMargin;
                    int top = itemView.getTop() + (itemView.getHeight() - intrinsicHeight) / 2;
                    int right = itemView.getRight() - iconMargin;
                    int bottom = top + intrinsicHeight;

                    int startColor = ContextCompat.getColor(context, R.color.textSecondary);
                    int endColor = Color.RED;

                    int redIntensity = (int) (Color.red(startColor) + (Color.red(endColor) - Color.red(startColor)) * Math.abs(dX) / itemView.getWidth());
                    int greenIntensity = (int) (Color.green(startColor) + (Color.green(endColor) - Color.green(startColor)) * Math.abs(dX) / itemView.getWidth());
                    int blueIntensity = (int) (Color.blue(startColor) + (Color.blue(endColor) - Color.blue(startColor)) * Math.abs(dX) / itemView.getWidth());
                    int interpolatedColor = Color.rgb(redIntensity, greenIntensity, blueIntensity);

                    drawable.setColorFilter(interpolatedColor, PorterDuff.Mode.SRC_IN);

                    drawable.setBounds(left, top, right, bottom);
                    drawable.draw(c);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }


    }

}
