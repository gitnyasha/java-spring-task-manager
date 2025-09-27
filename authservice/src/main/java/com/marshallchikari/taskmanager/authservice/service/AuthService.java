package com.marshallchikari.taskmanager.authservice.service;

import com.marshallchikari.taskmanager.authservice.dto.LoginRequestDTO;
import com.marshallchikari.taskmanager.authservice.dto.LoginResponseDTO;
import com.marshallchikari.taskmanager.authservice.dto.RegisterRequestDTO;
import com.marshallchikari.taskmanager.authservice.dto.UserResponseDTO;
import com.marshallchikari.taskmanager.authservice.model.User;
import com.marshallchikari.taskmanager.authservice.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {

        Optional<User> userOpt = userService.findByUsername(loginRequestDTO.getUsername());

        if (userOpt.isEmpty()) {
            logger.warn("Authentication failed: username '{}' not found", loginRequestDTO.getUsername());
            return Optional.empty();
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            logger.warn("Authentication failed: invalid credentials for username='{}'", loginRequestDTO.getUsername());
            return Optional.empty();
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        logger.info("Generated JWT for userId={}", user.getId());
        return Optional.of(token);
    }

    public boolean validateToken(String token) {
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e){
            logger.warn("JWT validation failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during token validation", e);
            return false;
        }
    }

    public Optional<LoginResponseDTO> register(RegisterRequestDTO request) {
        if (userService.existsByUsername(request.getUsername())) {
            logger.warn("Registration attempt with existing username='{}'", request.getUsername());
            return Optional.empty();
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userService.save(user);

        String token = jwtUtil.generateToken(saved.getUsername(), saved.getId());

        logger.info("User registered with userId={}", saved.getId());

        UserResponseDTO userDto = new UserResponseDTO(saved.getId(), saved.getUsername(), saved.getCreatedAt());

        return Optional.of(new LoginResponseDTO(token, userDto));
    }
}
