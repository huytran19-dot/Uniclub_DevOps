package com.uniclub.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uniclub.config.JwtConfig;
import com.uniclub.dto.request.Auth.LoginRequest;
import com.uniclub.dto.response.Auth.LoginResponse;
import com.uniclub.entity.User;
import com.uniclub.exception.UnverifiedAccountException;
import com.uniclub.repository.UserRepository;

/**
 * Authentication Service
 * 
 * User Status (trạng thái tài khoản):
 * - 0: Unverified (chưa xác thực email - mới đăng ký)
 * - 1: Active (đã xác thực email - đang hoạt động)
 * - 2: Disabled (đã bị vô hiệu hóa bởi admin - không thể đăng nhập)
 * 
 * User Role (vai trò - lưu ở bảng role):
 * - 1: SysAdmin (Quản trị viên)
 * - 2: Buyer (Người mua)
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtConfig jwtConfig;

    public LoginResponse login(LoginRequest loginRequest) {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }

        User user = userOptional.get();

        // Check user status
        // Status: 0 = Unverified, 1 = Active, 2 = Disabled
        if (user.getStatus() == 0) {
            // ✅ Throw special exception with email for verification resend
            throw new UnverifiedAccountException(
                user.getEmail(),
                "Tài khoản chưa được xác thực. Mã xác thực mới đã được gửi đến email của bạn."
            );
        }
        
        if (user.getStatus() == 2) {
            throw new RuntimeException("Tài khoản của bạn đã bị vô hiệu hóa. Vui lòng liên hệ quản trị viên để biết thêm chi tiết.");
        }
        
        // Only active users (status = 1) can login
        if (user.getStatus() != 1) {
            throw new RuntimeException("Trạng thái tài khoản không hợp lệ. Vui lòng liên hệ quản trị viên.");
        }

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }

        // Generate JWT token with email, userId and role
        String token = jwtConfig.generateToken(user.getEmail(), user.getId(), user.getRole().getName());

        // Return login response
        return new LoginResponse(
            token,
            "Bearer",
            user.getId(),
            user.getEmail(),
            user.getFullname(),
            user.getRole().getName()
        );
    }
}
