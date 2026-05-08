package com.onghub.api.service.impl;

import com.onghub.api.config.StorageProperties;
import com.onghub.api.exception.BadRequestException;
import com.onghub.api.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Set<String> ALLOWED_FOLDERS = Set.of("ngos", "campaigns", "donations", "misc");

    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/jpeg",
        "image/png",
        "image/webp",
        "application/pdf"
    );

    private final StorageProperties storageProperties;

    public FileStorageServiceImpl(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public String store(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }
        String safeFolder = folder == null || folder.isBlank() ? "misc" : folder.toLowerCase(Locale.ROOT);
        if (!ALLOWED_FOLDERS.contains(safeFolder)) {
            throw new BadRequestException("Invalid folder");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("Unsupported file type");
        }

        String original = file.getOriginalFilename();
        String ext = extension(original);
        String filename = UUID.randomUUID() + ext;

        Path root = Path.of(storageProperties.getUploadDir()).toAbsolutePath().normalize();
        Path targetDir = root.resolve(safeFolder).normalize();
        if (!targetDir.startsWith(root)) {
            throw new BadRequestException("Invalid path");
        }
        try {
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(filename);
            file.transferTo(targetFile.toFile());
        } catch (IOException ex) {
            throw new BadRequestException("Could not store file");
        }

        String base = storageProperties.getPublicBaseUrl().replaceAll("/+$", "");
        return base + "/" + safeFolder + "/" + filename;
    }

    private static String extension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase(Locale.ROOT);
        if (ext.length() > 10) {
            return "";
        }
        return ext;
    }
}
