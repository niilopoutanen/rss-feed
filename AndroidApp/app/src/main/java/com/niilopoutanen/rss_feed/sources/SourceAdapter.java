package com.niilopoutanen.rss_feed.sources;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.niilopoutanen.rss_feed.FeedActivity;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.customization.Preferences;
import com.niilopoutanen.rss_feed.customization.PreferencesManager;
import com.niilopoutanen.rss_feed.customization.SaveSystem;
import com.niilopoutanen.rss_feed.rss.MaskTransformation;
import com.niilopoutanen.rss_feed.web.WebCallBack;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.ViewHolder>{

    private List<Source> sources;
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private Context context;
    private Preferences preferences;

    private RecyclerView recyclerView;
    private Source tempSource;
    public SourceAdapter(List<Source> sources, Preferences preferences, RecyclerView recyclerView) {
        this.sources = sources;
        this.preferences = preferences;
        this.recyclerView = recyclerView;
    }


    @NonNull
    @Override
    public SourceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = inflater.inflate(R.layout.header_sources, parent, false);
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(40, 40, 40, 40);
            headerView.setLayoutParams(layoutParams);

            return new SourceAdapter.HeaderViewHolder(headerView);
        } else {
            View sourceItemView = inflater.inflate(R.layout.source_item, parent, false);
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(40, 0, 40, 40);
            sourceItemView.setLayoutParams(layoutParams);

            return new SourceAdapter.ViewHolder(sourceItemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SourceAdapter.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            Source source = sources.get(position -1);

            TextView sourceName = holder.sourceName;
            ImageView sourceImage = holder.sourceImage;
            View container = holder.itemView;

            container.setOnLongClickListener(view -> {
                PreferencesManager.vibrate(view, preferences, HapticFeedbackConstants.LONG_PRESS, context);
                askForSourceInput(source);
                return true;
            });
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

            Picasso.get().load(source.getImageUrl()).transform(new MaskTransformation(context, R.drawable.image_rounded)).into(sourceImage);
        }
        else if(getItemViewType(position) == VIEW_TYPE_HEADER){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            Button addNewButton = headerViewHolder.addNewButton;

            addNewButton.setOnClickListener(v ->{
                PreferencesManager.vibrate(v, preferences, context);
                askForSourceInput(null);
            });
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }
    @Override
    public int getItemCount() {
        if (sources.isEmpty()) {
            return 1; // return 1 for the header
        } else {
            return sources.size() + 1;
        }
    }
    public void askForSourceInput(Source source){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetStyle);
        bottomSheetDialog.setContentView(R.layout.dialog_addsource);

        TextView sourceAdd = bottomSheetDialog.findViewById(R.id.sourcedialog_add);
        TextView sourceCancel = bottomSheetDialog.findViewById(R.id.sourcedialog_cancel);

        EditText urlInput = bottomSheetDialog.findViewById(R.id.sourcedialog_feedUrl);
        EditText nameInput = bottomSheetDialog.findViewById(R.id.sourcedialog_feedName);

        LinearLayout sheetLayout = bottomSheetDialog.findViewById(R.id.addsource_layout);
        if(source != null){
            urlInput.setText(source.getFeedUrl());
            nameInput.setText(source.getName());
            sourceAdd.setText(context.getString(R.string.update));
            TextView title = bottomSheetDialog.findViewById(R.id.sourcedialog_title);
            title.setText(context.getString(R.string.updatesource));
        }
        sourceAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(urlInput.getWindowToken(), 0);

                for (int i = 0; i < sheetLayout.getChildCount(); i++) {
                    View childView = sheetLayout.getChildAt(i);
                    Object tag = childView.getTag();
                    if (tag != null && tag.equals("error-message")) {
                        sheetLayout.removeView(childView);
                    }
                }
                ProgressBar progress = bottomSheetDialog.findViewById(R.id.sourcedialog_progress);


                String inputUrl = urlInput.getText().toString();
                String inputName = nameInput.getText().toString();
                if(inputUrl.isEmpty()){
                    sheetLayout.addView(SourceValidator.createErrorMessage(context, "URL can't be empty"));
                    return;
                }
                sourceCancel.setOnClickListener(null);

                sourceAdd.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                bottomSheetDialog.setCancelable(false);
                SourceValidator.validate(inputUrl, inputName, new WebCallBack<Source>() {
                    @Override
                    public void onResult(Source result) {
                        Activity activity = (Activity)context;
                        if(result != null){
                            if(source == null){
                                SaveSystem.saveSources(context, new Source(result.getName(), result.getFeedUrl(), result.getImageUrl()));
                            }
                            else{
                                sources = SaveSystem.loadSources(context);
                                sources.removeIf(oldSource -> Objects.equals(oldSource.getName(), source.getName()));
                                sources.add(new Source(result.getName(), result.getFeedUrl(), result.getImageUrl()));
                                SaveSystem.saveSources(context, sources);
                            }
                            sources = SaveSystem.loadSources(context);
                            bottomSheetDialog.dismiss();

                            activity.runOnUiThread(() -> notifyDataSetChanged());
                        }
                        else{
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.setVisibility(View.GONE);
                                    sourceAdd.setVisibility(View.VISIBLE);
                                    bottomSheetDialog.setCancelable(true);
                                    sheetLayout.addView(SourceValidator.createErrorMessage(context, "Error with adding source. Please try again"));
                                }
                            });

                        }

                    }
                }, context);
            }
        });

        sourceCancel.setOnClickListener(view -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView sourceName;
        public ImageView sourceImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sourceName = itemView.findViewById(R.id.source_name);
            sourceImage = itemView.findViewById(R.id.source_image);
        }
    }

    public static class HeaderViewHolder extends ViewHolder {

        public RelativeLayout header;
        public Button addNewButton;
        public HeaderViewHolder(View itemView) {
            super(itemView);

            addNewButton = itemView.findViewById(R.id.addNewButton);

            header =  itemView.findViewById(R.id.actionbar_sources);
        }
    }
    public Source removeItem(int position) {
        List<Source> sourcesTemp = SaveSystem.loadSources(context);
        Source sourceToRemove = sourcesTemp.get(position -1);
        sourcesTemp.remove(sourceToRemove);
        SaveSystem.saveSources(context, sourcesTemp);
        sources.remove(position -1);
        notifyItemRemoved(position);

        return sourceToRemove;
    }
    private final Runnable undoDelete = new Runnable() {
        @Override
        public void run() {
            if(tempSource != null){
                sources = SaveSystem.loadSources(context);
                sources.add(tempSource);
                SaveSystem.saveSources(context, sources);
                notifyItemChanged(sources.size() - 1);
                tempSource = null;
            }
        }
    };
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

            TextView snackbarActionTextView = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_action);
            snackbarActionTextView.setAllCaps(false);
            snackbarActionTextView.setTypeface(ResourcesCompat.getFont(context, R.font.inter));
            snackbarActionTextView.setLetterSpacing(0.0f);
            snackbar.show();
        }
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View itemView = viewHolder.itemView;
                Paint paint = new Paint();
                RectF background;
                if(dX < 0) {
                    // Swiping left
                    paint.setColor(Color.parseColor("#FF0000"));
                    background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRoundRect(background, 25, 25, paint);

                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.icon_trash);

                    int intrinsicHeight = drawable.getIntrinsicHeight();
                    int iconMargin = 30;
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
