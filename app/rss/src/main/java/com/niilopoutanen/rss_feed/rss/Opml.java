package com.niilopoutanen.rss_feed.rss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.ActivityResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Opml {
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String TEMPLATE =
            "<opml version=\"1.0\">\n" +
                "  <head>\n" +
                    "    <title>RSS-Feed subscriptions</title>\n" +
                "  </head>\n" +
                "  <body>\n" +
                    "%s\n" +
                "  </body>\n" +
            "</opml>";

    public static String encode(List<Source> sources) {
        StringBuilder content = new StringBuilder();

        sources.forEach(source ->
                content.append(String.format("    <outline text=\"%s\" type=\"rss\" xmlUrl=\"%s\" description=\"%s\" />\n",
                        source.title, source.url, source.description)));

        return String.format("%s\n%s", XML_HEADER, String.format(TEMPLATE, content));
    }

    public static List<Source> decode(String content) {
        if(!isOpml(content)) return null;
        List<Source> sources = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(content.getBytes()));

            NodeList outlineNodes = document.getElementsByTagName("outline");

            for (int i = 0; i < outlineNodes.getLength(); i++) {
                Element outlineElement = (Element) outlineNodes.item(i);
                String title = outlineElement.getAttribute("text");
                String url = outlineElement.getAttribute("xmlUrl");
                String description = outlineElement.getAttribute("description");

                Source source = new Source();
                source.title = title;
                source.url = url;
                source.description = description;
                if(source.url != null && !source.url.isEmpty()){
                    sources.add(source);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sources;
    }

    public static boolean isOpml(String content) {
        if(!content.contains("<opml")){
            return false;
        }
        else if(!content.contains("<body")){
            return false;
        }
        else if(!content.contains("outline")){
            return false;
        }

        return true;
    }


    public static File cacheFile(String filename, String content, Context context) {
        try {
            String fileName =  filename + ".opml";

            File tempFile = new File(context.getCacheDir(), fileName);

            FileOutputStream fos = new FileOutputStream(tempFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(content);
            osw.close();

            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static List<Source> loadData(ActivityResult result, Context context) throws IOException {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if(data == null || data.getData() == null){
                throw new InvalidObjectException("No data");
            }
            Uri uri = data.getData();
            if(uri.getPath() == null){
                throw new InvalidObjectException("No path for data");
            }

            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(context.getContentResolver().openInputStream(uri)));

            String line = "";
            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }
            reader.close();

            String content = builder.toString();

            return decode(content);
        }
        return null;
    }
}
