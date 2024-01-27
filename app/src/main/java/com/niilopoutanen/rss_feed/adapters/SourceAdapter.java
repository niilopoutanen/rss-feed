package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.database.AppDatabase;
import com.niilopoutanen.rss_feed.database.DatabaseThread;
import com.niilopoutanen.rss_feed.fragments.components.SourceItem;
import com.niilopoutanen.rss_feed.rss.Source;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SourceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final RecyclerView recyclerView;
    private List<Source> sources;
    private Context context;
    private Source tempSource;
    private final FragmentManager manager;

    private final Runnable undoDelete = new Runnable() {
        @Override
        public void run() {
            if (tempSource != null) {
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    AppDatabase database = AppDatabase.getInstance(context);
                    database.sourceDao().insert(tempSource);
                    tempSource = null;
                });
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
        if (sources == null) {
            return;
        }
        Source source = sources.get(position);
        if (holder instanceof SourceItem) {
            SourceItem sourceItem = (SourceItem) holder;
            sourceItem.bindData(source, manager);
        }
    }


    @Override
    public int getItemCount() {
        if (sources == null) {
            return 0;
        }
        return sources.size();
    }


    public void removeItem(int position, DatabaseThread<Source> thread) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Source target = sources.get(position);
                AppDatabase database = AppDatabase.getInstance(context);
                database.sourceDao().delete(target);
                thread.complete(target);
            }
        });
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
            removeItem(position, result -> tempSource = result);

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
