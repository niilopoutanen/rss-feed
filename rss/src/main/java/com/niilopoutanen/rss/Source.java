package com.niilopoutanen.rss;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "source")
public class Source implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;

    public String url;

    public String image;
    public String language;

    public boolean visible;
}
