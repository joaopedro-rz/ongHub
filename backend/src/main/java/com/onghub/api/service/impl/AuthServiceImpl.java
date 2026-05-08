package com.onghub.api.service.impl;

import com.onghub.api.config.AppProperties;
import com.onghub.api.dto.request.LoginRequest;
import com.onghub.api.dto.request.RegisterRequest;
import com.onghub.api.dto.response.AuthResponse;
import com.onghub.api.dto.response.UserResponse;
import com.onghub.api.entity.EmailVerificationToken;
import com.onghub.api.entity.PasswordResetToken;
import com.onghub.api.entity.RefreshToken;
import com.onghub.api.entity.Role;
import com.onghub.api.entity.RoleName;
import com.onghub.api.entity.User;
import com.onghub.api.exception.DuplicateEntryException;
import com.onghub.api.exception.InvalidCredentialsException;
import com.onghub.api.exception.ResourceNotFoundException;
import com.onghub.api.exception.TokenExpiredException;
import com.onghub.api.mapper.UserMapper;
import com.onghub.api.repository.EmailVerificationTokenRepository;
import com.onghub.api.repository.PasswordResetTokenRepository;
import com.onghub.api.repository.RefreshTokenRepository;
import com.onghub.api.repository.RoleRepository;
import com.onghub.api.repository.UserRepository;
import com.onghub.api.security.JwtUtil;
import com.onghub.api.service.AuthService;
import com.onghub.api.service.MailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final MailService mailService;
    private final AppProperties appProperties;

    public AuthServiceImpl(
        UserRepository userRepository,
        RoleRepository roleRepository,
        RefreshTokenRepository refreshTokenRepository,
        EmailVerificationTokenRepository emailVerificationTokenRepository,
        PasswordResetTokenRepository passwordResetTokenRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        JwtUtil jwtUtil,
        UserMapper userMapper,
        MailService mailService,
        AppProperties appProperties
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.mailService = mailService;
        this.appProperties = appProperties;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email().toLowerCase())) {
            throw new DuplicateEntryException("Email already in use");
        }

        RoleName roleName = request.role();
        Role role = roleRepository.findByName(roleName)
            .orElseGet(() -> roleRepository.save(createRole(roleName)));

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of(role));
        user.setEnabled(false);

        User savedUser = userRepository.save(user);
        EmailVerificationToken emailToken = createEmailVerificationToken(savedUser);
        sendEmailVerification(savedUser.getEmail(), emailToken.getToken());

        String accessToken = jwtUtil.generateAccessToken(savedUser);
        String refreshToken = createRefreshToken(savedUser).getToken();
        UserResponse userResponse = userMapper.toResponse(savedUser);

        return new AuthResponse(accessToken, refreshToken, userResponse);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.email().toLowerCase();
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
            );
        } catch (Exception ex) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = createRefreshToken(user).getToken();
        UserResponse userResponse = userMapper.toResponse(user);

        return new AuthResponse(accessToken, refreshToken, userResponse);
    }

    @Override
    @Transactional
    public AuthResponse refresh(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        if (storedToken.isRevoked() || storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Refresh token expired");
        }

        User user = storedToken.getUser();
        if (user.getDeletedAt() != null) {
            throw new TokenExpiredException("Refresh token expired");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = createRefreshToken(user).getToken();
        UserResponse userResponse = userMapper.toResponse(user);

        storedToken.setRevoked(true);

        return new AuthResponse(accessToken, newRefreshToken, userResponse);
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
            .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        PasswordResetToken savedToken = passwordResetTokenRepository.save(token);
        sendPasswordReset(user.getEmail(), savedToken.getToken());
    }

    @Override
    @Transactional
    public void resetPassword(String token, String password) {
        PasswordResetToken storedToken = passwordResetTokenRepository.findByToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Invalid token"));

        if (storedToken.getUsedAt() != null || storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token expired");
        }

        User user = storedToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(password));
        storedToken.setUsedAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken storedToken = emailVerificationTokenRepository.findByToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Invalid token"));

        if (storedToken.getUsedAt() != null || storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token expired");
        }

        User user = storedToken.getUser();
        user.setEnabled(true);
        storedToken.setUsedAt(LocalDateTime.now());
    }

    private EmailVerificationToken createEmailVerificationToken(User user) {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusDays(1));
        return emailVerificationTokenRepository.save(token);
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        return refreshTokenRepository.save(refreshToken);
    }

    private Role createRole(RoleName roleName) {
        Role role = new Role();
        role.setName(roleName);
        return role;
    }

    private void sendEmailVerification(String email, String token) {
        String link = appProperties.getBaseUrl() + "/verify-email?token=" + token;
        String body = "Use o link para confirmar sua conta: " + link;
        mailService.sendEmail(email, "Confirme sua conta", body);
    }

    private void sendPasswordReset(String email, String token) {
        String link = appProperties.getBaseUrl() + "/reset-password?token=" + token;
        String body = "Use o link para redefinir sua senha: " + link;
        mailService.sendEmail(email, "Recuperar senha", body);
    }
}
