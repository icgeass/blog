package com.zeroq6.blog.common.utils;

import com.zeroq6.blog.common.domain.PostDomain;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;

/**
 * Created by icgeass on 2017/8/11.
 */
public class PostUtils {


    public final static int SUBSTRING_LENGTH = 200;

    private final static Parser parser = Parser.builder().build();

    private final static HtmlRenderer renderer = HtmlRenderer.builder().build();

    public static String substring(String content) {
        return substring(content, SUBSTRING_LENGTH);
    }

    public static String substring(String content, int length) {
        if (null == content) {
            return "";
        }
        return content.length() > length ? content.substring(0, length) + "..." : content;
    }

    public static String parseMarkdownText(String markdownText) {
        if (null == markdownText) {
            return "";
        }
        return renderer.render(parser.parse(markdownText));
    }

    public static String getHtmlText(String html) {
        if (null == html) {
            return "";
        }
        return Jsoup.parse(html).text();

    }

    public static String getHtmlTextSubstring(PostDomain postDomain, int length){
        return substring(getHtmlText(parseMarkdownText(postDomain.getContent())), length);
    }

    public static String getHtmlTextSubstring(PostDomain postDomain){
        return getHtmlTextSubstring(postDomain, SUBSTRING_LENGTH);
    }
}
