package com.onghub.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    /**
     * Directory where uploaded files are stored (absolute or relative path).
     */
    private String uploadDir = "./data/uploads";

    /**
     * Public base URL used to build links returned to clients (no trailing slash).
     */
    private String publicBaseUrl = "http://localhost:8080/api/v1/files";

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }
}
