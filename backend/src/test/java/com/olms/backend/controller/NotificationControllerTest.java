package com.olms.backend.controller;

import com.olms.backend.entities.Notification;
import com.olms.backend.entities.User;
import com.olms.backend.repository.NotificationRepository;
import com.olms.backend.repository.UserRepository;
import com.olms.backend.security.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@WithMockUser(username = "user@test.com")
@DisplayName("NotificationController Tests")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationRepository notificationRepository;
    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("✅ [GET /me] Should get notifications for the current user")
    void myNotifications() throws Exception {
        User currentUser = new User();
        currentUser.setId(1L);
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::currentEmail).thenReturn("user@test.com");
            when(userRepository.findByEmailIgnoreCase("user@test.com")).thenReturn(Optional.of(currentUser));
            when(notificationRepository.findByUserIdOrderBySentAtDesc(1L)).thenReturn(List.of());

            mockMvc.perform(get("/api/notifications/me"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("✅ [POST /{id}/read] Should mark a notification as read")
    void markRead() throws Exception {
        Notification notification = new Notification();
        notification.setId(1L);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        mockMvc.perform(post("/api/notifications/1/read").with(csrf()))
                .andExpect(status().isOk());
        verify(notificationRepository).save(any(Notification.class));
    }
}