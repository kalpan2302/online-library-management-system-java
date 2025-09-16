package com.olms.backend.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("ResourceNotFoundException Test")
class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("✅ Should create exception with the correct message and type")
    void shouldCreateExceptionWithMessageAndCorrectType() {
        // Arrange
        String expectedMessage = "Book with ID 42 could not be found.";

        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(expectedMessage);

        // Assert
        // Check if the message is correctly set.
        assertEquals(expectedMessage, exception.getMessage());

        // Check if the exception is an instance of RuntimeException.
        assertInstanceOf(RuntimeException.class, exception, "Exception should be a subclass of RuntimeException.");
    }
}