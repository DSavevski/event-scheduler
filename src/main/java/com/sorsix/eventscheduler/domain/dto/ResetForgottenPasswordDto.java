package com.sorsix.eventscheduler.domain.dto;

public class ResetForgottenPasswordDto {
    String password;
    String token;

    public ResetForgottenPasswordDto() {
    }

    public ResetForgottenPasswordDto(String password, String token) {
        this.password = password;
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
