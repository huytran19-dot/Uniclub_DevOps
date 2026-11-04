package com.uniclub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uniclub.dto.request.Auth.LoginRequest;
import com.uniclub.dto.request.User.RegisterRequest;
import com.uniclub.dto.request.User.VerifyCodeRequest;
import com.uniclub.dto.response.Auth.LoginResponse;
import com.uniclub.dto.response.User.UserResponse;
import com.uniclub.exception.UnverifiedAccountException;
import com.uniclub.service.AuthService;
import com.uniclub.service.UserService;
import com.uniclub.service.VerificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationService verificationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserResponse user = userService.registerNewUser(request);
            return ResponseEntity.ok("Registration successful! Please check your email for verification code.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        try {
            boolean isValid = userService.verifyUserByCode(request);
            if (isValid) {
                return ResponseEntity.ok("Account verified successfully. You can now log in.");
            } else {
                return ResponseEntity.badRequest().body("Invalid or expired verification code.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend-code")
    public ResponseEntity<String> resendCode(@RequestParam String email) {
        try {
            userService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code has been resent to your email.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (UnverifiedAccountException e) {
            // ✅ User chưa verify → Gửi lại mã xác thực
            try {
                userService.resendVerificationCode(e.getEmail());
                // Return 403 with special message indicating verification needed
                return ResponseEntity.status(403).body(new UnverifiedAccountResponse(
                    e.getEmail(),
                    e.getMessage(),
                    true  // needsVerification flag
                ));
            } catch (Exception resendError) {
                return ResponseEntity.status(403).body(new UnverifiedAccountResponse(
                    e.getEmail(),
                    "Tài khoản chưa xác thực. Không thể gửi mã xác thực: " + resendError.getMessage(),
                    true
                ));
            }
        } catch (RuntimeException e) {
            // Return 400 with error message for other errors
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // ✅ Response DTO for unverified account
    private static class UnverifiedAccountResponse {
        public String email;
        public String message;
        public boolean needsVerification;
        
        public UnverifiedAccountResponse(String email, String message, boolean needsVerification) {
            this.email = email;
            this.message = message;
            this.needsVerification = needsVerification;
        }
    }
}
