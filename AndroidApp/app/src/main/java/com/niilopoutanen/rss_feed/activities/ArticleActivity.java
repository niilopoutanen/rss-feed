package com.niilopoutanen.rss_feed.activities;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.niilopoutanen.rss_feed.fragments.ArticleView;
import com.niilopoutanen.rssparser.Callback;
import com.niilopoutanen.rssparser.Item;
import com.niilopoutanen.rssparser.RSSException;
import com.niilopoutanen.rssparser.WebUtils;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

import net.dankito.readability4j.Article;
import net.dankito.readability4j.Readability4J;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ArticleActivity extends AppCompatActivity {
    private ProgressBar articleLoader;
    private Item post;
    private ArticleView articleView;
    private String resultData;
    private Preferences preferences;

    private final String articleCSS =
              "<style>\n" +
                        "    html,\n" +
                        "    body {\n" +
                        "        width: 100%;\n" +
                        "        min-width: fit-content;\n" +
                        "        margin: 0;\n" +
                        "        box-sizing: border-box;\n" +
                        "    }\n" +
                        "\n" +
                        "    body {\n" +
                        "        padding: 10px;\n" +
                        "    }\n" +
                        "    \n" +
                        "    a{\n" +
                        "        color: rgb(31, 143, 255);\n" +
                        "        text-decoration: none;\n" +
                        "        font-weight: 600;\n" +
                        "    }\n" +
                        "    img {\n" +
                        "        max-width: 100%;\n" +
                        "        height: auto;\n" +
                        "        border-radius: 10px;\n" +
                        "    }\n" +
                        "\n" +
                        "    figure {\n" +
                        "        margin: 0;\n" +
                        "        padding: 0;\n" +
                        "    }\n" +
                        "\n" +
                        "    blockquote {\n" +
                        "        margin: 0; \n" +
                        "        padding-left: 15px;        \n" +
                        "        position: relative;\n" +
                        "    }\n" +
                        "\n" +
                        "    blockquote::before {\n" +
                        "      content: \"\";\n" +
                        "      position: absolute;\n" +
                        "      left: 0;\n" +
                        "      top: 0;\n" +
                        "      width: 5px;\n" +
                        "      height: 100%;\n" +
                        "      background-color: rgb(31, 143, 255);\n" +
                        "      border-radius: 10px;\n" +
                        "    }\n" +
                        "</style>";

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
        articleView = new ArticleView(this);
        ((FrameLayout)findViewById(R.id.article_webview_container)).addView(articleView);

        Document document = Jsoup.parse(html);
        Element head = document.head();
        head.append(articleCSS);

        Elements h1Elements = document.select("h1");
        if(h1Elements.isEmpty()){
            Element title = new Element("h1").text(post.getTitle());
            document.body().prependChild(title);
        }
        articleView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                openWebView(request.getUrl().toString(), "");
                return true;
            }
        });
        WebView.setWebContentsDebuggingEnabled(true);
        articleView.loadDocument(document);
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