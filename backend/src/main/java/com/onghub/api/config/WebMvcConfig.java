package com.onghub.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final StorageProperties storageProperties;

    public WebMvcConfig(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dir = Path.of(storageProperties.getUploadDir()).toAbsolutePath().normalize();
        String location = dir.toUri().toString();
        registry.addResourceHandler("/api/v1/files/**")
            .addResourceLocations(location.endsWith("/") ? location : location + "/");
    }
}
