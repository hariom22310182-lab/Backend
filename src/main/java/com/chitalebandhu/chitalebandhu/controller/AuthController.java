package com.chitalebandhu.chitalebandhu.controller;

import com.chitalebandhu.chitalebandhu.DTOs.AuthRequest;
import com.chitalebandhu.chitalebandhu.DTOs.AuthResponse;
import com.chitalebandhu.chitalebandhu.DTOs.ChangePasswordRequest;
import com.chitalebandhu.chitalebandhu.DTOs.RefreshRequest;
import com.chitalebandhu.chitalebandhu.Utility.JwtUtil;
import com.chitalebandhu.chitalebandhu.entity.Member;
import com.chitalebandhu.chitalebandhu.entity.RefreshToken;
import com.chitalebandhu.chitalebandhu.entity.User;
import com.chitalebandhu.chitalebandhu.repository.MemberRepository;
import com.chitalebandhu.chitalebandhu.repository.UserRepository;
import com.chitalebandhu.chitalebandhu.services.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

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
        String username = user.getUsername() == null ? "" : user.getUsername().trim();
        if (username.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }

        if (memberRepository.existsByEmailIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An employee with this email already exists");
        }

        User existingUser = userRepository.findFirstByUsernameIgnoreCase(username).orElse(null);
        if (existingUser != null) {
            if (existingUser.getRole() != null && !existingUser.getRole().trim().isEmpty()
                    && !"USER".equalsIgnoreCase(existingUser.getRole().trim())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
            }

            existingUser.setUsername(username);
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            if (user.getRole() != null && !user.getRole().trim().isEmpty()) {
                existingUser.setRole(user.getRole().trim());
            }
            return userRepository.save(existingUser);
        }

        user.setUsername(username);
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

        String username = request.getUsername() == null ? "" : request.getUsername().trim();

        User user = userRepository.findFirstByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
        String refreshToken = refreshTokenService.createRefreshToken(user.getUsername()).getToken();

        Optional<Member> member = memberRepository.findFirstByEmailIgnoreCase(username);
        if(member.isPresent()){
            return new AuthResponse(accessToken, refreshToken, user.getRole() ,member.get().getId());
        }

        System.out.println("sending usr id " + user.getId());
        return new AuthResponse(accessToken, refreshToken, user.getRole() ,user.getId());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request){
        var token = refreshTokenService
                .verifyExpiration(
                        refreshTokenService.findByToken(request.getRefreshToken())
                );



        if(userRepository.findById(token.getUser()).isPresent()){
            User user = userRepository.findById(token.getUser()).get();
        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
        return new AuthResponse(newAccessToken, token.getToken(), user.getRole(),user.getId());
        }
        else{
            return null;
        }
    }

    @PostMapping("/logout")
    public void logout(@RequestBody RefreshRequest request){
        RefreshToken token = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.deleteToken(token);
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()
                || request.getOldPassword() == null || request.getOldPassword().trim().isEmpty()
                || request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            return new ResponseEntity<>("All fields are required", HttpStatus.BAD_REQUEST);
        }

        var userOpt = userRepository.findFirstByUsernameIgnoreCase(request.getUsername().trim());
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return new ResponseEntity<>("Current password is incorrect", HttpStatus.FORBIDDEN);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }
}