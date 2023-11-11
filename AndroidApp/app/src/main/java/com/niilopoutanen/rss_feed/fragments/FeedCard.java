package com.niilopoutanen.rss_feed.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Preferences.FeedCardStyle;
import com.niilopoutanen.rssparser.Item;

public class FeedCard {
    private Context context;
    private Item item;
    private final FeedCardStyle cardStyle;
    private View element;

    public FeedCard(FeedCardStyle cardStyle, Context context){
        this.cardStyle = cardStyle;
        this.context = context;
    }

    public void setItem(Item item){
        this.item = item;
    }

    public View getView(){
        return this.element;
    }
    private void init(ViewGroup parent){
        if(item == null){
            return;
        }

        element = inflate(parent);

    }

    private View inflate(ViewGroup parent){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        switch (cardStyle){
            case SMALL:
                return layoutInflater.inflate(R.layout.feedcard_small, parent);
            case NONE:
                View card = layoutInflater.inflate(R.layout.feedcard_small, parent);
                View image = card.findViewById(R.id.feedcard_image);
                image.setVisibility(View.GONE);
                return card;
            case LARGE:
            default:
                return layoutInflater.inflate(R.layout.feedcard, parent);
        }
    }


    public static class FeedCardViewHolder extends RecyclerView.ViewHolder {
        private final FeedCard feedCard;

        public FeedCardViewHolder(FeedCard feedCard) {
            super(feedCard.getView());
            this.feedCard = feedCard;
        }

        public void bindData(Item item) {
            feedCard.setItem(item);
            feedCard.init((ViewGroup) itemView);
        }
    }
}
