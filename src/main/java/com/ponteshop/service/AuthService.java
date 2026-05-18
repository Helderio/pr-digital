package com.ponteshop.service;

import com.ponteshop.dto.AuthResponse;
import com.ponteshop.dto.LoginRequest;
import com.ponteshop.dto.RegisterRequest;
import com.ponteshop.dto.mapper.UserMapper;
import com.ponteshop.entity.User;
import com.ponteshop.enums.UserRole;
import com.ponteshop.exception.ForbiddenException;
import com.ponteshop.repository.UserRepository;
import com.ponteshop.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail().trim().toLowerCase())) {
            throw new ForbiddenException("Email já registado");
        }

        User user = User.builder()
            .name(req.getName().trim())
            .email(req.getEmail().trim().toLowerCase())
            .phone(req.getPhone())
            .passwordHash(passwordEncoder.encode(req.getPassword()))
            .role(UserRole.CUSTOMER)
            .city(req.getCity())
            .address(req.getAddress())
            .isActive(true)
            .build();

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getId(), saved.getEmail(), saved.getRole());
        return AuthResponse.builder()
            .token(token)
            .user(userMapper.toDto(saved))
            .build();
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, req.getPassword()));
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ForbiddenException("Credenciais inválidas"));
        if (!user.isActive()) throw new ForbiddenException("Conta desativada");

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        return AuthResponse.builder()
            .token(token)
            .user(userMapper.toDto(user))
            .build();
    }
}
