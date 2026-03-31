package com.chitalebandhu.chitalebandhu.DTOs;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String role;
    private String userId;

    public AuthResponse(String accessToken, String refreshToken, String role , String userId){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
        this.userId  = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
