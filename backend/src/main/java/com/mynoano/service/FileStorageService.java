package com.mynoano.service;

import com.mynoano.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path dir;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) throws IOException {
        this.dir = Path.of(uploadDir).toAbsolutePath();
        Files.createDirectories(dir);
    }

    /** Saves an image and returns its public URL path. */
    public String store(MultipartFile file) {
        String ct = file.getContentType();
        if (ct == null || !ct.startsWith("image/")) throw ApiException.bad("Only image files are allowed");
        String ext = switch (ct) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
        String name = UUID.randomUUID() + ext;
        try {
            file.transferTo(dir.resolve(name));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return "/files/" + name;
    }
}
