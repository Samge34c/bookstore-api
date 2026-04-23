package com.taller.bookstore.service.impl;

import com.taller.bookstore.dto.request.LoginRequest;
import com.taller.bookstore.dto.request.RegisterRequest;
import com.taller.bookstore.dto.response.AuthResponse;
import com.taller.bookstore.dto.response.UserResponse;
import com.taller.bookstore.entity.User;
import com.taller.bookstore.exception.custom.DuplicateResourceException;
import com.taller.bookstore.mapper.UserMapper;
import com.taller.bookstore.repository.UserRepository;
import com.taller.bookstore.security.JwtService;
import com.taller.bookstore.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager,
                           UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Usuario", "email", request.getEmail());
        }
        User user = userMapper.toEntity(request, passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return AuthResponse.builder()
                .token(token)
                .expiresIn(jwtService.getExpiration())
                .role(role)
                .build();
    }
}
