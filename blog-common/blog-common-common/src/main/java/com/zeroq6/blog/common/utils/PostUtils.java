package com.zeroq6.blog.common.utils;

import com.zeroq6.blog.common.domain.PostDomain;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by icgeass on 2017/8/11.
 */
public class PostUtils {


    public final static int SUBSTRING_LENGTH = 200;

    private final static Parser parser = Parser.builder().build();

    private final static HtmlRenderer renderer = HtmlRenderer.builder().build();

    private final static String CLIPPED = "...";

    public static String substring(String content) {
        return substring(content, SUBSTRING_LENGTH);
    }

    public static String substring(String content, int length) {
        if (null == content) {
            return "";
        }
        return content.length() > length ? content.substring(0, length) + CLIPPED : content;
    }

    public static String parseMarkdownText(String markdownText) {
        if (null == markdownText) {
            return "";
        }
        return urlToLink(renderer.render(parser.parse(markdownText)));
    }

    public static String getHtmlText(String html) {
        if (null == html) {
            return "";
        }
        return Jsoup.parse(html).text();

    }

    public static String getHtmlTextSubstring(PostDomain postDomain, int length) {
        String result = substring(getHtmlText(parseMarkdownText(postDomain.getContent())), length);
        int index = result.indexOf("<");
        if (index != -1) {
            result = result.substring(0, index) + CLIPPED;
        }
        return result;
    }

    public static String getHtmlTextSubstring(PostDomain postDomain){
        return getHtmlTextSubstring(postDomain, SUBSTRING_LENGTH);
    }


    /**
     * 版权声明：本文为CSDN博主「nick_gu」的原创文章，遵循CC 4.0 by-sa版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/waww116529/article/details/46045833
     * <p>
     * http://urlregex.com/
     *
     * @param urlText
     * @return
     */
    private static String urlToLink(String urlText) {
        // url的正则表达式
        String regexp = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(urlText);

        StringBuilder resultText = new StringBuilder();// （临时变量，保存转换后的文本）
        int lastEnd = 0;// 保存每个链接最后一会的下标

        while (matcher.find()) {
            resultText.append(urlText.substring(lastEnd, matcher.start() - 1));
            resultText.append("<a href=\"" + matcher.group() + "\" target=\"_blank\">" + matcher.group() + "</a>");
            lastEnd = matcher.end();
        }
        resultText.append(urlText.substring(lastEnd));
        return resultText.toString();

    }
}
