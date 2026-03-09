package com.chitalebandhu.chitalebandhu.controller;

import com.chitalebandhu.chitalebandhu.DTOs.AuthRequest;
import com.chitalebandhu.chitalebandhu.DTOs.AuthResponse;
import com.chitalebandhu.chitalebandhu.DTOs.RefreshRequest;
import com.chitalebandhu.chitalebandhu.Utility.JwtUtil;
import com.chitalebandhu.chitalebandhu.entity.User;
import com.chitalebandhu.chitalebandhu.repository.UserRepository;
import com.chitalebandhu.chitalebandhu.services.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request){
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            )
        );

        User user = userRepository.findByUsername(request.getUsername()).get();

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());

        String refreshToken = refreshTokenService.createRefreshToken(user.getUsername()).getToken();

        return new AuthResponse(accessToken, refreshToken, user.getRole());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request){
        var token = refreshTokenService
                .verifyExpiration(
                        refreshTokenService.findByToken(request.getRefreshToken())
                );

        User user = userRepository.findById(token.getUser()).get();

        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());

        return new AuthResponse(newAccessToken, token.getToken(), user.getRole());
    }

    @PostMapping("/logout")
    public void logout(@RequestBody RefreshRequest request){
        refreshTokenService.findByToken(request.getRefreshToken()).getUser();
    }
}