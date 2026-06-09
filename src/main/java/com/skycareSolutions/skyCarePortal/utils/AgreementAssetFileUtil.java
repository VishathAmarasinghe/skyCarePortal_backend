package com.skycareSolutions.skyCarePortal.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

public final class AgreementAssetFileUtil {

    public static final String DIRECTORY = Paths
            .get(System.getProperty("user.home"), "skyCareUploads", "agreements", "assets")
            .toString();

    public static final String ORG_LOGO_DIRECTORY = Paths
            .get(System.getProperty("user.home"), "skyCareUploads", "agreements", "org")
            .toString();

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".png", ".jpg", ".jpeg", ".gif", ".webp", ".svg");

    private AgreementAssetFileUtil() {
    }

    public static File ensureDirectory() {
        File directory = new File(DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    public static File ensureOrgLogoDirectory() {
        File directory = new File(ORG_LOGO_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    public static void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Image must be 5MB or smaller");
        }
        String original = file.getOriginalFilename();
        if (original == null || original.isBlank()) {
            throw new IllegalArgumentException("File must have a name");
        }
        String lower = original.toLowerCase();
        boolean allowed = ALLOWED_EXTENSIONS.stream().anyMatch(lower::endsWith);
        if (!allowed) {
            throw new IllegalArgumentException("Only PNG, JPG, GIF, WEBP, and SVG images are allowed");
        }
    }

    public static String storeAsset(MultipartFile file, String templateId) throws Exception {
        validateImage(file);
        String extension = getExtension(file.getOriginalFilename());
        String storedFileName = (templateId != null ? templateId + "_" : "") + UUID.randomUUID() + extension;
        File destination = new File(ensureDirectory(), storedFileName);
        file.transferTo(destination);
        return destination.getAbsolutePath();
    }

    public static String storeOrgLogo(MultipartFile file) throws Exception {
        validateImage(file);
        String extension = getExtension(file.getOriginalFilename());
        String storedFileName = "org_logo" + extension;
        File destination = new File(ensureOrgLogoDirectory(), storedFileName);
        if (destination.exists()) {
            destination.delete();
        }
        file.transferTo(destination);
        return destination.getAbsolutePath();
    }

    private static String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0) {
            return ".png";
        }
        return filename.substring(dot).toLowerCase();
    }
}
