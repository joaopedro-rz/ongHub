package com.onghub.api.service;

import com.onghub.api.config.AppProperties;
import com.onghub.api.dto.request.LoginRequest;
import com.onghub.api.dto.request.RegisterRequest;
import com.onghub.api.dto.response.AuthResponse;
import com.onghub.api.entity.Role;
import com.onghub.api.entity.RoleName;
import com.onghub.api.entity.User;
import com.onghub.api.exception.DuplicateEntryException;
import com.onghub.api.mapper.UserMapper;
import com.onghub.api.repository.EmailVerificationTokenRepository;
import com.onghub.api.repository.PasswordResetTokenRepository;
import com.onghub.api.repository.RefreshTokenRepository;
import com.onghub.api.repository.RoleRepository;
import com.onghub.api.repository.UserRepository;
import com.onghub.api.security.JwtUtil;
import com.onghub.api.service.MailService;
import com.onghub.api.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Test
    void registerCreatesUserAndTokens() {
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        EmailVerificationTokenRepository emailVerificationTokenRepository = mock(EmailVerificationTokenRepository.class);
        PasswordResetTokenRepository passwordResetTokenRepository = mock(PasswordResetTokenRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtUtil jwtUtil = mock(JwtUtil.class);
        UserMapper userMapper = mock(UserMapper.class);
        MailService mailService = mock(MailService.class);
        AppProperties appProperties = new AppProperties();
        appProperties.setBaseUrl("http://localhost:5173");

        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed");
        Role role = new Role();
        role.setName(RoleName.DONOR);
        when(roleRepository.findByName(RoleName.DONOR)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateAccessToken(any(User.class))).thenReturn("access");
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(emailVerificationTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponse(any(User.class))).thenReturn(null);

        AuthServiceImpl authService = new AuthServiceImpl(
            userRepository,
            roleRepository,
            refreshTokenRepository,
            emailVerificationTokenRepository,
            passwordResetTokenRepository,
            passwordEncoder,
            authenticationManager,
            jwtUtil,
            userMapper,
            mailService,
            appProperties
        );

        AuthResponse response = authService.register(
            new RegisterRequest("User", "user@example.com", "password", RoleName.DONOR)
        );

        assertThat(response.accessToken()).isEqualTo("access");
        verify(emailVerificationTokenRepository).save(any());
        verify(mailService).sendEmail(eq("user@example.com"), any(), contains("verify-email"));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void loginAuthenticatesAndReturnsTokens() {
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        EmailVerificationTokenRepository emailVerificationTokenRepository = mock(EmailVerificationTokenRepository.class);
        PasswordResetTokenRepository passwordResetTokenRepository = mock(PasswordResetTokenRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtUtil jwtUtil = mock(JwtUtil.class);
        UserMapper userMapper = mock(UserMapper.class);
        MailService mailService = mock(MailService.class);
        AppProperties appProperties = new AppProperties();
        appProperties.setBaseUrl("http://localhost:5173");

        User user = new User();
        user.setEmail("user@example.com");
        user.setRoles(Set.of());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(user)).thenReturn("access");
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AuthServiceImpl authService = new AuthServiceImpl(
            userRepository,
            roleRepository,
            refreshTokenRepository,
            emailVerificationTokenRepository,
            passwordResetTokenRepository,
            passwordEncoder,
            authenticationManager,
            jwtUtil,
            userMapper,
            mailService,
            appProperties
        );

        AuthResponse response = authService.login(new LoginRequest("user@example.com", "password"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(response.accessToken()).isEqualTo("access");
    }

    @Test
    void registerFailsWhenEmailExists() {
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        EmailVerificationTokenRepository emailVerificationTokenRepository = mock(EmailVerificationTokenRepository.class);
        PasswordResetTokenRepository passwordResetTokenRepository = mock(PasswordResetTokenRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtUtil jwtUtil = mock(JwtUtil.class);
        UserMapper userMapper = mock(UserMapper.class);
        MailService mailService = mock(MailService.class);
        AppProperties appProperties = new AppProperties();
        appProperties.setBaseUrl("http://localhost:5173");

        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        AuthServiceImpl authService = new AuthServiceImpl(
            userRepository,
            roleRepository,
            refreshTokenRepository,
            emailVerificationTokenRepository,
            passwordResetTokenRepository,
            passwordEncoder,
            authenticationManager,
            jwtUtil,
            userMapper,
            mailService,
            appProperties
        );

        assertThatThrownBy(() -> authService.register(
            new RegisterRequest("User", "user@example.com", "password", RoleName.DONOR)
        )).isInstanceOf(DuplicateEntryException.class);
    }
}
