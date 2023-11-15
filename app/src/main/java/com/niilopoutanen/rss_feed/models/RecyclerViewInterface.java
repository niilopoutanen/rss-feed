package com.niilopoutanen.rss_feed.models;

/**
 * Interface for recyclerview item clicks with position
 */
public interface RecyclerViewInterface {
    void onItemClick(int position);

    void onItemLongClick(int position);
}
