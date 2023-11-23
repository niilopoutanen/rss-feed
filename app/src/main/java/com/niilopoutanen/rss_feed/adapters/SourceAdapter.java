package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.activities.FeedActivity;
import com.niilopoutanen.rss_feed.fragments.SourceItem;
import com.niilopoutanen.rss_feed.models.MaskTransformation;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.Source;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rss_feed.utils.SaveSystem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SourceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final RecyclerView recyclerView;
    private List<Source> sources;
    private Context context;
    private Source tempSource;
    private FragmentManager manager;

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



    public SourceAdapter(List<Source> sources, RecyclerView recyclerView, FragmentManager manager) {
        this.sources = sources;
        this.recyclerView = recyclerView;
        this.manager = manager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return SourceItem.create(parent);
    }

    public void updateSources(List<Source> sources) {
        this.sources = sources;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Source source = sources.get(position);
        if(holder instanceof SourceItem){
            SourceItem sourceItem = (SourceItem)holder;
            sourceItem.bindData(source, manager);
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
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background, paint);

                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.icon_trash);

                    int intrinsicHeight = drawable.getIntrinsicHeight();
                    int iconMargin = 50;
                    int left = itemView.getRight() - drawable.getIntrinsicWidth() - iconMargin;
                    int top = itemView.getTop() + (itemView.getHeight() - intrinsicHeight) / 2;
                    int right = itemView.getRight() - iconMargin;
                    int bottom = top + intrinsicHeight;


                    drawable.setBounds(left, top, right, bottom);
                    drawable.draw(c);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }


    }

}