package com.niilopoutanen.rss_feed.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

public class SearchBar extends LinearLayoutCompat {
    private EditText searchField;
    private View searchIcon, closeIcon;
    public SearchBar(Context context) {
        super(context);
        init();
    }

    public SearchBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        Context context = getContext();
        setBackgroundResource(R.drawable.element_background);
        int edgePadding = PreferencesManager.dpToPx(8, context);
        setPadding(edgePadding,0,edgePadding,0);
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(HORIZONTAL);
        setMinimumHeight(PreferencesManager.dpToPx(35, context));

        LayoutParams fieldParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        fieldParams.weight = 1;
        searchField = new EditText(context);
        searchField.setHint("Search");
        searchField.setLayoutParams(fieldParams);
        searchField.setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_NO);
        searchField.setBackground(null);
        searchField.setPadding(0,0,0,0);
        searchField.setTextColor(context.getColor(R.color.textPrimary));

        int iconSize = PreferencesManager.dpToPx(20, context);
        LayoutParams iconParams = new LayoutParams(iconSize, iconSize);

        searchIcon = new View(context);
        searchIcon.setLayoutParams(iconParams);
        searchIcon.setBackgroundResource(R.drawable.icon_search);

        closeIcon = new View(context);
        closeIcon.setLayoutParams(iconParams);
        closeIcon.setBackgroundResource(R.drawable.icon_xmark);
        closeIcon.setVisibility(GONE);
        closeIcon.setOnClickListener(v -> clearFocus());

        searchField.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                closeIcon.setVisibility(VISIBLE);
            }
            else{
                closeIcon.setVisibility(GONE);
            }
        });

        addView(searchIcon);
        addView(searchField);
        addView(closeIcon);
    }

    public void clearFocus(){
        if(searchField == null) return;

        searchField.setFocusableInTouchMode(false);
        searchField.setFocusable(false);
        searchField.setFocusableInTouchMode(true);
        searchField.setFocusable(true);

        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
    }
}
