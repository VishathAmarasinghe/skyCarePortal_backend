package com.skycareSolutions.skyCarePortal.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

public final class StaffTrainingCertificateFileUtil {

    public static final String DIRECTORY = Paths
            .get(System.getProperty("user.home"), "skyCareUploads", "staffTraining")
            .toString();

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".pdf", ".doc", ".docx", ".png", ".jpg", ".jpeg"
    );

    private static final long MAX_BYTES = 20L * 1024 * 1024;

    private StaffTrainingCertificateFileUtil() {
    }

    public static File ensureDirectory() {
        File directory = new File(DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    public static void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("File must be 20MB or smaller");
        }
        String original = file.getOriginalFilename();
        if (original == null || original.isBlank()) {
            throw new IllegalArgumentException("File must have a name");
        }
        String lower = original.toLowerCase();
        boolean allowed = ALLOWED_EXTENSIONS.stream().anyMatch(lower::endsWith);
        if (!allowed) {
            throw new IllegalArgumentException("Allowed types: PDF, Word, PNG, JPG");
        }
    }

    public static String storeFile(MultipartFile file) throws Exception {
        validateFile(file);
        String extension = getExtension(file.getOriginalFilename());
        String storedFileName = UUID.randomUUID() + extension;
        File destination = new File(ensureDirectory(), storedFileName);
        file.transferTo(destination);
        return destination.getAbsolutePath();
    }

    public static String guessMimeType(String filename) {
        if (filename == null) {
            return "application/octet-stream";
        }
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".doc")) return "application/msword";
        if (lower.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        return "application/octet-stream";
    }

    private static String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : "";
    }
}
