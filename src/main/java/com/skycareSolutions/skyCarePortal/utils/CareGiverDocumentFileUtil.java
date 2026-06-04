package com.skycareSolutions.skyCarePortal.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

    public static String normalizeForMatch(String name) {
        if (name == null) {
            return "";
        }
        String normalized = name.trim();
        try {
            normalized = URLDecoder.decode(normalized, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ignored) {
        }
        normalized = normalized.replace('+', ' ');
        return sanitizeFileName(normalized);
    }

    public static boolean filenamesMatch(String a, String b) {
        if (a == null || b == null || a.isBlank() || b.isBlank()) {
            return false;
        }
        if (a.equalsIgnoreCase(b)) {
            return true;
        }
        return normalizeForMatch(a).equalsIgnoreCase(normalizeForMatch(b));
    }

    public static boolean isPersistedPath(String document) {
        if (document == null || document.isBlank()) {
            return false;
        }
        return document.startsWith("/") || document.contains("careGiverDocuments");
    }

    /**
     * True when the DTO references a filename that matches one of the uploaded multipart files.
     * Blank references never imply upload intent (avoids applying one file to every document type).
     */
    public static boolean hasUploadIntent(String documentReference, List<MultipartFile> files) {
        if (documentReference == null || documentReference.isBlank() || files == null || files.isEmpty()) {
            return false;
        }
        if (isPersistedPath(documentReference)) {
            String basename = new File(documentReference).getName();
            return resolveMultipartFile(basename, files) != null;
        }
        for (MultipartFile file : files) {
            String original = effectiveOriginalFilename(file, documentReference);
            if (filenamesMatch(original, documentReference)) {
                return true;
            }
        }
        return false;
    }

    public static MultipartFile resolveMultipartFile(String referenceName, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return null;
        }
        if (referenceName == null || referenceName.isBlank()) {
            return null;
        }
        String matchReference = referenceName;
        if (isPersistedPath(referenceName)) {
            matchReference = new File(referenceName).getName();
        }
        for (MultipartFile file : files) {
            String original = file.getOriginalFilename();
            if (original != null && filenamesMatch(original, matchReference)) {
                return file;
            }
        }
        return null;
    }

    /**
     * Prefer the multipart original filename; fall back to the client-provided reference name.
     */
    public static String effectiveOriginalFilename(MultipartFile file, String referenceName) {
        if (file == null) {
            return referenceName;
        }
        String original = file.getOriginalFilename();
        if (original != null && !original.isBlank()) {
            return original.trim();
        }
        if (referenceName != null && !referenceName.isBlank() && !isPersistedPath(referenceName)) {
            return referenceName.trim();
        }
        return original;
    }

    public static boolean sameDocumentReference(String inputReference, String existingReference) {
        if (existingReference == null || existingReference.isBlank()) {
            return false;
        }
        if (inputReference == null || inputReference.isBlank()) {
            return false;
        }
        if (inputReference.equalsIgnoreCase(existingReference)) {
            return true;
        }
        if (isPersistedPath(inputReference) && isPersistedPath(existingReference)) {
            return inputReference.equalsIgnoreCase(existingReference);
        }
        return filenamesMatch(inputReference, existingReference);
    }

    public static File ensureDirectory() {
        File directory = new File(DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    public static String storeFile(
            String careGiverID,
            String documentTypeID,
            MultipartFile file,
            String referenceName
    ) throws Exception {
        String original = effectiveOriginalFilename(file, referenceName);
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
