package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.model.OrganizationSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Base64;

@Service
public class EmailTemplateLayoutService {

    /**
     * Bundled SkyCare logo for agreement emails (also served at GET /email-templates/logo).
     */
    public static final String EMAIL_LOGO_CLASSPATH = "agreement-assets/skycare_email_logo.png";

    /**
     * Public fallback when classpath inlining is unavailable.
     */
    public static final String DEFAULT_EMAIL_LOGO_URL =
            "https://raw.githubusercontent.com/VishathAmarasinghe/publicImages/main/skycarelogo.png";

    @Autowired
    private OrganizationSettingService organizationSettingService;

    @Value("${app.external.service.baseUrl:http://localhost:5000}")
    private String backendBaseUrl;

    private volatile String cachedLogoDataUrl;

    public String wrapBody(String bodyHtml) {
        OrganizationSetting org = organizationSettingService.getEntity();
        String orgName = safe(org.getName(), "SkyCare Solutions Pty Ltd");
        String orgEmail = safe(org.getEmail(), "info@skycaresolutions.com.au");
        String orgPhone = safe(org.getPhone(), "");
        String logoBlock = buildLogoBlock(org);

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                  <title>%s</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f4f6f8;font-family:Arial,Helvetica,sans-serif;">
                  <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color:#f4f6f8;padding:24px 0;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="600" cellspacing="0" cellpadding="0" style="max-width:600px;width:100%%;background-color:#ffffff;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.08);">
                          <tr>
                            <td style="background-color:#055476;padding:20px 24px;">
                              %s
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:28px 24px;color:#222222;font-size:15px;line-height:1.6;">
                              %s
                            </td>
                          </tr>
                          <tr>
                            <td style="background-color:#f5f5f5;padding:16px 24px;font-size:12px;line-height:1.5;color:#666666;">
                              <strong>%s</strong><br/>
                              %s%s
                              <p style="margin:12px 0 0;font-size:11px;color:#999;">This is an automated message from SkyCare Portal.</p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(
                orgName,
                logoBlock,
                bodyHtml != null ? bodyHtml : "",
                orgName,
                orgEmail,
                orgPhone != null && !orgPhone.isBlank() ? "<br/>" + orgPhone : ""
        );
    }

    public String resolvePublicLogoUrl() {
        String base = backendBaseUrl.endsWith("/")
                ? backendBaseUrl.substring(0, backendBaseUrl.length() - 1)
                : backendBaseUrl;
        return base + "/email-templates/logo";
    }

    private String buildLogoBlock(OrganizationSetting org) {
        String orgName = safe(org.getName(), "SkyCare Solutions");
        String logoSrc = resolveEmailLogoSrc(org);
        if (logoSrc != null && !logoSrc.isBlank()) {
            return """
                    <img src="%s" alt="%s" style="max-height:56px;max-width:220px;display:block;" />
                    """.formatted(logoSrc, orgName);
        }
        return """
                <span style="color:#ffffff;font-size:20px;font-weight:bold;">%s</span>
                """.formatted(orgName);
    }

    /**
     * Prefer an inlined data-URI so in-app previews always render the logo.
     * Fall back to the backend logo endpoint, then the public GitHub raw URL.
     */
    private String resolveEmailLogoSrc(OrganizationSetting org) {
        String dataUrl = getCachedLogoDataUrl();
        if (dataUrl != null && !dataUrl.isBlank()) {
            return dataUrl;
        }

        String backendLogo = resolvePublicLogoUrl();
        if (backendLogo != null && !backendLogo.toLowerCase().contains("localhost")
                && !backendLogo.toLowerCase().contains("127.0.0.1")) {
            return backendLogo;
        }

        if (DEFAULT_EMAIL_LOGO_URL != null && !DEFAULT_EMAIL_LOGO_URL.isBlank()) {
            return DEFAULT_EMAIL_LOGO_URL;
        }

        String orgLogo = organizationSettingService.buildLogoUrl(org);
        if (orgLogo != null && !orgLogo.isBlank()
                && !orgLogo.toLowerCase().contains("localhost")
                && !orgLogo.toLowerCase().contains("127.0.0.1")) {
            return orgLogo;
        }

        return backendLogo;
    }

    private String getCachedLogoDataUrl() {
        if (cachedLogoDataUrl != null) {
            return cachedLogoDataUrl;
        }
        synchronized (this) {
            if (cachedLogoDataUrl != null) {
                return cachedLogoDataUrl;
            }
            cachedLogoDataUrl = loadClasspathLogoDataUrl();
            return cachedLogoDataUrl;
        }
    }

    private String loadClasspathLogoDataUrl() {
        try {
            ClassPathResource resource = new ClassPathResource(EMAIL_LOGO_CLASSPATH);
            if (!resource.exists()) {
                return null;
            }
            try (InputStream input = resource.getInputStream()) {
                byte[] bytes = input.readAllBytes();
                if (bytes.length == 0) {
                    return null;
                }
                return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    private String safe(String value, String fallback) {
        return value != null && !value.isBlank() ? value.trim() : fallback;
    }
}
