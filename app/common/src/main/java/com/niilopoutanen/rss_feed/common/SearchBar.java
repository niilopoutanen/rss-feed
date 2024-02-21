package com.niilopoutanen.rss_feed.common;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import java.util.function.Consumer;

public class SearchBar extends LinearLayout {
    private EditText searchField;
    private TextView closeToggle;
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
        setOrientation(HORIZONTAL);
        setLayoutTransition(new LayoutTransition());
        setGravity(Gravity.CENTER_VERTICAL);

        int gap = PreferencesManager.dpToPx(8, context);
        addView(bar(gap));
        addView(toggle(gap));
    }

    private View bar(int gap){
        LinearLayout searchBar = new LinearLayout(getContext());
        searchBar.setBackgroundResource(R.drawable.element_background);
        searchBar.setPadding(gap,0,gap,0);
        searchBar.setGravity(Gravity.CENTER_VERTICAL);
        searchBar.setOrientation(HORIZONTAL);
        searchBar.setMinimumHeight(PreferencesManager.dpToPx(35, getContext()));
        LinearLayout.LayoutParams barParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        barParams.weight = 1;
        searchBar.setLayoutParams(barParams);

        LayoutParams fieldParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        fieldParams.weight = 1;
        searchField = new EditText(getContext());
        searchField.setHint("Search");
        searchField.setLayoutParams(fieldParams);
        searchField.setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_NO);
        searchField.setSingleLine(true);
        searchField.setBackground(null);
        searchField.setPadding(0,0,0,0);
        searchField.setTextColor(getContext().getColor(R.color.textPrimary));
        searchField.setHintTextColor(getContext().getColor(R.color.textSecondary));
        searchField.setOnFocusChangeListener((v, hasFocus) -> onFocusChanged(hasFocus));
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    clearFocus(false);
                }
                return false;
            }
        });

        int iconSize = PreferencesManager.dpToPx(20, getContext());
        MarginLayoutParams iconParams = new MarginLayoutParams(iconSize, iconSize);
        iconParams.rightMargin = gap;

        View searchIcon = new View(getContext());
        searchIcon.setLayoutParams(iconParams);
        searchIcon.setBackgroundResource(R.drawable.icon_search);
        searchIcon.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.textSecondary)));

        searchBar.addView(searchIcon);
        searchBar.addView(searchField);

        return searchBar;
    }

    private View toggle(int gap){
        closeToggle = new TextView(getContext());
        closeToggle.setText("Close");
        closeToggle.setOnClickListener(v -> clearFocus(true));
        closeToggle.setVisibility(GONE);
        MarginLayoutParams toggleParams = new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        toggleParams.leftMargin = gap;
        closeToggle.setLayoutParams(toggleParams);
        return closeToggle;
    }

    public void setQueryHandler(Consumer<String> queryHandler){
        if(searchField == null) return;
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                queryHandler.accept(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void onFocusChanged(boolean hasFocus){
        if(closeToggle == null) return;
        if(hasFocus){
            closeToggle.setVisibility(VISIBLE);
        }
        else{
            closeToggle.setVisibility(GONE);
        }
    }
    public void clearFocus(boolean reset){
        if(searchField == null) return;

        searchField.setFocusableInTouchMode(false);
        searchField.setFocusable(false);
        searchField.setFocusableInTouchMode(true);
        searchField.setFocusable(true);

        if(reset){
            searchField.setText("");
        }

        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
    }
}
