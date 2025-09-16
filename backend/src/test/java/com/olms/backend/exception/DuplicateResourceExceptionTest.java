package com.olms.backend.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("DuplicateResourceException Test")
class DuplicateResourceExceptionTest {

    @Test
    @DisplayName("✅ Should create exception with the correct message and type")
    void shouldCreateExceptionWithMessageAndCorrectType() {
        // Arrange
        String expectedMessage = "A user with this email already exists.";

        // Act
        DuplicateResourceException exception = new DuplicateResourceException(expectedMessage);

        // Assert
        // Verify that the message passed to the constructor is stored correctly.
        assertEquals(expectedMessage, exception.getMessage());

        // Verify that it is an unchecked exception (i.e., it extends RuntimeException).
        assertInstanceOf(RuntimeException.class, exception, "Exception should be a subclass of RuntimeException.");
    }
}