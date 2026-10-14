package com.ventureseed.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    // =========================================
    // USER NAME
    // =========================================

    private String name;

    private String password;

    private String role;

    // =========================================
    // EMAIL VERIFICATION
    // =========================================

    private boolean emailVerified;

    private String verificationToken;

    private LocalDateTime verificationTokenExpiry;

    // =========================================
    // PASSWORD RESET
    // =========================================

    private String resetPasswordToken;

    private LocalDateTime resetPasswordTokenExpiry;

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public User() {
    }

    // =========================================
    // ID
    // =========================================

    public Long getId() {
        return id;
    }

    // =========================================
    // NAME
    // =========================================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // =========================================
    // EMAIL
    // =========================================

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // =========================================
    // PASSWORD
    // =========================================

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // =========================================
    // ROLE
    // =========================================

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // =========================================
    // EMAIL VERIFIED
    // =========================================

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    // =========================================
    // VERIFICATION TOKEN
    // =========================================

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    // =========================================
    // VERIFICATION TOKEN EXPIRY
    // =========================================

    public LocalDateTime getVerificationTokenExpiry() {
        return verificationTokenExpiry;
    }

    public void setVerificationTokenExpiry(
            LocalDateTime verificationTokenExpiry) {

        this.verificationTokenExpiry =
                verificationTokenExpiry;
    }

    // =========================================
    // RESET PASSWORD TOKEN
    // =========================================

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(
            String resetPasswordToken) {

        this.resetPasswordToken =
                resetPasswordToken;
    }

    // =========================================
    // RESET PASSWORD TOKEN EXPIRY
    // =========================================

    public LocalDateTime getResetPasswordTokenExpiry() {
        return resetPasswordTokenExpiry;
    }

    public void setResetPasswordTokenExpiry(
            LocalDateTime resetPasswordTokenExpiry) {

        this.resetPasswordTokenExpiry =
                resetPasswordTokenExpiry;
    }
}