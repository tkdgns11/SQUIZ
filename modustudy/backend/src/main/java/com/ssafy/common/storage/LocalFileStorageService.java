package com.ssafy.common.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService {
    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final Path basePath;

    public LocalFileStorageService(@Value("${app.storage.base-path:./uploads}") String basePath) {
        this.basePath = Paths.get(basePath).toAbsolutePath().normalize();
    }

    public String saveMeetingPhoto(Long meetingId, MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String safeName = originalName == null ? "photo" : originalName.replace("\\", "_").replace("/", "_");
        String extension = extractExtension(safeName);
        String timestamp = LocalDateTime.now().format(FILE_TS);
        String filename = timestamp + "-" + UUID.randomUUID() + extension;
        Path targetDir = basePath.resolve("meetings").resolve(String.valueOf(meetingId)).normalize();
        Path targetFile = targetDir.resolve(filename).normalize();
        if (!targetFile.startsWith(targetDir)) {
            throw new IllegalArgumentException("Invalid path");
        }
        try {
            Files.createDirectories(targetDir);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store file", e);
        }
        return "/uploads/meetings/" + meetingId + "/" + filename;
    }

    public Path getBasePath() {
        return basePath;
    }

    private String extractExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        String ext = filename.substring(dotIndex).toLowerCase(Locale.ROOT);
        if (ext.length() > 10) {
            return "";
        }
        return ext;
    }
}
