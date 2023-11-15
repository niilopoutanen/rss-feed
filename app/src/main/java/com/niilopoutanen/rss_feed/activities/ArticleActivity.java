package com.niilopoutanen.rss_feed.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
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

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Lifecycle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.niilopoutanen.rss_feed.fragments.ArticleView;
import com.niilopoutanen.rss_feed.models.Source;
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
        EdgeToEdge.enable(this);
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

        findViewById(R.id.article_viewinbrowser).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(post.getLink()))));
        findViewById(R.id.article_share).setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, post.getLink());
            shareIntent.putExtra(Intent.EXTRA_TITLE, post.getTitle());
            startActivity(Intent.createChooser(shareIntent, getString(R.string.sharepost)));

        });

        // Insets to bottom controls
        LinearLayout footer = findViewById(R.id.article_footer);
        ViewCompat.setOnApplyWindowInsetsListener(footer, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.leftMargin = insets.left;
            mlp.bottomMargin = insets.bottom;
            mlp.rightMargin = insets.right;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        View focusMode = findViewById(R.id.article_focusmode);
        if(focusMode != null){
            focusMode.setOnClickListener(v -> {
                ViewGroup.LayoutParams params = articleView.getLayoutParams();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = (int)(displayMetrics.widthPixels * 0.6f);
                if(params.width == ViewGroup.LayoutParams.MATCH_PARENT){
                    params.width = width;
                }
                else {
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                articleView.setLayoutParams(params);
            });
        }

        RelativeLayout footerToggle = findViewById(R.id.article_footer_toggle);
        if(preferences.s_article_show_controls){
            footerToggle.getBackground().setAlpha(120);
            footerToggle.getChildAt(0).getBackground().setAlpha(120);
            footerToggle.setOnClickListener(v -> toggleControls(footerToggle));
        }
        else{
            footerToggle.setVisibility(View.GONE);
        }


    }


    private void toggleControls(ViewGroup toggle){
        LinearLayout controls = findViewById(R.id.article_footer_controls);
        boolean visible = controls.getVisibility() == View.VISIBLE;

        if(visible){
            ObjectAnimator slideDown = ObjectAnimator.ofFloat(controls, "translationY", 0, PreferencesManager.dpToPx(60, this));
            slideDown.setInterpolator(new AccelerateDecelerateInterpolator());
            slideDown.setDuration(200);
            slideDown.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    controls.setVisibility(View.GONE);
                }
            });
            slideDown.start();
            toggle.getBackground().setAlpha(120);
            toggle.getChildAt(0).getBackground().setAlpha(120);
        }
        else {
            controls.setVisibility(View.VISIBLE);

            ObjectAnimator slideUp = ObjectAnimator.ofFloat(controls, "translationY", PreferencesManager.dpToPx(60, this), 0);
            slideUp.setInterpolator(new AccelerateDecelerateInterpolator());
            slideUp.setDuration(200);

            slideUp.start();
            toggle.getBackground().setAlpha(255);
            toggle.getChildAt(0).getBackground().setAlpha(255);
        }
    }

    private String getCSS(){
        String css =
                  "<style>\n" +
                            "    @font-face {\n" +
                            "        font-family: \"CustomFont\";\n" +
                            "        src: url(\"'$FONTFACE'\");\n" +
                            "        font-weight: normal;\n" +
                            "    }\n" +
                            "    @font-face {\n" +
                            "        font-family: \"CustomFont\";\n" +
                            "        src: url(\"'$BOLDFONTFACE'\");\n" +
                            "        font-weight: bold;\n" +
                            "    }\n" +
                            "    html,\n" +
                            "    body {\n" +
                            "        width: 100%;\n" +
                            "        min-width: fit-content;\n" +
                            "        margin: 0;\n" +
                            "        box-sizing: border-box;\n" +
                            "        color: '$TEXTCOLOR';\n" +
                            "        background-color: '$BACKGROUNDCOLOR';\n" +
                            "        font-family: \"CustomFont\";\n" +
                            "        font-size: '$FONTSIZE';\n" +
                            "    }\n" +
                            "\n" +
                            "    body {\n" +
                            "        padding: 10px;\n" +
                            "    }\n" +
                            "\n" +
                            "    a {\n" +
                            "        color: '$ACCENTCOLOR';\n" +
                            "        text-decoration: none;\n" +
                            "        font-weight: 600;\n" +
                            "    }\n" +
                            "\n" +
                            "    th, td{\n" +
                            "        border: 2px solid '$TEXTSECONDARY';\n" +
                            "    }\n" +
                            "    table{\n" +
                            "        border-collapse: collapse;\n" +
                            "        overflow: scroll;\n" +
                            "    }\n" +
                            "\n" +
                            "    img {\n" +
                            "        max-width: 100%;\n" +
                            "        height: auto;\n" +
                            "        border-radius: 10px;\n" +
                            "    }\n" +
                            "    ul{\n" +
                            "        padding-left: 20px;\n" +
                            "    }\n" +
                            "\n" +
                            "    figure {\n" +
                            "        margin: 0;\n" +
                            "        padding: 0;\n" +
                            "    }\n" +
                            "\n" +
                            "    blockquote {\n" +
                            "        margin: 0;\n" +
                            "        padding-left: 15px;\n" +
                            "        position: relative;\n" +
                            "    }\n" +
                            "\n" +
                            "    blockquote::before {\n" +
                            "        content: \"\";\n" +
                            "        position: absolute;\n" +
                            "        left: 0;\n" +
                            "        top: 0;\n" +
                            "        width: 5px;\n" +
                            "        height: 100%;\n" +
                            "        background-color:'$ACCENTCOLOR';\n" +
                            "        border-radius: 10px;\n" +
                            "    }\n" +
                            "</style>";

        String accentColor = formatColor(PreferencesManager.getAccentColor(this));
        String backgroundColor = formatColor(this.getColor(R.color.windowBg));
        String textColor = formatColor(this.getColor(R.color.textPrimary));
        String textSecondary = formatColor(this.getColor(R.color.textSecondary));
        String fontSize = String.valueOf(preferences.s_fontsize);

        String fontFace = "file:///android_res";
        String boldFontFace = "file:///android_res";

        switch (preferences.s_font){
            case INTER:
                fontFace += "/font/inter_regular.ttf";
                boldFontFace += "/font/inter_bold.ttf";
                break;
            case POPPINS:
                fontFace += "/font/poppins_regular.ttf";
                boldFontFace += "/font/poppins_bold.ttf";
                break;
            case ROBOTO_MONO:
                fontFace += "/font/roboto_mono_regular.ttf";
                boldFontFace += "/font/roboto_mono_bold.ttf";
                break;
            case ROBOTO_SERIF:
                fontFace += "/font/roboto_serif_regular.ttf";
                boldFontFace += "/font/roboto_serif_bold.ttf";
                break;
        }
        css = css.replace("'$FONTFACE'", fontFace);
        css = css.replace("'$FONTSIZE'", fontSize);
        css = css.replace("'$BOLDFONTFACE'", boldFontFace);

        css = css.replace("'$ACCENTCOLOR'", accentColor);
        css = css.replace("'$TEXTCOLOR'", textColor);
        css = css.replace("'$TEXTSECONDARY'", textSecondary);
        css = css.replace("'$BACKGROUNDCOLOR'", backgroundColor);

        return css;
    }

    private String formatColor(int colorID){
        int red = (colorID >> 16) & 0xFF;
        int green = (colorID >> 8) & 0xFF;
        int blue = colorID & 0xFF;

        return String.format(Locale.US ,"rgb(%d, %d, %d)", red, green, blue);
    }
    private void initWebView(String html){
        articleView = findViewById(R.id.articleview);

        Document document = Jsoup.parse(html);
        Element head = document.head();
        head.append(getCSS());

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
        articleView.loadDocument(document);
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
            catch (Exception ignored) {}
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