package com.skycareSolutions.skyCarePortal.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AgreementHtmlImageInliner {

    private static final Pattern IMG_SRC_PATTERN =
            Pattern.compile("(<img[^>]+src=\")([^\"]+)(\"[^>]*>)", Pattern.CASE_INSENSITIVE);

    private AgreementHtmlImageInliner() {
    }

    public static String inlineImages(String html, String backendBaseUrl) {
        if (html == null || html.isBlank()) {
            return html;
        }
        Matcher matcher = IMG_SRC_PATTERN.matcher(html);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String prefix = matcher.group(1);
            String src = matcher.group(2);
            String suffix = matcher.group(3);
            String inlined = inlineSingle(src, backendBaseUrl);
            matcher.appendReplacement(result, Matcher.quoteReplacement(prefix + inlined + suffix));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String inlineSingle(String src, String backendBaseUrl) {
        if (src == null || src.isBlank() || src.startsWith("data:")) {
            return src;
        }
        try {
            if (src.startsWith("http://") || src.startsWith("https://")) {
                if (backendBaseUrl != null && src.startsWith(backendBaseUrl)) {
                    String path = extractLocalPath(src, backendBaseUrl);
                    if (path != null) {
                        return readFileAsDataUrl(path);
                    }
                }
                java.net.URL url = new java.net.URL(src);
                byte[] bytes = url.openStream().readAllBytes();
                String mime = src.toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";
                return "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(bytes);
            }
            if (src.startsWith("/") || src.contains("skyCareUploads")) {
                return readFileAsDataUrl(src);
            }
        } catch (Exception ignored) {
        }
        return src;
    }

    private static String extractLocalPath(String url, String backendBaseUrl) {
        if (url.contains("/agreement-templates/assets/")) {
            return null;
        }
        if (url.contains("/settings/organization/logo")) {
            return null;
        }
        return null;
    }

    private static String readFileAsDataUrl(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            return path;
        }
        byte[] bytes = Files.readAllBytes(file.toPath());
        String name = file.getName().toLowerCase();
        String mime = name.endsWith(".png") ? "image/png"
                : name.endsWith(".svg") ? "image/svg+xml"
                : "image/jpeg";
        return "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(bytes);
    }

    public static String fileToDataUrl(String filePath) {
        try {
            return readFileAsDataUrl(filePath);
        } catch (Exception e) {
            return "";
        }
    }

    public static final String SIGNATURES_DIR = Paths
            .get(System.getProperty("user.home"), "skyCareUploads", "agreements", "signatures")
            .toString();

    public static final String INSTANCES_DIR = Paths
            .get(System.getProperty("user.home"), "skyCareUploads", "agreements", "instances")
            .toString();

    public static File ensureSignaturesDir() {
        File dir = new File(SIGNATURES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File ensureInstanceDir(String instanceId) {
        File dir = new File(INSTANCES_DIR, instanceId);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
