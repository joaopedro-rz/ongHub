package com.onghub.api.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc buildMockMvc() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        return MockMvcBuilders.standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .setValidator(validator)
            .build();
    }

    @Test
    void mapsDuplicateEntryException() throws Exception {
        MockMvc mockMvc = buildMockMvc();

        mockMvc.perform(get("/test/duplicate").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_ENTRY"))
            .andExpect(jsonPath("$.message").value("Duplicate"))
            .andExpect(jsonPath("$.path").value("/test/duplicate"));
    }

    @Test
    void mapsNotFoundException() throws Exception {
        MockMvc mockMvc = buildMockMvc();

        mockMvc.perform(get("/test/not-found").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void mapsTokenExpiredException() throws Exception {
        MockMvc mockMvc = buildMockMvc();

        mockMvc.perform(get("/test/token").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("TOKEN_EXPIRED"));
    }

    @Test
    void mapsInvalidCredentialsException() throws Exception {
        MockMvc mockMvc = buildMockMvc();

        mockMvc.perform(get("/test/invalid-credentials").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void mapsValidationException() throws Exception {
        MockMvc mockMvc = buildMockMvc();

        mockMvc.perform(post("/test/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void mapsGenericException() throws Exception {
        MockMvc mockMvc = buildMockMvc();

        mockMvc.perform(get("/test/generic").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/duplicate")
        public void duplicate() {
            throw new DuplicateEntryException("Duplicate");
        }

        @GetMapping("/not-found")
        public void notFound() {
            throw new ResourceNotFoundException("Missing");
        }

        @GetMapping("/token")
        public void token() {
            throw new TokenExpiredException("Expired");
        }

        @GetMapping("/invalid-credentials")
        public void invalidCredentials() {
            throw new InvalidCredentialsException("Invalid");
        }

        @GetMapping("/generic")
        public void generic() {
            throw new RuntimeException("Boom");
        }

        @PostMapping("/validate")
        public void validate(@Valid @RequestBody ValidationRequest request) {
        }
    }

    record ValidationRequest(@NotBlank String name) {}
}
