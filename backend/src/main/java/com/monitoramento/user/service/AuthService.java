package com.monitoramento.user.service;

import com.monitoramento.config.jwt.JwtService;
import com.monitoramento.shared.exception.ApiException;
import com.monitoramento.user.dto.LoginRequest;
import com.monitoramento.user.dto.LoginResponse;
import com.monitoramento.user.entity.User;
import com.monitoramento.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DatabaseUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException("Credenciais inválidas."));

        if (!user.getEnabled() || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException("Credenciais inválidas.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);

        return LoginResponse.builder()
                .username(user.getUsername())
                .role(user.getRole())
                .token(jwtToken)
                .build();
    }
}
