package com.marshallchikari.taskmanager.authservice.controller;

import com.marshallchikari.taskmanager.authservice.dto.LoginRequestDTO;
import com.marshallchikari.taskmanager.authservice.dto.LoginResponseDTO;
import com.marshallchikari.taskmanager.authservice.dto.RegisterRequestDTO;
import com.marshallchikari.taskmanager.authservice.dto.UserResponseDTO;
import com.marshallchikari.taskmanager.authservice.model.User;
import com.marshallchikari.taskmanager.authservice.service.AuthService;
import com.marshallchikari.taskmanager.authservice.service.UserService;
import com.marshallchikari.taskmanager.authservice.util.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, UserService userService, JwtUtil jwtUtl) {
        this.authService = authService;
        this.userService = userService;
        this.jwtUtil = jwtUtl;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO loginRequestDTO) {

        logger.info("Login attempt for username='{}'", loginRequestDTO.getUsername());

        Optional<String> tokenOptional = authService.authenticate(loginRequestDTO);

        if (tokenOptional.isEmpty()) {
            logger.warn("Authentication failed for username='{}'", loginRequestDTO.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenOptional.get();

        String username = loginRequestDTO.getUsername();

        Optional<User> userEntityOpt = userService.findByUsername(username);
        if (userEntityOpt.isEmpty()) {
            logger.error("Authenticated user '{}' not found in database (this should not happen)", username);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        User userEntity = userEntityOpt.get();

        UserResponseDTO userDto = new UserResponseDTO(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getCreatedAt()
        );

        logger.info("User '{}' authenticated successfully (userId={})", username, userEntity.getId());
        return ResponseEntity.ok(new LoginResponseDTO(token, userDto));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO registerRequestDTO) {

        logger.info("Registration attempt for username='{}'", registerRequestDTO.getUsername());

        Optional<LoginResponseDTO> result = authService.register(registerRequestDTO);

        if (result.isEmpty()) {
            logger.warn("Registration failed: username '{}' already exists", registerRequestDTO.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        logger.info("User '{}' registered successfully", registerRequestDTO.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(result.get());
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Token validation attempted without Authorization header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean valid = authService.validateToken(authHeader.substring(7));
        if (!valid) {
            logger.warn("Invalid JWT presented for validation");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        logger.debug("JWT validated successfully");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponseDTO> getUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            logger.warn("Attempt to fetch /user with invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtUtil.extractUsername(token);
        User user = userService.findByUsername(username).orElseThrow();

        logger.debug("Returning user info for username='{}' (userId={})", username, user.getId());
        return ResponseEntity.ok(new UserResponseDTO(
                user.getId(), user.getUsername(), user.getCreatedAt()
        ));
    }
}
