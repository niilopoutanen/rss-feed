package com.niilopoutanen.rss_feed.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.text.style.QuoteSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.niilopoutanen.rssparser.Callback;
import com.niilopoutanen.rssparser.Item;
import com.niilopoutanen.rssparser.RSSException;
import com.niilopoutanen.rssparser.WebUtils;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.adapters.ArticleAdapter;
import com.niilopoutanen.rss_feed.models.ArticleQuoteSpan;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

import net.dankito.readability4j.Article;
import net.dankito.readability4j.Readability4J;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ArticleActivity extends AppCompatActivity {
    private ProgressBar articleLoader;
    private Item post;
    private WebView webView;
    private String resultData;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        preferences = (Preferences) extras.get("preferences");
        post  = (Item)extras.get("item");

        if (savedInstanceState != null) {
            resultData = savedInstanceState.getString("content");
        }

        PreferencesManager.setSavedTheme(this, preferences);
        setContentView(R.layout.activity_article);

        articleLoader = findViewById(R.id.article_load);
        webView = findViewById(R.id.article_webview);

        initializeBase();

        if (resultData == null || resultData.isEmpty()) {
            readabilityProcessor(post.getLink(), new Callback<String>() {
                @Override
                public void onResult(String result) {
                    articleLoader.setVisibility(View.GONE);
                    resultData = result;
                    initializeContent(result);
                }

                @Override
                public void onError(RSSException e) {
                    if (e.getErrorType() == HttpURLConnection.HTTP_NOT_FOUND) {
                        initializeContent(getString(R.string.error_url));
                    } else if (e.getErrorType() == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
                        initializeContent(getString(R.string.error_host));
                    }
                }
            });
        } else {
            initializeContent(resultData);
        }
    }

    private void initializeBase() {
        if (preferences.s_articlefullscreen) {
            Window window = getWindow();
            if(window != null){
                 window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
    }

    private void initializeContent(String result) {
        initWebView(result);

    }

    private void initWebView(String html){
        Document document = Jsoup.parse(html);
        Element head = document.head();
        head.append("<style> img { display: block; max-width: 100%; height: auto; } </style>");

        Elements h1Elements = document.select("h1");
        if(h1Elements.isEmpty()){
            Element title = new Element("h1").text(post.getTitle());
            document.body().prependChild(title);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String weburl){
                runOnUiThread(() -> articleLoader.setVisibility(View.GONE));
            }
        });
        webView.loadData(document.toString(), "text/html", "utf-8");
    }


    /**
     * Opens a WebView sheet
     *
     * @param url       URL to open
     * @param titleText Text to show on header. Gets replaced when the URL is fully loaded
     */
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


    private void readabilityProcessor(String url, Callback<String> callBack) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                URL urlObject = new URL(url);
                String html = WebUtils.connect(urlObject).toString();

                Readability4J readability = new Readability4J(url, html);
                Article article = readability.parse();
                runOnUiThread(() -> callBack.onResult(article.getContent()));

            }
            catch (RSSException r){
                runOnUiThread(() ->  callBack.onError(r));
            }
            catch (Exception ignored) {}
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("content", resultData);
    }
}