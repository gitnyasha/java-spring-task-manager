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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AuthController {
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

        Optional<String> tokenOptional = authService.authenticate(loginRequestDTO);

        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenOptional.get();

        String username = loginRequestDTO.getUsername();

        Optional<User> userEntityOpt = userService.findByUsername(username);
        if (userEntityOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        User userEntity = userEntityOpt.get();

        UserResponseDTO userDto = new UserResponseDTO(
                userEntity.getId(),
                userEntity.getUsername()
        );

        return ResponseEntity.ok(new LoginResponseDTO(token, userDto));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO registerRequestDTO) {

        Optional<LoginResponseDTO> result = authService.register(registerRequestDTO);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.get());
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return authService.validateToken(authHeader.substring(7))
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponseDTO> getUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtUtil.extractUsername(token);
        User user = userService.findByUsername(username).orElseThrow();

        return ResponseEntity.ok(new UserResponseDTO(
                user.getId(), user.getUsername()
        ));
    }
}
