package com.ventureseed.controller;

import com.ventureseed.entity.User;
import com.ventureseed.repository.UserRepository;
import com.ventureseed.service.EmailService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository users;
    private final EmailService emailService;

    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    private static final String SECRET =
            "VentureSeedDemoSecretKeyMustBeAtLeast32Chars!";

    private static final long TOKEN_MS = 3600000L;

    private static final int VERIFICATION_EXPIRY_MINUTES = 10;

    private final Random random = new Random();

    public AuthController(
            UserRepository users,
            EmailService emailService) {

        this.users = users;
        this.emailService = emailService;
    }

    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public Map<String, String> register(
            @RequestBody User u) {

        // Check name
        if (u.getName() == null ||
                u.getName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Name is required"
            );
        }

        u.setName(u.getName().trim());

        // Check email
        if (u.getEmail() == null ||
                u.getEmail().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email is required"
            );
        }

        // Check password
        if (u.getPassword() == null ||
                u.getPassword().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password is required"
            );
        }

        String email =
                u.getEmail().trim().toLowerCase();

        // Check duplicate email
        if (users.findByEmail(email).isPresent()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists"
            );
        }

        // Save user details
        u.setEmail(email);

        u.setPassword(
                encoder.encode(u.getPassword())
        );

        u.setRole("USER");

        u.setEmailVerified(false);

        // Generate 6-digit verification code
        String verificationToken =
                String.format(
                        "%06d",
                        random.nextInt(1000000)
                );

        u.setVerificationToken(
                verificationToken
        );

        u.setVerificationTokenExpiry(
                LocalDateTime.now()
                        .plusMinutes(
                                VERIFICATION_EXPIRY_MINUTES
                        )
        );

        // Save user
        users.save(u);

        // Send verification email
        try {

            emailService.sendVerificationEmail(
                    u.getEmail(),
                    verificationToken
            );

        } catch (Exception e) {

            users.delete(u);

            System.out.println(
                    "Email sending failed: "
                            + e.getMessage()
            );

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not send verification email. Please try again."
            );
        }

        Map<String, String> response =
                new LinkedHashMap<>();

        response.put(
                "message",
                "Account created successfully. "
                        + "A verification code has been sent "
                        + "to your email."
        );

        response.put(
                "email",
                u.getEmail()
        );

        response.put(
                "name",
                u.getName()
        );

        return response;
    }

    // =========================================================
    // VERIFY EMAIL
    // =========================================================

    @PostMapping("/verify-email")
    public Map<String, String> verifyEmail(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");
        String token = request.get("token");

        if (email == null ||
                email.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email is required"
            );
        }

        if (token == null ||
                token.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Verification code is required"
            );
        }

        email = email.trim().toLowerCase();

        User user =
                users.findByEmail(email)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Account not found"
                                )
                        );

        if (user.isEmailVerified()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email is already verified"
            );
        }

        if (user.getVerificationToken() == null ||
                !user.getVerificationToken()
                        .equals(token.trim())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid verification code"
            );
        }

        if (user.getVerificationTokenExpiry() == null ||
                LocalDateTime.now()
                        .isAfter(
                                user.getVerificationTokenExpiry()
                        )) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Verification code has expired. Please register again."
            );
        }

        user.setEmailVerified(true);

        user.setVerificationToken(null);

        user.setVerificationTokenExpiry(null);

        users.save(user);

        Map<String, String> response =
                new LinkedHashMap<>();

        response.put(
                "message",
                "Email verified successfully. "
                        + "You can now sign in."
        );

        return response;
    }

    // =========================================================
    // FORGOT PASSWORD
    // =========================================================

    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");

        if (email == null ||
                email.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email is required"
            );
        }

        email = email.trim().toLowerCase();

        User user =
                users.findByEmail(email)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No account found with this email"
                                )
                        );

        // Generate 6-digit reset code
        String resetToken =
                String.format(
                        "%06d",
                        random.nextInt(1000000)
                );

        user.setResetPasswordToken(
                resetToken
        );

        user.setResetPasswordTokenExpiry(
                LocalDateTime.now()
                        .plusMinutes(
                                VERIFICATION_EXPIRY_MINUTES
                        )
        );

        users.save(user);

        // Send reset email
        try {

            emailService.sendPasswordResetEmail(
                    user.getEmail(),
                    resetToken
            );

        } catch (Exception e) {

            System.out.println(
                    "Password reset email failed: "
                            + e.getMessage()
            );

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not send password reset email. Please try again."
            );
        }

        Map<String, String> response =
                new LinkedHashMap<>();

        response.put(
                "message",
                "A password reset code has been sent to your email."
        );

        response.put(
                "email",
                user.getEmail()
        );

        return response;
    }

    // =========================================================
    // RESET PASSWORD
    // =========================================================

    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");
        String token = request.get("token");
        String password = request.get("password");

        if (email == null ||
                email.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email is required"
            );
        }

        if (token == null ||
                token.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reset code is required"
            );
        }

        if (password == null ||
                password.length() < 6) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password must be at least 6 characters"
            );
        }

        email = email.trim().toLowerCase();

        User user =
                users.findByEmail(email)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Account not found"
                                )
                        );

        // Check reset code
        if (user.getResetPasswordToken() == null ||
                !user.getResetPasswordToken()
                        .equals(token.trim())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid reset code"
            );
        }

        // Check expiry
        if (user.getResetPasswordTokenExpiry() == null ||
                LocalDateTime.now()
                        .isAfter(
                                user.getResetPasswordTokenExpiry()
                        )) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reset code has expired. Please request a new code."
            );
        }

        // Encrypt new password
        user.setPassword(
                encoder.encode(password)
        );

        // Remove reset code after successful reset
        user.setResetPasswordToken(null);

        user.setResetPasswordTokenExpiry(null);

        users.save(user);

        Map<String, String> response =
                new LinkedHashMap<>();

        response.put(
                "message",
                "Password reset successfully. "
                        + "You can now sign in."
        );

        return response;
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public Map<String, String> login(
            @RequestBody User input) {

        if (input.getEmail() == null ||
                input.getEmail().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email is required"
            );
        }

        if (input.getPassword() == null ||
                input.getPassword().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password is required"
            );
        }

        User u =
                users.findByEmail(
                        input.getEmail()
                                .trim()
                                .toLowerCase()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Account not found"
                        )
                );

        // Check password
        if (!encoder.matches(
                input.getPassword(),
                u.getPassword())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Wrong password"
            );
        }

        // Check email verification
        if (!u.isEmailVerified()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please verify your email before signing in."
            );
        }

        // =====================================================
        // CREATE JWT
        // =====================================================

        String token =
                Jwts.builder()
                        .subject(u.getEmail())

                        // User name
                        .claim(
                                "name",
                                u.getName()
                        )

                        // User role
                        .claim(
                                "role",
                                u.getRole()
                        )

                        .issuedAt(
                                new Date()
                        )

                        .expiration(
                                new Date(
                                        System.currentTimeMillis()
                                                + TOKEN_MS
                                )
                        )

                        .signWith(
                                Keys.hmacShaKeyFor(
                                        SECRET.getBytes(
                                                StandardCharsets.UTF_8
                                        )
                                )
                        )

                        .compact();

        // Return login information
        return Map.of(
                "token",
                token,

                "name",
                u.getName(),

                "email",
                u.getEmail(),

                "role",
                u.getRole()
        );
    }

    // =========================================================
    // GET ALL USERS - ADMIN ONLY
    // =========================================================

    @GetMapping("/users")
    public List<Map<String, Object>> getAllUsers(
            @RequestHeader(
                    value = "X-User-Role",
                    required = false
            )
            String role) {

        requireAdmin(role);

        return users.findAll()
                .stream()
                .map(u -> {

                    Map<String, Object> user =
                            new LinkedHashMap<>();

                    user.put(
                            "id",
                            u.getId()
                    );

                    user.put(
                            "name",
                            u.getName()
                    );

                    user.put(
                            "email",
                            u.getEmail()
                    );

                    user.put(
                            "role",
                            u.getRole()
                    );

                    user.put(
                            "emailVerified",
                            u.isEmailVerified()
                    );

                    return user;
                })
                .collect(
                        Collectors.toList()
                );
    }

    // =========================================================
    // ADMIN SECURITY CHECK
    // =========================================================

    private void requireAdmin(
            String role) {

        if (role == null ||
                !"ADMIN".equalsIgnoreCase(role)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Admin access required"
            );
        }
    }
}