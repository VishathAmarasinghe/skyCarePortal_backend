package com.skycareSolutions.skyCarePortal.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public final class CareGiverDocumentFileUtil {

    public static final String DIRECTORY = Paths
            .get(System.getProperty("user.home"), "skyCareUploads", "careGiverDocuments")
            .toString();

    private CareGiverDocumentFileUtil() {
    }

    public static String sanitizeFileName(String name) {
        if (name == null) {
            return "";
        }
        String sanitized = name.trim().replaceAll("\\s+", "_");
        sanitized = sanitized.replaceAll("_+", "_");
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9._-]", "_");
        return sanitized;
    }

    public static boolean filenamesMatch(String a, String b) {
        if (a == null || b == null || a.isBlank() || b.isBlank()) {
            return false;
        }
        if (a.equalsIgnoreCase(b)) {
            return true;
        }
        return sanitizeFileName(a).equalsIgnoreCase(sanitizeFileName(b));
    }

    public static boolean isPersistedPath(String document) {
        if (document == null || document.isBlank()) {
            return false;
        }
        return document.startsWith("/") || document.contains("careGiverDocuments");
    }

    public static MultipartFile resolveMultipartFile(String referenceName, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return null;
        }
        if (referenceName != null && !referenceName.isBlank()) {
            for (MultipartFile file : files) {
                String original = file.getOriginalFilename();
                if (original != null && filenamesMatch(original, referenceName)) {
                    return file;
                }
            }
        }
        if (files.size() == 1 && (referenceName == null || referenceName.isBlank())) {
            return files.get(0);
        }
        return null;
    }

    public static File ensureDirectory() {
        File directory = new File(DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    public static String storeFile(String careGiverID, String documentTypeID, MultipartFile file) throws Exception {
        String original = file.getOriginalFilename();
        if (original == null || original.isBlank()) {
            throw new IllegalArgumentException("Uploaded file has no name");
        }
        String sanitized = sanitizeFileName(original);
        int dotIndex = sanitized.lastIndexOf('.');
        String baseName = dotIndex > 0 ? sanitized.substring(0, dotIndex) : sanitized;
        String extension = dotIndex > 0 ? sanitized.substring(dotIndex) : "";
        String storedFileName = careGiverID + "_" + documentTypeID + "_" + baseName + extension;
        File destination = new File(ensureDirectory(), storedFileName);
        file.transferTo(destination);
        return destination.getAbsolutePath();
    }

    public static void deleteFileIfExists(String documentPath) {
        if (documentPath == null || documentPath.isBlank()) {
            return;
        }
        try {
            File file = new File(documentPath);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        } catch (Exception ignored) {
        }
    }
}
