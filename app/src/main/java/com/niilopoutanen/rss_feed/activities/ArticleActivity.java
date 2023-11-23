package com.niilopoutanen.rss_feed.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.fragments.ArticleView;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rssparser.Callback;
import com.niilopoutanen.rssparser.Item;
import com.niilopoutanen.rssparser.RSSException;
import com.niilopoutanen.rssparser.WebUtils;

import net.dankito.readability4j.Article;
import net.dankito.readability4j.Readability4J;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ArticleActivity extends AppCompatActivity {
    private ProgressBar articleLoader;
    private Item post;
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
                    resultData = result;
                    initWebView(result);
                }

                @Override
                public void onError(RSSException e) {
                    if (e.getErrorType() == HttpURLConnection.HTTP_NOT_FOUND) {
                        initWebView(getString(R.string.error_url));
                    } else if (e.getErrorType() == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
                        initWebView(getString(R.string.error_host));
                    }
                    else{
                        initWebView(getString(R.string.error_notsupported));
                    }
                }
            });
        } else {
            initWebView(resultData);
        }
    }

    private void initializeBase() {
        if (preferences.s_articlefullscreen) {
            Window window = getWindow();
            if(window != null){
                 window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }



        // Insets to bottom control
        RelativeLayout footerToggle = findViewById(R.id.article_footer_toggle);
        footerToggle.setOnClickListener(v -> showControls());
        ViewCompat.setOnApplyWindowInsetsListener(footerToggle, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.bottomMargin = insets.bottom;
            mlp.rightMargin = insets.right;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });


        if(!preferences.s_article_show_controls){
            footerToggle.setVisibility(View.GONE);
        }

    }


    private void showControls(){
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        sheet.setContentView(R.layout.dialog_article_controls);
        sheet.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        sheet.show();
        View openInBrowser = sheet.findViewById(R.id.article_open_in_browser);
        if(openInBrowser != null) openInBrowser.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(post.getLink())));
            sheet.dismiss();
        });

        View share = sheet.findViewById(R.id.article_share);
        if(share != null)share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, post.getLink());
            shareIntent.putExtra(Intent.EXTRA_TITLE, post.getTitle());
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

    private void initWebView(String html){
        articleView = findViewById(R.id.articleview);


        Document document = Jsoup.parse(html);

        Elements h1Elements = document.select("h1");
        if(h1Elements.isEmpty()){
            if(post.getTitle() != null){
                Element title = new Element("h1").text(post.getTitle());
                document.body().prependChild(title);
            }
        }
        articleView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                runOnUiThread(() -> openWebView(request.getUrl().toString()));
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
        articleView.loadDocument(document, post.getCategories());
    }


    /**
     * Opens a WebView sheet
     *
     * @param url       URL to open
     */
    private void openWebView(String url) {

        final BottomSheetDialog webViewSheet = new BottomSheetDialog(this, R.style.BottomSheetStyle);
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
            catch (Exception e) {
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
        if(articleView != null){
            articleView.destroy();
        }
    }
}