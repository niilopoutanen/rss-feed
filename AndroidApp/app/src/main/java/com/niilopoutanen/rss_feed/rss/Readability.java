/*

             Apache License
       Version 2.0, January 2004
    http://www.apache.org/licenses/

Readability.Java is licened by David Wu ('https://github.com/wuman') under the Apache License 2.0
A permissive license whose main conditions require preservation of copyright and license notices.
Contributors provide an express grant of patent rights.
Licensed works, modifications, and larger works may be distributed under different terms and without source code.
The code is modified, although all credit goes to him.

*/
package com.niilopoutanen.rss_feed.rss;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Readability {
    private static final String CONTENT_SCORE = "readabilityContentScore";
    private final Document mDocument;
    private String mBodyCache;
    public Readability(URL url, int timeoutMillis) throws IOException {
        super();
        mDocument = Jsoup.parse(url, timeoutMillis);
    }
    public void init(boolean preserveUnlikelyCandidates) {
        if (mDocument.body() != null && mBodyCache == null) {
            mBodyCache = mDocument.body().html();
        }
        prepDocument();
        
        Element overlay = mDocument.createElement("div");
        Element innerDiv = mDocument.createElement("div");
        Element articleTitle = getArticleTitle();
        Element articleContent = grabArticle(preserveUnlikelyCandidates);
        
        if (isEmpty(getInnerText(articleContent, false))) {
            if (!preserveUnlikelyCandidates) {
                mDocument.body().html(mBodyCache);
                init(true);
                return;
            } else {
                articleContent
                        .html("<p>Sorry, readability was unable to parse this page for content.</p>");
            }
        }
        
        innerDiv.appendChild(articleTitle);
        innerDiv.appendChild(articleContent);
        overlay.appendChild(innerDiv);
        
        mDocument.body().html("");
        mDocument.body().prependChild(overlay);
    }

    public final String html() {
        return mDocument.html();
    }
    
    public final String outerHtml() {
        return mDocument.outerHtml();
    }
    public String separateTitle(){
        String title = mDocument.title();
        Element h1 = mDocument.select("h1").first();
        if (h1 != null) {
            h1.remove();
        }
        Element docTitle = mDocument.select("title").first();
        if (docTitle != null) {
            docTitle.remove();
        }
        
        Elements images = mDocument.select("img");
        for (Element image : images) {
            image.removeAttr("alt");
        }
        return  title;
    }
    
    protected Element getArticleTitle() {
        Element articleTitle = mDocument.createElement("h1");
        articleTitle.html(mDocument.title());
        return articleTitle;
    }
    
    protected void prepDocument() {
        
        if (mDocument.body() == null) {
            mDocument.appendElement("body");
        }
        
        Elements elementsToRemove = mDocument.getElementsByTag("script");
        for (Element script : elementsToRemove) {
            script.remove();
        }
        
        elementsToRemove = getElementsByTag(mDocument.head(), "link");
        for (Element styleSheet : elementsToRemove) {
            if ("stylesheet".equalsIgnoreCase(styleSheet.attr("rel"))) {
                styleSheet.remove();
            }
        }
        
        elementsToRemove = mDocument.getElementsByTag("style");
        for (Element styleTag : elementsToRemove) {
            styleTag.remove();
        }
        
        
        mDocument.body().html(
                mDocument.body().html()
                        .replaceAll(Patterns.REGEX_REPLACE_BRS, "</p><p>")
                        .replaceAll(Patterns.REGEX_REPLACE_FONTS, "<$1span>"));
    }
    
    private void prepArticle(Element articleContent) {
        cleanStyles(articleContent);
        killBreaks(articleContent);
        
        clean(articleContent, "form");
        clean(articleContent, "object");
        clean(articleContent, "h1");
        
        if (getElementsByTag(articleContent, "h2").size() == 1) {
            clean(articleContent, "h2");
        }
        clean(articleContent, "iframe");
        cleanHeaders(articleContent);
        
        cleanConditionally(articleContent, "table");
        cleanConditionally(articleContent, "ul");
        cleanConditionally(articleContent, "div");
        
        Elements articleParagraphs = getElementsByTag(articleContent, "p");
        for (Element articleParagraph : articleParagraphs) {
            int imgCount = getElementsByTag(articleParagraph, "img").size();
            int embedCount = getElementsByTag(articleParagraph, "embed").size();
            int objectCount = getElementsByTag(articleParagraph, "object")
                    .size();
            if (imgCount == 0 && embedCount == 0 && objectCount == 0
                    && isEmpty(getInnerText(articleParagraph, false))) {
                articleParagraph.remove();
            }
        }
        try {
            articleContent.html(articleContent.html().replaceAll(
                    "(?i)<br[^>]*>\\s*<p", "<p"));
        } catch (Exception e) {
            dbg("Cleaning innerHTML of breaks failed. This is an IE strict-block-elements bug. Ignoring.",
                    e);
        }
    }
    
    private static void initializeNode(Element node) {
        node.attr(CONTENT_SCORE, Integer.toString(0));
        String tagName = node.tagName();
        if ("div".equalsIgnoreCase(tagName)) {
            incrementContentScore(node, 5);
        } else if ("pre".equalsIgnoreCase(tagName)
                || "td".equalsIgnoreCase(tagName)
                || "blockquote".equalsIgnoreCase(tagName)) {
            incrementContentScore(node, 3);
        } else if ("address".equalsIgnoreCase(tagName)
                || "ol".equalsIgnoreCase(tagName)
                || "ul".equalsIgnoreCase(tagName)
                || "dl".equalsIgnoreCase(tagName)
                || "dd".equalsIgnoreCase(tagName)
                || "dt".equalsIgnoreCase(tagName)
                || "li".equalsIgnoreCase(tagName)
                || "form".equalsIgnoreCase(tagName)) {
            incrementContentScore(node, -3);
        } else if ("h1".equalsIgnoreCase(tagName)
                || "h2".equalsIgnoreCase(tagName)
                || "h3".equalsIgnoreCase(tagName)
                || "h4".equalsIgnoreCase(tagName)
                || "h5".equalsIgnoreCase(tagName)
                || "h6".equalsIgnoreCase(tagName)
                || "th".equalsIgnoreCase(tagName)) {
            incrementContentScore(node, -5);
        }
        incrementContentScore(node, getClassWeight(node));
    }
    
    protected Element grabArticle(boolean preserveUnlikelyCandidates) {
        
        for (Element node : mDocument.getAllElements()) {
            
            if (!preserveUnlikelyCandidates) {
                String unlikelyMatchString = node.className() + node.id();
                Matcher unlikelyCandidatesMatcher = Patterns.get(
                        Patterns.RegEx.UNLIKELY_CANDIDATES).matcher(
                        unlikelyMatchString);
                Matcher maybeCandidateMatcher = Patterns.get(
                        Patterns.RegEx.OK_MAYBE_ITS_A_CANDIDATE).matcher(
                        unlikelyMatchString);
                if (unlikelyCandidatesMatcher.find()
                        && maybeCandidateMatcher.find()
                        && !"body".equalsIgnoreCase(node.tagName())) {
                    node.remove();
                    dbg("Removing unlikely candidate - " + unlikelyMatchString);
                    continue;
                }
            }
            
            if ("div".equalsIgnoreCase(node.tagName())) {
                Matcher matcher = Patterns
                        .get(Patterns.RegEx.DIV_TO_P_ELEMENTS).matcher(
                                node.html());
                if (!matcher.find()) {
                    dbg("Alternating div to p: " + node);
                    try {
                        node.tagName("p");
                    } catch (Exception e) {
                        dbg("Could not alter div to p, probably an IE restriction, reverting back to div.",
                                e);
                    }
                }
            }
        }
        
        Elements allParagraphs = mDocument.getElementsByTag("p");
        ArrayList<Element> candidates = new ArrayList<Element>();
        for (Element node : allParagraphs) {
            Element parentNode = node.parent();
            Element grandParentNode = parentNode.parent();
            String innerText = getInnerText(node, true);
            
            if (innerText.length() < 25) {
                continue;
            }
            
            if (!parentNode.hasAttr("readabilityContentScore")) {
                initializeNode(parentNode);
                candidates.add(parentNode);
            }
            
            if (!grandParentNode.hasAttr("readabilityContentScore")) {
                initializeNode(grandParentNode);
                candidates.add(grandParentNode);
            }
            int contentScore = 0;
            
            contentScore++;
            
            contentScore += innerText.split(",").length;
            
            contentScore += Math.min(Math.floor(innerText.length() / 100), 3);
            
            incrementContentScore(parentNode, contentScore);
            incrementContentScore(grandParentNode, contentScore / 2);
        }
        
        Element topCandidate = null;
        for (Element candidate : candidates) {
            
            scaleContentScore(candidate, 1 - getLinkDensity(candidate));
            dbg("Candidate: (" + candidate.className() + ":" + candidate.id()
                    + ") with score " + getContentScore(candidate));
            if (topCandidate == null
                    || getContentScore(candidate) > getContentScore(topCandidate)) {
                topCandidate = candidate;
            }
        }
        
        if (topCandidate == null
                || "body".equalsIgnoreCase(topCandidate.tagName())) {
            topCandidate = mDocument.createElement("div");
            topCandidate.html(mDocument.body().html());
            mDocument.body().html("");
            mDocument.body().appendChild(topCandidate);
            initializeNode(topCandidate);
        }
        
        Element articleContent = mDocument.createElement("div");
        articleContent.attr("id", "readability-content");
        int siblingScoreThreshold = Math.max(10,
                (int) (getContentScore(topCandidate) * 0.2f));
        Elements siblingNodes = topCandidate.parent().children();
        for (Element siblingNode : siblingNodes) {
            boolean append = false;
            dbg("Looking at sibling node: (" + siblingNode.className() + ":"
                    + siblingNode.id() + ")" + " with score "
                    + getContentScore(siblingNode));
            if (siblingNode == topCandidate) {
                append = true;
            }
            if (getContentScore(siblingNode) >= siblingScoreThreshold) {
                append = true;
            }
            if ("p".equalsIgnoreCase(siblingNode.tagName())) {
                float linkDensity = getLinkDensity(siblingNode);
                String nodeContent = getInnerText(siblingNode, true);
                int nodeLength = nodeContent.length();
                if (nodeLength > 80 && linkDensity < 0.25f) {
                    append = true;
                } else if (nodeLength < 80 && linkDensity == 0.0f
                        && nodeContent.matches(".*\\.( |$).*")) {
                    append = true;
                }
            }
            if (append) {
                dbg("Appending node: " + siblingNode);
                
                articleContent.appendChild(siblingNode);
                continue;
            }
        }
        
        prepArticle(articleContent);
        return articleContent;
    }
    
    private static String getInnerText(Element e, boolean normalizeSpaces) {
        String textContent = e.text().trim();
        if (normalizeSpaces) {
            textContent = textContent.replaceAll(Patterns.REGEX_NORMALIZE, "");
        }
        return textContent;
    }
    
    private static int getCharCount(Element e, String s) {
        if (s == null || s.length() == 0) {
            s = ",";
        }
        return getInnerText(e, true).split(s).length;
    }
    
    private static void cleanStyles(Element e) {
        if (e == null) {
            return;
        }
        Element cur = e.children().first();
        
        if (!"readability-styled".equals(e.className())) {
            e.removeAttr("style");
        }
        
        while (cur != null) {
            
            if (!"readability-styled".equals(cur.className())) {
                cur.removeAttr("style");
            }
            cleanStyles(cur);
            cur = cur.nextElementSibling();
        }
    }
    
    private static float getLinkDensity(Element e) {
        Elements links = getElementsByTag(e, "a");
        int textLength = getInnerText(e, true).length();
        float linkLength = 0.0F;
        for (Element link : links) {
            linkLength += getInnerText(link, true).length();
        }
        return linkLength / textLength;
    }
    
    private static int getClassWeight(Element e) {
        int weight = 0;
        
        String className = e.className();
        if (!isEmpty(className)) {
            Matcher negativeMatcher = Patterns.get(Patterns.RegEx.NEGATIVE)
                    .matcher(className);
            Matcher positiveMatcher = Patterns.get(Patterns.RegEx.POSITIVE)
                    .matcher(className);
            if (negativeMatcher.find()) {
                weight -= 25;
            }
            if (positiveMatcher.find()) {
                weight += 25;
            }
        }
        
        String id = e.id();
        if (!isEmpty(id)) {
            Matcher negativeMatcher = Patterns.get(Patterns.RegEx.NEGATIVE)
                    .matcher(id);
            Matcher positiveMatcher = Patterns.get(Patterns.RegEx.POSITIVE)
                    .matcher(id);
            if (negativeMatcher.find()) {
                weight -= 25;
            }
            if (positiveMatcher.find()) {
                weight += 25;
            }
        }
        return weight;
    }
    
    private static void killBreaks(Element e) {
        e.html(e.html().replaceAll(Patterns.REGEX_KILL_BREAKS, "<br />"));
    }
    
    private static void clean(Element e, String tag) {
        Elements targetList = getElementsByTag(e, tag);
        boolean isEmbed = "object".equalsIgnoreCase(tag)
                || "embed".equalsIgnoreCase(tag)
                || "iframe".equalsIgnoreCase(tag);
        for (Element target : targetList) {
            Matcher matcher = Patterns.get(Patterns.RegEx.VIDEO).matcher(
                    target.outerHtml());
            if (isEmbed && matcher.find()) {
                continue;
            }
            target.remove();
        }
    }
    
    private void cleanConditionally(Element e, String tag) {
        Elements tagsList = getElementsByTag(e, tag);
        
        for (Element node : tagsList) {
            int weight = getClassWeight(node);
            dbg("Cleaning Conditionally (" + node.className() + ":" + node.id()
                    + ")" + getContentScore(node));
            if (weight < 0) {
                node.remove();
            } else if (getCharCount(node, ",") < 10) {
                
                int p = getElementsByTag(node, "p").size();
                int img = getElementsByTag(node, "img").size();
                int li = getElementsByTag(node, "li").size() - 100;
                int input = getElementsByTag(node, "input").size();
                int embedCount = 0;
                Elements embeds = getElementsByTag(node, "embed");
                for (Element embed : embeds) {
                    if (!Patterns.get(Patterns.RegEx.VIDEO)
                            .matcher(embed.absUrl("src")).find()) {
                        embedCount++;
                    }
                }
                float linkDensity = getLinkDensity(node);
                int contentLength = getInnerText(node, true).length();
                boolean toRemove = false;
                if (img > p) {
                    toRemove = true;
                } else if (li > p && !"ul".equalsIgnoreCase(tag)
                        && !"ol".equalsIgnoreCase(tag)) {
                    toRemove = true;
                } else if (input > Math.floor(p / 3)) {
                    toRemove = true;
                } else if (contentLength < 25 && (img == 0 || img > 2)) {
                    toRemove = true;
                } else if (weight < 25 && linkDensity > 0.2f) {
                    toRemove = true;
                } else if (weight > 25 && linkDensity > 0.5f) {
                    toRemove = true;
                } else if ((embedCount == 1 && contentLength < 75)
                        || embedCount > 1) {
                    toRemove = true;
                }
                if (toRemove) {
                    node.remove();
                }
            }
        }
    }
    
    private static void cleanHeaders(Element e) {
        for (int headerIndex = 1; headerIndex < 7; headerIndex++) {
            Elements headers = getElementsByTag(e, "h" + headerIndex);
            for (Element header : headers) {
                if (getClassWeight(header) < 0
                        || getLinkDensity(header) > 0.33f) {
                    header.remove();
                }
            }
        }
    }
    
    protected void dbg(String msg) {
        dbg(msg, null);
    }
    
    protected void dbg(String msg, Throwable t) {
        System.out.println(msg + (t != null ? ("\n" + t.getMessage()) : "")
                + (t != null ? ("\n" + t.getStackTrace()) : ""));
    }
    private static class Patterns {
        private static Pattern sUnlikelyCandidatesRe;
        private static Pattern sOkMaybeItsACandidateRe;
        private static Pattern sPositiveRe;
        private static Pattern sNegativeRe;
        private static Pattern sDivToPElementsRe;
        private static Pattern sVideoRe;
        private static final String REGEX_REPLACE_BRS = "(?i)(<br[^>]*>[ \n\r\t]*){2,}";
        private static final String REGEX_REPLACE_FONTS = "(?i)<(\\/?)font[^>]*>";
        
        
        private static final String REGEX_NORMALIZE = "\\s{2,}";
        private static final String REGEX_KILL_BREAKS = "(<br\\s*\\/?>(\\s|&nbsp;?)*){1,}";
        public enum RegEx {
            UNLIKELY_CANDIDATES, OK_MAYBE_ITS_A_CANDIDATE, POSITIVE, NEGATIVE, DIV_TO_P_ELEMENTS, VIDEO
        }
        public static Pattern get(RegEx re) {
            switch (re) {
                case UNLIKELY_CANDIDATES: {
                    if (sUnlikelyCandidatesRe == null) {
                        sUnlikelyCandidatesRe = Pattern
                                .compile(
                                        "combx|comment|disqus|foot|header|menu|meta|nav|rss|shoutbox|sidebar|sponsor",
                                        Pattern.CASE_INSENSITIVE);
                    }
                    return sUnlikelyCandidatesRe;
                }
                case OK_MAYBE_ITS_A_CANDIDATE: {
                    if (sOkMaybeItsACandidateRe == null) {
                        sOkMaybeItsACandidateRe = Pattern.compile(
                                "and|article|body|column|main",
                                Pattern.CASE_INSENSITIVE);
                    }
                    return sOkMaybeItsACandidateRe;
                }
                case POSITIVE: {
                    if (sPositiveRe == null) {
                        sPositiveRe = Pattern
                                .compile(
                                        "article|body|content|entry|hentry|page|pagination|post|text",
                                        Pattern.CASE_INSENSITIVE);
                    }
                    return sPositiveRe;
                }
                case NEGATIVE: {
                    if (sNegativeRe == null) {
                        sNegativeRe = Pattern
                                .compile(
                                        "combx|comment|contact|foot|footer|footnote|link|media|meta|promo|related|scroll|shoutbox|sponsor|tags|widget",
                                        Pattern.CASE_INSENSITIVE);
                    }
                    return sNegativeRe;
                }
                case DIV_TO_P_ELEMENTS: {
                    if (sDivToPElementsRe == null) {
                        sDivToPElementsRe = Pattern.compile(
                                "<(a|blockquote|dl|div|img|ol|p|pre|table|ul)",
                                Pattern.CASE_INSENSITIVE);
                    }
                    return sDivToPElementsRe;
                }
                case VIDEO: {
                    if (sVideoRe == null) {
                        sVideoRe = Pattern.compile(
                                "http:\\/\\/(www\\.)?(youtube|vimeo)\\.com",
                                Pattern.CASE_INSENSITIVE);
                    }
                    return sVideoRe;
                }
            }
            return null;
        }
    }
    
    private static int getContentScore(Element node) {
        try {
            return Integer.parseInt(node.attr(CONTENT_SCORE));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private static Element incrementContentScore(Element node, int increment) {
        int contentScore = getContentScore(node);
        contentScore += increment;
        node.attr(CONTENT_SCORE, Integer.toString(contentScore));
        return node;
    }
    
    private static Element scaleContentScore(Element node, float scale) {
        int contentScore = getContentScore(node);
        contentScore *= scale;
        node.attr(CONTENT_SCORE, Integer.toString(contentScore));
        return node;
    }
    
    private static Elements getElementsByTag(Element e, String tag) {
        Elements es = e.getElementsByTag(tag);
        es.remove(e);
        return es;
    }
    
    private static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }
}