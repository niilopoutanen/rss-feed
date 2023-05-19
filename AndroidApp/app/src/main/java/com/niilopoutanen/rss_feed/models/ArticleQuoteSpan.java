package com.niilopoutanen.rss_feed.models;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.style.QuoteSpan;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

public class ArticleQuoteSpan extends QuoteSpan {

    private final @ColorInt int quoteColor;
    private final int quoteStripeWidth;
    private final int quoteGapWidth;
    private final Context appContext;

    public ArticleQuoteSpan(Context context) {
        super(); // Use the default values for QuoteSpan
        this.appContext = context;
        TypedValue typedValue = new TypedValue();
        appContext.getTheme().resolveAttribute(com.google.android.material.R.attr.colorAccent, typedValue, true);
        quoteColor = typedValue.data;
        quoteStripeWidth = PreferencesManager.dpToPx(3, context);
        quoteGapWidth = PreferencesManager.dpToPx(15, context);
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        Rect rect = new Rect();
        p.getTextBounds(text.toString(), start, end, rect);

        // Set the quote line color
        p.setColor(quoteColor);

        // Draw the quote line
        c.drawRect(x, top, x + quoteStripeWidth, bottom, p);

        // Set the original color for the text
        p.setColor(ContextCompat.getColor(appContext, R.color.textPrimary));

        // Adjust the position of the text to the right of the quote line
        c.drawText(text, start, end, x + quoteStripeWidth + quoteGapWidth, baseline, p);
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return quoteStripeWidth + quoteGapWidth;
    }
}
