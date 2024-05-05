package com.niilopoutanen.rss_feed.fragments.components;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.niilopoutanen.rss_feed.activities.ImageViewActivity;
import com.niilopoutanen.rss_feed.common.PreferencesManager;
import com.niilopoutanen.rss_feed.resources.R;
import com.niilopoutanen.rss_feed.common.models.Preferences;
import com.niilopoutanen.rss_feed.rss.Post;
import com.niilopoutanen.rss_feed.rss.Source;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Locale;

public class ArticleView extends WebView {
    private final Context context;

    public ArticleView(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ArticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }


    private void init() {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        setWebContentsDebuggingEnabled(true);
        addJavascriptInterface(this, "Android");
    }

    public void loadDocument(Document document, Post post) {
        if (PreferencesManager.loadPreferences(context).s_article_show_categories && !post.getCategories().isEmpty()) {
            Element container = new Element("div");
            container.id("rssfeed-categories");

            for (String category : post.getCategories()) {
                container.appendChild(createCategory(category));
            }
            Elements titles = document.select("h1");
            if (!titles.isEmpty()) {
                Element title = titles.first();
                if (title != null) {
                    title.after(createSection(null, container));

                    Element author = new Element("p");
                    author.text(post.author);
                    author.addClass("author");
                    title.after(author);
                }
            } else {
                document.prependChild(createSection(null, container));
            }
        }

        Element head = document.head();
        head.append(getCSS());

        loadDocument(document);
    }

    private Element createCategory(String name){
        Element category = new Element("div");
        category.addClass("category");

        Element text = new Element("p");
        text.text(name);

        category.appendChild(text);

        return category;
    }

    private String getCSS() {
        Preferences preferences = PreferencesManager.loadPreferences(context);
        String css =
                  "<style>\n" +
                            "    @font-face {\n" +
                            "        font-family: \"CustomFont\";\n" +
                            "        src: url(\"'$FONTFACE'\");\n" +
                            "        font-weight: normal;\n" +
                            "    }\n" +
                            "\n" +
                            "    @font-face {\n" +
                            "        font-family: \"CustomFont\";\n" +
                            "        src: url(\"'$BOLDFONTFACE'\");\n" +
                            "        font-weight: bold;\n" +
                            "    }\n" +
                            "\n" +
                            "    html,\n" +
                            "    body {\n" +
                            "        width: 100%;\n" +
                            "        margin: 0;\n" +
                            "        box-sizing: border-box;\n" +
                            "        color: '$TEXTCOLOR';\n" +
                            "        background-color: '$BACKGROUNDCOLOR';\n" +
                            "        font-family: \"CustomFont\";\n" +
                            "        font-size: '$FONTSIZE';\n" +
                            "        max-width: 100vw;\n" +
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
                            "    th,\n" +
                            "    td {\n" +
                            "        border: 2px solid '$TEXTSECONDARY';\n" +
                            "    }\n" +
                            "\n" +
                            "    h1{\n" +
                            "        margin-bottom: 0px;\n" +
                            "        font-size: 1.6rem;\n" +
                            "    }\n" +
                            "    p.author{\n" +
                            "        margin-top: 3px;\n" +
                            "        margin-bottom: 15px;\n" +
                            "        font-size: 1rem;\n" +
                            "        color: '$TEXTSECONDARY';\n" +
                            "    }\n" +
                            "\n" +
                            "    table {\n" +
                            "        border-collapse: collapse;\n" +
                            "        overflow: scroll;\n" +
                            "    }\n" +
                            "\n" +
                            "    iframe {\n" +
                            "        width: 100%;\n" +
                            "        border-radius: 10px;\n" +
                            "        border: none;\n" +
                            "    }\n" +
                            "\n" +
                            "    img {\n" +
                            "        max-width: 100%;\n" +
                            "        height: auto;\n" +
                            "        border-radius: 15px;\n" +
                            "        margin-bottom: 5px;\n" +
                            "        transition: transform 0.3s ease;\n" +
                            "    }\n" +
                            "    img:active{\n" +
                            "        transform: scale(0.95);\n" +
                            "    }\n" +
                            "\n" +
                            "    ul {\n" +
                            "        padding-left: 20px;\n" +
                            "    }\n" +
                            "\n" +
                            "    figure {\n" +
                            "        margin: 0;\n" +
                            "        padding: 0;\n" +
                            "    }\n" +
                            "\n" +
                            "    svg{\n" +
                            "        display: none;\n" +
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
                            "        background-color: '$ACCENTCOLOR';\n" +
                            "        border-radius: 10px;\n" +
                            "    }\n" +
                            "\n" +
                            "    #rssfeed-categories{\n" +
                            "        display: flex;\n" +
                            "        flex-direction: row;\n" +
                            "        gap: 5px;\n" +
                            "        max-width: 100%;\n" +
                            "        overflow-x: scroll;\n" +
                            "        margin-bottom: 5px;\n" +
                            "    }\n" +
                            "    #rssfeed-categories .category{\n" +
                            "        background-color: '$ELEMENTBACKGROUND';\n" +
                            "        border-radius: 100px;\n" +
                            "        padding: 5px 15px;\n" +
                            "        border: 1px solid '$ELEMENTBORDER';\n" +
                            "        font-size: 0.8rem;\n" +
                            "        white-space: nowrap;\n" +
                            "        color: '$TEXTSECONDARY';\n" +
                            "    }\n" +
                            "    #rssfeed-categories p{\n" +
                            "        margin: 0;\n" +
                            "    }\n" +
                            "    .rssfeed_section{\n" +
                            "        display: flex;\n" +
                            "        flex-direction: column;\n" +
                            "    }\n" +
                            "    .rssfeed-section p{\n" +
                            "        margin: 0;\n" +
                            "        font-size: 12px;\n" +
                            "        margin-bottom: 5px;\n" +
                            "    }\n" +
                            "</style>";

        String accentColor = formatColor(PreferencesManager.getAccentColor(context));
        String backgroundColor = formatColor(context.getColor(R.color.windowBg));
        String elementBackground = formatColor(context.getColor(R.color.element));
        String elementBorder = formatColor(context.getColor(R.color.element_border));
        String textColor = formatColor(context.getColor(R.color.textPrimary));
        String textSecondary = formatColor(context.getColor(R.color.textSecondary));
        String fontSize = String.valueOf(preferences.s_fontsize);

        String fontFace = "file:///android_res";
        String boldFontFace = "file:///android_res";

        switch (preferences.s_font) {
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
        css = css.replace("'$ELEMENTBACKGROUND'", elementBackground);
        css = css.replace("'$ELEMENTBORDER'", elementBorder);

        return css;
    }

    private static String formatColor(int colorID) {
        int red = (colorID >> 16) & 0xFF;
        int green = (colorID >> 8) & 0xFF;
        int blue = colorID & 0xFF;

        return String.format(Locale.US, "rgb(%d, %d, %d)", red, green, blue);
    }

    private static Element createSection(String title, Element child) {
        Element section = new Element("div");
        section.addClass("rssfeed-section");

        if (title != null) {
            Element header = new Element("p");
            header.append(title);
            section.appendChild(header);
        }

        section.appendChild(child);

        return section;
    }

    public void loadDocument(Document document) {
        Elements images = document.select("img");
        for (Element image : images) {
            image.attr("onclick", "event.preventDefault(); Android.onImageClick(this.src);");
            image.attr("onerror", "this.style.display='none'");
        }
        Elements iframes = document.select("iframe");
        for(Element iframe : iframes){
            if(iframe.hasAttr("data-src")){
                String data = iframe.attr("data-src");
                iframe.attr("src", data);
                iframe.removeAttr("data-src");
            }
        }

        super.loadDataWithBaseURL(null, document.html(), "text/html", "charset=utf-8", "");
    }

    public void setInsets(int top, int bottom) {
        String javascript = "javascript:(function() { " +
                  "document.body.style.paddingTop = '" + top + "px';" +
                  "document.body.style.paddingBottom = '" + bottom + "px';" +
                  "})()";
        post(() -> loadUrl(javascript));

    }

    @JavascriptInterface
    public void onImageClick(String imageUrl) {
        Intent imageIntent = new Intent(context, ImageViewActivity.class);
        imageIntent.putExtra("imageurl", imageUrl);
        context.startActivity(imageIntent);
    }
}
