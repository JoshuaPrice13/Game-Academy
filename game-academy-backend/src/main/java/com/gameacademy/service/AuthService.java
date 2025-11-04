package com.gameacademy.service;

import com.gameacademy.dto.AuthResponseDTO;
import com.gameacademy.dto.LoginRequestDTO;
import com.gameacademy.dto.RegisterRequestDTO;
import com.gameacademy.exception.DuplicateResourceException;
import com.gameacademy.exception.ResourceNotFoundException;
import com.gameacademy.model.Student;
import com.gameacademy.model.User;
import com.gameacademy.repository.StudentRepository;
import com.gameacademy.repository.UserRepository;
import com.gameacademy.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public String register(RegisterRequestDTO request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // Create user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .createdAt(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        // Create student record if role is STUDENT
        if (request.getRole() == User.UserRole.STUDENT) {
            Student student = Student.builder()
                    .userId(savedUser.getId())
                    .totalPoints(0)
                    .level(1)
                    .build();
            studentRepository.save(student);
        }

        return "User registered successfully";
    }

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request) {
        // Find user by email first to get username
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Authenticate user with username
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        request.getPassword()
                )
        );

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getEmail());

        return AuthResponseDTO.builder()
                .token(token)
                .userId(user.getId())
                .role(user.getRole())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public User getCurrentUser(String token) {
        String username = jwtUtil.extractUsername(token);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
