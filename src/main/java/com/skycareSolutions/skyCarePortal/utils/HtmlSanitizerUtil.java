package com.skycareSolutions.skyCarePortal.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

public final class HtmlSanitizerUtil {

    private static final Safelist AGREEMENT_ALLOWLIST = Safelist.relaxed()
            .addTags("h1", "h2", "h3", "hr", "span", "div")
            .addAttributes("img", "src", "alt", "width", "height", "style", "class")
            .addAttributes("table", "style", "class", "border", "cellpadding", "cellspacing")
            .addAttributes("td", "colspan", "rowspan", "style", "class", "border")
            .addAttributes("th", "colspan", "rowspan", "style", "class", "border")
            .addAttributes("tr", "style", "class")
            .addAttributes("thead", "style", "class")
            .addAttributes("tbody", "style", "class")
            .addAttributes("p", "style", "class")
            .addAttributes("span", "style", "class")
            .addAttributes("div", "style", "class")
            .addAttributes("a", "href", "target", "rel")
            .addProtocols("img", "src", "http", "https", "data")
            .addProtocols("a", "href", "http", "https", "mailto");

    private HtmlSanitizerUtil() {
    }

    public static String sanitizeAgreementHtml(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        String cleaned = Jsoup.clean(html, AGREEMENT_ALLOWLIST);
        Document doc = Jsoup.parseBodyFragment(cleaned);
        return doc.body().html();
    }
}
