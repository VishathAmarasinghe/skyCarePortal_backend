package com.skycareSolutions.skyCarePortal.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin("*")
@RequestMapping("/file")
public class FileController {

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) {
        try {
            // Resolve the file path
            Path path = Paths.get(filePath).normalize();
            Resource resource = new UrlResource(path.toUri());

            // Check if the file exists and is readable
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Determine content type
            String contentType;
            if (filePath.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (filePath.endsWith(".jpeg") || filePath.endsWith(".jpg")) {
                contentType = "image/jpeg";
            } else if (filePath.endsWith(".png")) {
                contentType = "image/png";
            } else {
                contentType = "application/octet-stream";
            }

            // Return the file with headers
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName().toString() + "\"")
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
