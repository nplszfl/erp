package com.crossborder.erp.user.service.impl;

import com.crossborder.erp.user.dto.LoginRequest;
import com.crossborder.erp.user.dto.LoginResponse;
import com.crossborder.erp.user.entity.User;
import com.crossborder.erp.user.mapper.UserMapper;
import com.crossborder.erp.user.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 认证服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setRealName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setStatus(1);
        testUser.setLastLoginTime(LocalDateTime.now());
    }

    @Test
    void testLogin_Success() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userMapperEncoder.matches("password123", "$2a$10$encodedPassword")).thenReturn(true);
        when(redisTemplate.opsForValue().set(anyString(), anyString(), anyLong(), any())).thenReturn(true);

        // When
        LoginResponse response = authService.login(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getUserInfo());
        assertEquals("Test User", response.getUserInfo().getRealName());
    }

    @Test
    void testLogin_UserNotFound() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password123");

        // When & Then
        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void testLogin_UserDisabled() {
        // Given
        testUser.setStatus(0);
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void testLogin_WrongPassword() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(passwordEncoder.matches("wrongpassword", "$2a$10$encodedPassword")).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void testLogout_Success() {
        // Given
        String token = "Bearer eyJhbGciOiJIUzI1NiIsInRQ";
        when(redisTemplate.delete(anyString())).thenReturn(true);

        // When
        authService.logout(token);

        // Then
        verify(redisTemplate, times(1)).delete(anyString());
    }
}
