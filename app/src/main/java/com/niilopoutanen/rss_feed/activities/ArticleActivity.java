package com.niilopoutanen.rss_feed.activities;

import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.niilopoutanen.rss_feed.common.PreferencesManager;
import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.common.models.Preferences;
import com.niilopoutanen.rss_feed.fragments.components.ArticleView;
import com.niilopoutanen.rss_feed.parser.Callback;
import com.niilopoutanen.rss_feed.parser.RSSException;
import com.niilopoutanen.rss_feed.parser.WebUtils;
import com.niilopoutanen.rss_feed.rss.Post;

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
    private Post post;
    private ArticleView articleView;
    private String resultData;
    private Preferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        EdgeToEdge.enable(this);
        preferences = PreferencesManager.loadPreferences(this);
        post = (Post) extras.get("post");

        if (savedInstanceState != null) {
            resultData = savedInstanceState.getString("content");
        }

        PreferencesManager.setSavedTheme(this, preferences);
        setContentView(com.niilopoutanen.rss_feed.R.layout.activity_article);
        articleLoader = findViewById(com.niilopoutanen.rss_feed.R.id.article_load);

        initializeBase();

        if (resultData == null || resultData.isEmpty()) {
            processArticle(post.link, new Callback<String>() {
                @Override
                public void onResult(String result) {
                    resultData = result;
                    initWebView(result);
                }

                @Override
                public void onError(RSSException e) {
                    if (e.getErrorType() == HttpURLConnection.HTTP_NOT_FOUND) {
                        initWebView(getString(R.string.error_url));
                    } else if (e.getErrorType() == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
                        initWebView(getString(R.string.error_host));
                    } else {
                        initWebView(getString(R.string.error_notsupported));
                    }
                }
            });
        } else {
            initWebView(resultData);
        }

        Bundle params = new Bundle();
        params.putString("url", post.link);
        params.putString("source_name", post.title);
        FirebaseAnalytics.getInstance(this).logEvent("read_article", params);
    }

    private void initializeBase() {
        articleView = findViewById(com.niilopoutanen.rss_feed.R.id.articleview);

        if (preferences.s_articlefullscreen) {
            Window window = getWindow();
            if (window != null) {
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }


        // Insets to bottom control
        RelativeLayout footerToggle = findViewById(com.niilopoutanen.rss_feed.R.id.article_footer_toggle);
        footerToggle.setOnClickListener(v -> showControls());
        ViewCompat.setOnApplyWindowInsetsListener(footerToggle, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.bottomMargin = Math.max(insets.bottom, PreferencesManager.dpToPx(10, this));
            mlp.rightMargin = Math.max(insets.right, PreferencesManager.dpToPx(10, this));
            footerToggle.getBackground().setAlpha(128);
            footerToggle.getChildAt(0).getBackground().setAlpha(128);

            ViewGroup.MarginLayoutParams articleMlp = (ViewGroup.MarginLayoutParams) articleView.getLayoutParams();
            articleMlp.bottomMargin = insets.bottom;
            articleMlp.topMargin = insets.top;
            articleView.setLayoutParams(articleMlp);

            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });


        if (!preferences.s_article_show_controls) {
            footerToggle.setVisibility(View.GONE);
        }

    }


    private void showControls() {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        sheet.setContentView(R.layout.dialog_article_controls);
        sheet.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        Window window = sheet.getWindow();
        if (window != null) window.setNavigationBarColor(getColor(R.color.element));
        sheet.show();

        View openInBrowser = sheet.findViewById(R.id.article_open_in_browser);
        if (openInBrowser != null) openInBrowser.setOnClickListener(v -> {
            if(post.link != null && !post.link.isEmpty()){
                Uri uri = Uri.parse(post.link);
                if(uri != null){
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            }

            sheet.dismiss();
        });

        View share = sheet.findViewById(R.id.article_share);
        if (share != null) share.setOnClickListener(v -> {
            Bundle params = new Bundle();
            params.putString("url", post.link);
            params.putString("source_name", post.title);
            FirebaseAnalytics.getInstance(this).logEvent("share_article", params);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, post.link);
            shareIntent.putExtra(Intent.EXTRA_TITLE, post.title);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.sharepost)));
            sheet.dismiss();
        });

        View focusMode = sheet.findViewById(R.id.article_focusmode);
        if (focusMode != null) focusMode.setOnClickListener(v -> {
            ViewGroup.LayoutParams params = articleView.getLayoutParams();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = (int) (displayMetrics.widthPixels * 0.6f);
            if (params.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                params.width = width;
            } else {
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            articleView.setLayoutParams(params);
            sheet.dismiss();
        });
    }

    private void initWebView(String html) {
        if(html == null) return;
        Document document = Jsoup.parse(html);

        Elements h1Elements = document.select("h1");
        if (h1Elements.isEmpty()) {
            if (post.title != null) {
                Element title = new Element("h1").text(post.title);
                document.body().prependChild(title);
            }
        }
        articleView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                runOnUiThread(() -> openSheet(request.getUrl().toString()));
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                runOnUiThread(() -> {
                    articleLoader.setVisibility(View.GONE);
                    articleView.setVisibility(View.VISIBLE);
                });
            }
        });
        articleView.loadDocument(document, post);
    }


    private void openSheet(String url) {

        final BottomSheetDialog webViewSheet = new BottomSheetDialog(this);
        webViewSheet.setContentView(R.layout.dialog_webview);
        webViewSheet.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        webViewSheet.getBehavior().setDraggable(false);

        TextView titleView = webViewSheet.findViewById(R.id.dialog_webview_title);

        TextView cancel = webViewSheet.findViewById(R.id.dialog_webview_cancel);


        WebView webView = webViewSheet.findViewById(R.id.dialog_webview);
        webView.getSettings().setJavaScriptEnabled(true);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        webView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, screenHeight));


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
                    webView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

                } catch (Exception e) {
                    titleView.setText(view.getTitle());
                }

            }
        });
        webView.loadUrl(url);

        webViewSheet.show();
    }


    private void processArticle(String url, Callback<String> callBack) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                URL urlObject = new URL(url);
                String html = WebUtils.connect(urlObject).toString();

                Readability4J readability = new Readability4J(url, html);
                Article article = readability.parse();
                runOnUiThread(() -> callBack.onResult(article.getContent()));

            } catch (RSSException r) {
                runOnUiThread(() -> callBack.onError(r));
            } catch (Exception e) {
                runOnUiThread(() -> callBack.onError(new RSSException(e.getMessage())));
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("content", resultData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (articleView != null) {
            articleView.destroy();
        }
    }
}