package com.uniwork.service;

import com.uniwork.common.utils.BeanCopyUtils;
import com.uniwork.exceptions.CoreException;
import com.uniwork.exceptions.ErrorCode;
import com.uniwork.modules.user.dto.ProfileDTO;
import com.uniwork.modules.user.entity.User;
import com.uniwork.modules.user.projection.UserProfileProjection;
import com.uniwork.modules.user.repository.UserRepository;
import com.uniwork.modules.user.request.UpdateProfileRequest;
import com.uniwork.modules.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(10L);
        testUser.setName("Nguyen Developer");
        testUser.setEmail("nguyen@uniwork.com");
        testUser.setPassword("$2a$10$hashedPassword123");
        testUser.setActive(true);
    }

    @Test
    @DisplayName("login should return user when password matches")
    void testLogin_Success() {
        when(userRepository.findByEmail("nguyen@uniwork.com")).thenReturn(testUser);
        when(passwordEncoder.matches("rawPassword123", "$2a$10$hashedPassword123")).thenReturn(true);

        User loggedIn = userService.login("nguyen@uniwork.com", "rawPassword123");

        assertThat(loggedIn).isNotNull();
        assertThat(loggedIn.getEmail()).isEqualTo("nguyen@uniwork.com");
    }

    @Test
    @DisplayName("login should throw RuntimeException when password does not match")
    void testLogin_WrongPassword_ThrowsException() {
        when(userRepository.findByEmail("nguyen@uniwork.com")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongPass", "$2a$10$hashedPassword123")).thenReturn(false);

        assertThrows(RuntimeException.class, () ->
                userService.login("nguyen@uniwork.com", "wrongPass")
        );
    }

    @Test
    @DisplayName("updateProfile should encode password with BCrypt before saving (security fix)")
    void testUpdateProfile_EncodesPassword() {
        when(userRepository.findByUserId(10L)).thenReturn(testUser);
        when(passwordEncoder.encode("newSecretPass")).thenReturn("$2a$10$newHashedPassword456");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("Nguyen Middle Dev");
        request.setPassword("newSecretPass");

        User updated = userService.updateProfile(10L, request);

        assertThat(updated.getPassword()).isEqualTo("$2a$10$newHashedPassword456");
        assertThat(updated.getName()).isEqualTo("Nguyen Middle Dev");

        verify(passwordEncoder, times(1)).encode("newSecretPass");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("getUserById should throw CoreException USER_NOT_FOUND when user does not exist")
    void testGetUserById_NotFound_ThrowsCoreException() {
        when(userRepository.getUserProfile(999L)).thenReturn(null);

        CoreException ex = assertThrows(CoreException.class, () ->
                userService.getUserById(999L)
        );

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}
