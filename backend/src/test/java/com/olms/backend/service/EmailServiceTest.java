package com.olms.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService Tests")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @BeforeEach
    void setUp() {
        // Set configured values for testing
        ReflectionTestUtils.setField(emailService, "from", "no-reply@olms.com");
        ReflectionTestUtils.setField(emailService, "frontendBaseUrl", "http://localhost:3000");
    }

    @Test
    @DisplayName("✅ Should send verification email with correct content")
    void sendVerificationEmail_shouldConstructMessageCorrectly() {
        // Arrange
        String to = "test@example.com";
        String link = "http://localhost:8080/verify?token=123";
        int expiryHours = 24;

        // Act
        emailService.sendVerificationEmail(to, link, expiryHours);

        // Assert
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals("no-reply@olms.com", sentMessage.getFrom());
        assertEquals("Verify your BiblioTrack account", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains(link));
        assertTrue(sentMessage.getText().contains("expires in 24 hour(s)"));
    }

    @Test
    @DisplayName("✅ Should send verification success email with correct content")
    void sendVerificationSuccessEmail_shouldConstructMessageCorrectly() {
        // Arrange
        String to = "success@example.com";

        // Act
        emailService.sendVerificationSuccessEmail(to);

        // Assert
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals("Your BiblioTrack account is verified 🎉", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("http://localhost:3000/login"));
    }
}