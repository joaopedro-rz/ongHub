package com.onghub.api.controller;

import com.onghub.api.dto.response.ApiResponse;
import com.onghub.api.service.FileStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<FileUploadResponse>> upload(
        @RequestPart("file") MultipartFile file,
        @RequestParam(value = "folder", defaultValue = "misc") String folder
    ) {
        String url = fileStorageService.store(file, folder);
        return ResponseEntity.ok(ApiResponse.success(new FileUploadResponse(url), "Upload concluido"));
    }

    public record FileUploadResponse(String url) {}
}
