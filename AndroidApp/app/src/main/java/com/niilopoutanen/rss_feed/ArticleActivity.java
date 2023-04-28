package com.niilopoutanen.rss_feed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.QuoteSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.niilopoutanen.rss_feed.customization.Preferences;
import com.niilopoutanen.rss_feed.customization.PreferencesManager;
import com.niilopoutanen.rss_feed.rss.ArticleQuoteSpan;
import com.niilopoutanen.rss_feed.rss.MaskTransformation;
import com.niilopoutanen.rss_feed.rss.Readability;
import com.niilopoutanen.rss_feed.web.WebCallBack;
import com.squareup.picasso.Picasso;

public class ArticleActivity extends AppCompatActivity {
    private LinearLayout articleContainer;
    private String title;
    private ProgressBar articleLoader;

    private String publisher;
    private Date publishTime;
    private URL postUrl;
    private Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try{
                postUrl = new URL(extras.getString("postUrl"));
                publisher = extras.getString("postPublisher");
                publishTime = (Date)extras.get("postPublishTime");
                preferences = (Preferences)extras.get("preferences");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        PreferencesManager.setSavedTheme(this, preferences);
        setContentView(R.layout.activity_article);
        articleLoader = findViewById(R.id.article_load);
        articleContainer = findViewById(R.id.articleview);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.article_base), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top;
            v.setLayoutParams(mlp);

            return WindowInsetsCompat.CONSUMED;
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels - PreferencesManager.dpToPx(200, this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        articleContainer.setLayoutParams(layoutParams);

        initializeBase();
        ReadabilityThread(postUrl, new WebCallBack<String>() {
            @Override
            public void onResult(String result) {
                if(result.equals("404")){
                    runOnUiThread(() -> {
                        articleLoader.setVisibility(View.GONE);
                        createTextView(new SpannedString(ArticleActivity.this.getString(R.string.error_url)));
                    });
                }
                else if(result.equals("408")){
                    runOnUiThread(() -> {
                        articleLoader.setVisibility(View.GONE);
                        createTextView(new SpannedString(ArticleActivity.this.getString(R.string.error_host)));
                    });
                }
                else{
                    runOnUiThread(() -> {
                        LinearLayout.LayoutParams restoreParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        articleContainer.setLayoutParams(restoreParams);
                        initializeContent(result);
                    });
                }
            }
        });
    }
    private void initializeBase(){
        TextView publishTimeView = findViewById(R.id.article_publishtime);
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        publishTimeView.setText(dateFormat.format(publishTime));

        LinearLayout articleReturn = findViewById(R.id.article_return);
        articleReturn.setOnClickListener(v -> finish());

        TextView publisherView = findViewById(R.id.article_source);
        publisherView.setText(publisher);

        RelativeLayout viewInBrowser = findViewById(R.id.article_viewinbrowser);
        viewInBrowser.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl.toString()));
            startActivity(intent);
        });
    }
    private void initializeContent(String result){
        TextView titleView = findViewById(R.id.article_title);
        titleView.setText(title);

        parseSpanned(HtmlCompat.fromHtml(result, HtmlCompat.FROM_HTML_MODE_LEGACY));

        articleLoader.setVisibility(View.GONE);
    }
    private void parseSpanned(Spanned spanned) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spanned);
        int subSequenceStart = 0;
        ImageSpan[] imageSpans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ImageSpan.class);
        if(imageSpans.length == 0){
            createTextView(spanned);
            return;
        }
        for (ImageSpan imageSpan : imageSpans) {
            int start = spannableStringBuilder.getSpanStart(imageSpan);
            int end = spannableStringBuilder.getSpanEnd(imageSpan);
            createTextView((Spanned) spannableStringBuilder.subSequence(subSequenceStart, start));
            subSequenceStart = end;

            createImageView(imageSpan);

        }
        // Handle remaining text after last image
        if (subSequenceStart < spannableStringBuilder.length()) {
            createTextView((Spanned) spannableStringBuilder.subSequence(subSequenceStart, spannableStringBuilder.length()));
        }
    }

    private void createImageView(ImageSpan span) {
        ImageView imageView = new ImageView(this);
        articleContainer.addView(imageView);


        ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
        int marginPx = PreferencesManager.dpToPx(15, this);
        margin.setMargins(0,marginPx, 0,marginPx);

        Picasso.get().load(span.getSource())
                .resize(PreferencesManager.getImageWidth(PreferencesManager.ARTICLE_IMAGE, this), 0)
                .transform(new MaskTransformation(this, R.drawable.image_rounded))
                .into(imageView);

    }
    private void createTextView(Spanned text){
        if(text.length() > 0 && !TextUtils.isEmpty(text) && !text.toString().matches("\\A\\s*\\z")){
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


            TextView textView = new TextView(ArticleActivity.this);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(text);
            textView.setTextColor(getColor(R.color.textPrimary));

            textView.setTypeface(PreferencesManager.getSavedFont(preferences, this));

            SpannableString spannableString = new SpannableString(text);

            // Find all QuoteSpans and replace them with CustomQuoteSpan
            QuoteSpan[] quoteSpans = spannableString.getSpans(0, text.length(), QuoteSpan.class);
            for(QuoteSpan quoteSpan : quoteSpans){
                int spanStart = spannableString.getSpanStart(quoteSpan);
                int spanEnd = spannableString.getSpanEnd(quoteSpan);
                int flags = spannableString.getSpanFlags(quoteSpan);
                spannableString.removeSpan(quoteSpan);
                spannableString.setSpan(new ArticleQuoteSpan(this), spanStart, spanEnd, flags);
            }
            if(!preferences.s_articlesinbrowser){
                URLSpan[] linkSpans = spannableString.getSpans(0, text.length(), URLSpan.class);
                for(URLSpan linkSpan : linkSpans){
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


            textView.setText(spannableString);

            articleContainer.addView(textView);
        }
    }
    private void createUrlSpan(URLSpan span){
        
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void openWebView(String url, String titleText){
        final BottomSheetDialog webViewSheet = new BottomSheetDialog(this, R.style.BottomSheetStyle);
        webViewSheet.setContentView(R.layout.webview_dialog);
        webViewSheet.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        webViewSheet.getBehavior().setDraggable(false);

        TextView titleView = webViewSheet.findViewById(R.id.dialog_webview_title);
        titleView.setText(titleText);

        TextView cancel = webViewSheet.findViewById(R.id.dialog_webview_cancel);


        WebView webView = webViewSheet.findViewById(R.id.dialog_webview);
        webView.getSettings().setJavaScriptEnabled(true);

        cancel.setOnClickListener(view -> {
            webViewSheet.cancel();
            webView.destroy();
        } );
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                try{
                    URL host = new URL(url);
                    titleView.setText(host.getHost());
                }
                catch (Exception e){
                    titleView.setText(view.getTitle());
                }

            }
        });
        webView.loadUrl(url);

        webViewSheet.show();
    }
    private void ReadabilityThread(URL url, WebCallBack<String> callback){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Readability readability = new Readability(url, 100000);

                    readability.init(false);
                    title = readability.separateTitle();

                    callback.onResult(readability.outerHtml());
                }
                catch (Exception e){
                    if(e.getClass() == java.net.UnknownHostException.class){
                        callback.onResult("408");
                        e.printStackTrace();
                    }
                    else{
                        callback.onResult("404");
                        e.printStackTrace();
                    }

                }
            }
        });
        thread.start();
    }
}