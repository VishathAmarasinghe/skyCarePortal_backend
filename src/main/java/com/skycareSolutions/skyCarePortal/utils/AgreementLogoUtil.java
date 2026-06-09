package com.skycareSolutions.skyCarePortal.utils;

import com.skycareSolutions.skyCarePortal.model.AgreementTemplate;

import java.io.InputStream;
import java.util.Base64;

public final class AgreementLogoUtil {

    public static final String CLASSPATH_LOGO = "/agreement-assets/skycare_logo_ndis.png";
    public static final String DEFAULT_LOGO_PATH = "/agreements/default-logo";

    private AgreementLogoUtil() {
    }

    public static String buildLogoImgHtml(String logoUrl) {
        if (logoUrl == null || logoUrl.isBlank()) {
            return "";
        }
        return "<img src=\"" + logoUrl + "\" alt=\"Organization logo\" "
                + "style=\"max-width:180px;height:auto;display:block;margin:0 auto;\" />";
    }

    public static String buildServiceAgreementLogoHtml() {
        String dataUrl = defaultLogoDataUrl();
        if (dataUrl.isBlank()) {
            return "";
        }
        return "<img src=\"" + dataUrl + "\" alt=\"SkyCare Solutions — Registered NDIS Provider\" "
                + "style=\"max-width:480px;width:100%;height:auto;display:block;margin:0 auto;\" />";
    }

    public static boolean isClientServiceAgreementTemplate(AgreementTemplate template) {
        return template != null && "CLIENT".equalsIgnoreCase(template.getAudience());
    }

    public static String defaultLogoDataUrl() {
        try (InputStream input = AgreementLogoUtil.class.getResourceAsStream(CLASSPATH_LOGO)) {
            if (input == null) {
                return "";
            }
            byte[] bytes = input.readAllBytes();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            return "";
        }
    }
}
