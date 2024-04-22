package com.niilopoutanen.rss_feed.common;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.niilopoutanen.rss_feed.resources.R;

import androidx.annotation.Nullable;

import com.niilopoutanen.rss_feed.common.models.Preferences;

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
        if(context == null) return;

        boolean searchVisible = PreferencesManager.getBooleanPreference(Preferences.SP_SHOW_SEARCH, Preferences.PREFS_UI, Preferences.SP_SHOW_SEARCH_DEFAULT, context);
        if(!searchVisible){
            setVisibility(GONE);
            return;
        }
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

        LayoutParams fieldParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        searchField = new EditText(getContext());
        searchField.setHint(getContext().getString(R.string.search));
        searchField.setLayoutParams(fieldParams);
        searchField.setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_NO);
        searchField.setSingleLine(true);
        searchField.setBackground(null);
        searchField.setPadding(0,0,0,0);
        searchField.setTextColor(getContext().getColor(R.color.textPrimary));
        searchField.setHintTextColor(getContext().getColor(R.color.textSecondary));

        searchField.setOnEditorActionListener((v, actionId, event) -> {
            if(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                onStateUpdate(false, false);
            }
            return false;
        });

        int iconSize = PreferencesManager.dpToPx(20, getContext());
        MarginLayoutParams iconParams = new MarginLayoutParams(iconSize, iconSize);
        iconParams.rightMargin = gap;

        View searchIcon = new View(getContext());
        searchIcon.setLayoutParams(iconParams);
        searchIcon.setBackgroundResource(R.drawable.icon_search);
        searchIcon.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.textSecondary)));
        searchIcon.setOnClickListener(v -> showKeyboard());
        searchBar.setOnClickListener(v -> showKeyboard());

        searchBar.addView(searchIcon);
        searchBar.addView(searchField);

        return searchBar;
    }

    private View toggle(int gap){
        closeToggle = new TextView(getContext());
        closeToggle.setText(R.string.close);
        closeToggle.setOnClickListener(v -> onStateUpdate(false, true));
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                queryHandler.accept(s.toString());
                if(s.toString().length() != 0){
                    onStateUpdate(true, null);
                }
                else{
                    onStateUpdate(false, false);
                }
            }
        });
    }

    private void onStateUpdate(Boolean toggleVisible, Boolean resetField){
        if(toggleVisible != null){
            if(toggleVisible){
                closeToggle.setVisibility(VISIBLE);
            }
            else{
                closeToggle.setVisibility(GONE);
            }
        }

        if(resetField != null && resetField){
            hideKeyboard();
        }
    }

    private void hideKeyboard(){
        if(searchField == null) return;
        searchField.setText("");
        searchField.clearFocus();
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
    }

    private void showKeyboard(){
        if(searchField == null) return;
        searchField.requestFocus();
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchField, 0);
    }
}
