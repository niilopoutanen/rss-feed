package com.niilopoutanen.rss_feed.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.text.style.QuoteSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rss_feed.adapters.ArticleAdapter;
import com.niilopoutanen.rss_feed.models.ArticleQuoteSpan;
import com.niilopoutanen.rss_feed.utils.Readability;
import com.niilopoutanen.rss_feed.models.WebCallBack;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ArticleActivity extends AppCompatActivity {
    List<ArticleAdapter.ArticleItem> views = new ArrayList<>();
    ArticleAdapter adapter;
    private RecyclerView articleContainer;
    private String title;
    private ProgressBar articleLoader;
    private String resultData;
    private String publisher;
    private Date publishTime;
    private URL postUrl;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        try {
            postUrl = new URL(extras.getString("postUrl"));
            publisher = extras.getString("postPublisher");
            publishTime = (Date) extras.get("postPublishTime");
            preferences = (Preferences) extras.get("preferences");

            if (savedInstanceState != null) {
                resultData = savedInstanceState.getString("content");
                title = savedInstanceState.getString("title");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        PreferencesManager.setSavedTheme(this, preferences);
        setContentView(R.layout.activity_article);

        articleLoader = findViewById(R.id.article_load);
        articleContainer = findViewById(R.id.article_recyclerview);


        initializeBase();

        if (resultData == null || resultData.isEmpty()) {
            ReadabilityThread(postUrl, new WebCallBack<String>() {
                @Override
                public void onResult(String result) {
                    runOnUiThread(() -> {
                        articleLoader.setVisibility(View.GONE);

                        if (result.equals("404")) {
                            createTextView(new SpannedString(getString(R.string.error_url)));
                        } else if (result.equals("408")) {
                            createTextView(new SpannedString(getString(R.string.error_host)));
                        } else {
                            resultData = result;
                            initializeContent(result);
                        }
                    });
                }
            });
        } else {
            initializeContent(resultData);
        }
    }

    private void initializeBase() {
        View background = findViewById(R.id.article_base);
        Window window = getWindow();
        int color = getColor(R.color.article1);

        switch (preferences.s_articlecolor) {
            case STAGE2:
                color = getColor(R.color.article2);
                break;
            case STAGE3:
                color = getColor(R.color.article3);
                break;
            case STAGE4:
                color = getColor(R.color.article4);
                break;
        }

        window.setNavigationBarColor(color);
        window.setStatusBarColor(color);
        background.setBackgroundColor(color);

        if (preferences.s_articlefullscreen) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void initializeContent(String result) {
        adapter = new ArticleAdapter(views, preferences, this, postUrl.toString(), publishTime, publisher);
        articleContainer.setAdapter(adapter);
        articleContainer.setLayoutManager(new LinearLayoutManager(this));

        parseSpanned(HtmlCompat.fromHtml(result, HtmlCompat.FROM_HTML_MODE_LEGACY));
        articleLoader.setVisibility(View.GONE);
    }

    private void parseSpanned(Spanned spanned) {
        views.add(new ArticleAdapter.TitleItem(title));
        SpannableStringBuilder builder = new SpannableStringBuilder(spanned);
        ImageSpan[] imageSpans = builder.getSpans(0, builder.length(), ImageSpan.class);

        if (imageSpans.length == 0) {
            createTextView(spanned);
            return;
        }

        int lastEnd = 0;
        for (ImageSpan imageSpan : imageSpans) {
            int start = builder.getSpanStart(imageSpan);
            int end = builder.getSpanEnd(imageSpan);

            createTextView((Spanned) builder.subSequence(lastEnd, start));
            createImageView(imageSpan);

            lastEnd = end;
        }

        // Handle remaining text after last image
        if (lastEnd < builder.length()) {
            createTextView((Spanned) builder.subSequence(lastEnd, builder.length()));
        }
    }


    private void createImageView(ImageSpan span) {
        int itemIndex = views.size();
        views.add(itemIndex, new ArticleAdapter.ImageItem(span.getSource()));
        adapter.notifyItemInserted(itemIndex);
    }


    private void createTextView(Spanned text) {
        if (text.length() > 0 && !TextUtils.isEmpty(text) && !text.toString().matches("\\A\\s*\\z")) {
            //remove excess line breaks from start and end
            int startIndex = 0;
            int endIndex = text.length();

            while (startIndex < text.length() && Character.isWhitespace(text.charAt(startIndex))) {
                startIndex++;
            }
            while (endIndex > 0 && Character.isWhitespace(text.charAt(endIndex - 1))) {
                endIndex--;
            }

            text = (Spanned) text.subSequence(startIndex, endIndex);

            SpannableString spannableString = new SpannableString(text);

            // Find all QuoteSpans and replace them with CustomQuoteSpan
            QuoteSpan[] quoteSpans = spannableString.getSpans(0, text.length(), QuoteSpan.class);
            for (QuoteSpan quoteSpan : quoteSpans) {
                int spanStart = spannableString.getSpanStart(quoteSpan);
                int spanEnd = spannableString.getSpanEnd(quoteSpan);
                int flags = spannableString.getSpanFlags(quoteSpan);
                spannableString.removeSpan(quoteSpan);
                spannableString.setSpan(new ArticleQuoteSpan(this), spanStart, spanEnd, flags);
            }
            if (!preferences.s_articlesinbrowser) {
                URLSpan[] linkSpans = spannableString.getSpans(0, text.length(), URLSpan.class);
                for (URLSpan linkSpan : linkSpans) {
                    int start = spannableString.getSpanStart(linkSpan);
                    int end = spannableString.getSpanEnd(linkSpan);
                    int flags = spannableString.getSpanFlags(linkSpan);
                    final String url = linkSpan.getURL();
                    final String linkText = spannableString.subSequence(start, end).toString();
                    URLSpan newSpan = new URLSpan(url) {
                        @Override
                        public void onClick(View widget) {
                            openWebView(url, linkText);
                        }
                    };

                    spannableString.removeSpan(linkSpan);
                    spannableString.setSpan(newSpan, start, end, flags);
                }
            }

            int itemIndex = views.size();
            views.add(new ArticleAdapter.SpannedItem(spannableString));
            adapter.notifyItemInserted(itemIndex);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void openWebView(String url, String titleText) {
        final BottomSheetDialog webViewSheet = new BottomSheetDialog(this, R.style.BottomSheetStyle);
        webViewSheet.setContentView(R.layout.dialog_webview);
        webViewSheet.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        webViewSheet.getBehavior().setDraggable(false);

        TextView titleView = webViewSheet.findViewById(R.id.dialog_webview_title);
        titleView.setText(titleText);

        TextView cancel = webViewSheet.findViewById(R.id.dialog_webview_cancel);


        WebView webView = webViewSheet.findViewById(R.id.dialog_webview);
        webView.getSettings().setJavaScriptEnabled(true);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        webView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screenHeight));


        cancel.setOnClickListener(view -> {
            webViewSheet.cancel();
            webView.destroy();
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    URL host = new URL(url);
                    titleView.setText(host.getHost());
                    webView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                } catch (Exception e) {
                    titleView.setText(view.getTitle());
                }

            }
        });
        webView.loadUrl(url);

        webViewSheet.show();
    }

    private void ReadabilityThread(URL url, WebCallBack<String> callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Readability readability = new Readability(url, 100000);
                readability.init(false, this);
                title = readability.separateTitle();
                callback.onResult(readability.outerHtml());
            } catch (Exception e) {
                if (e.getClass() == java.net.UnknownHostException.class) {
                    callback.onResult("408");
                    e.printStackTrace();
                } else {
                    callback.onResult("404");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("content", resultData);
        outState.putString("title", title);
    }
}