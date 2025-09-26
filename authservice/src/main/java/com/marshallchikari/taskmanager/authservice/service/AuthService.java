package com.marshallchikari.taskmanager.authservice.service;

import com.marshallchikari.taskmanager.authservice.dto.LoginRequestDTO;
import com.marshallchikari.taskmanager.authservice.dto.LoginResponseDTO;
import com.marshallchikari.taskmanager.authservice.dto.RegisterRequestDTO;
import com.marshallchikari.taskmanager.authservice.dto.UserResponseDTO;
import com.marshallchikari.taskmanager.authservice.model.User;
import com.marshallchikari.taskmanager.authservice.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
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

        return userService.findByUsername(loginRequestDTO.getUsername())
                .filter(u -> passwordEncoder.matches(loginRequestDTO.getPassword(),
                        u.getPassword()))
                .map(u -> jwtUtil.generateToken(u.getUsername(), u.getId()));
    }

    public boolean validateToken(String token) {
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e){
            return false;
        }
    }

    public Optional<LoginResponseDTO> register(RegisterRequestDTO request) {
        if (userService.existsByUsername(request.getUsername())) {
            return Optional.empty();
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userService.save(user);

        String token = jwtUtil.generateToken(saved.getUsername(), saved.getId());

        UserResponseDTO userDto = new UserResponseDTO(saved.getId(), saved.getUsername());

        return Optional.of(new LoginResponseDTO(token, userDto));
    }
}
