package com.uniclub.security.evaluator;

import org.springframework.stereotype.Component;

import com.uniclub.security.UserPrincipal;

/**
 * Đảm bảo chống lỗ hổng IDOR trên hồ sơ người dùng:
 * User chỉ được xem/sửa hồ sơ của chính mình, trừ khi là SysAdmin.
 */
@Component("userSecurity")
public class UserSecurity {

    public boolean isCurrentUser(Integer userId, Object principal) {
        if (!(principal instanceof UserPrincipal userPrincipal) || userId == null) {
            return false;
        }

        if (isAdmin(userPrincipal)) {
            return true;
        }

        return userPrincipal.getId().equals(userId);
    }

    private boolean isAdmin(UserPrincipal principal) {
        return principal.getAuthorities().stream()
                .anyMatch(a -> "ROLE_SysAdmin".equalsIgnoreCase(a.getAuthority()));
    }
}

