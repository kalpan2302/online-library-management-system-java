package com.olms.backend.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// We load both the handler and a dummy controller that throws exceptions
@WebMvcTest(controllers = {ApiExceptionHandler.class, ApiExceptionHandlerTest.TestController.class})
@DisplayName("ApiExceptionHandler Tests")
class ApiExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- A dummy controller to trigger exceptions for the handler to catch ---
    @RestController
    static class TestController {
        @GetMapping("/not-found")
        void throwNotFound() {
            throw new ResourceNotFoundException("Test resource was not found");
        }

        @GetMapping("/duplicate")
        void throwDuplicate() {
            throw new DuplicateResourceException("Test resource already exists");
        }

        @GetMapping("/illegal-state")
        void throwIllegalState() {
            throw new IllegalStateException("The application is in an invalid state");
        }

        @GetMapping("/bad-request")
        void throwIllegalArgument() {
            throw new IllegalArgumentException("An illegal argument was provided");
        }

        @PostMapping("/validation-error")
        void throwValidationError(@Valid @RequestBody TestDto dto) {
            // This endpoint triggers MethodArgumentNotValidException if 'name' is blank
        }

        @GetMapping("/access-denied")
        void throwAccessDenied() {
            throw new AccessDeniedException("Access is denied");
        }

        @GetMapping("/unauthorized")
        void throwUnauthorized() {
            // Spring Security typically throws this, but we simulate it
            throw new AuthenticationException("Full authentication is required") {};
        }

        @GetMapping("/response-status")
        void throwResponseStatus() {
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "I am a teapot");
        }
        
        @GetMapping("/fallback")
        void throwGenericException() {
            throw new RuntimeException("A generic, unexpected error occurred");
        }
    }
    
    // A dummy DTO for validation tests
    @Data @NoArgsConstructor @AllArgsConstructor
    static class TestDto {
        @NotBlank(message = "Name cannot be blank")
        private String name;
    }

    // --- Tests for each exception handler ---

    @Test
    @DisplayName("✅ Should handle ResourceNotFoundException (404)")
    void whenResourceNotFound_thenReturns404() throws Exception {
        mockMvc.perform(get("/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Test resource was not found")))
                .andExpect(jsonPath("$.path", is("/not-found")));
    }

    @Test
    @DisplayName("✅ Should handle DuplicateResourceException (409)")
    void whenDuplicateResource_thenReturns409() throws Exception {
        mockMvc.perform(get("/duplicate"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is("Test resource already exists")));
    }
    
    @Test
    @DisplayName("✅ Should handle IllegalStateException (409)")
    void whenIllegalState_thenReturns409() throws Exception {
        mockMvc.perform(get("/illegal-state"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", is("The application is in an invalid state")));
    }

    @Test
    @DisplayName("✅ Should handle IllegalArgumentException (400)")
    void whenIllegalArgument_thenReturns400() throws Exception {
        mockMvc.perform(get("/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("An illegal argument was provided")));
    }

    @Test
    @DisplayName("✅ Should handle MethodArgumentNotValidException (400)")
    void whenValidationFails_thenReturns400() throws Exception {
        TestDto invalidDto = new TestDto(" "); // Blank name is invalid
        mockMvc.perform(post("/validation-error")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                // This handler has a different response format, which is also tested here
                .andExpect(jsonPath("$").value("name: Name cannot be blank"));
    }

    @Test
    @DisplayName("✅ Should handle AccessDeniedException (403)")
    void whenAccessDenied_thenReturns403() throws Exception {
        mockMvc.perform(get("/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.error", is("Forbidden")))
                .andExpect(jsonPath("$.message", is("Insufficient privileges")));
    }
    
    @Test
    @DisplayName("✅ Should handle AuthenticationException (401)")
    void whenUnauthorized_thenReturns401() throws Exception {
        mockMvc.perform(get("/unauthorized"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("Unauthorized")))
                .andExpect(jsonPath("$.message", is("Authentication required")));
    }
    
    @Test
    @DisplayName("✅ Should handle ResponseStatusException (custom status)")
    void whenResponseStatusException_thenReturnsCorrectStatusAndMessage() throws Exception {
        mockMvc.perform(get("/response-status"))
                .andExpect(status().isIAmATeapot()) // 418
                .andExpect(jsonPath("$.status", is(418)))
                .andExpect(jsonPath("$.error", is("I'm a teapot")))
                .andExpect(jsonPath("$.message", is("I am a teapot")));
    }

    @Test
    @DisplayName("✅ Should handle any other Exception as a fallback (500)")
    void whenGenericException_thenReturns500() throws Exception {
        mockMvc.perform(get("/fallback"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.message", is("Something went wrong")));
    }
}