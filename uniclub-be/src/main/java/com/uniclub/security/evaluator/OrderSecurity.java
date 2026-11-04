package com.uniclub.security.evaluator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uniclub.repository.OrderRepository;
import com.uniclub.security.UserPrincipal;

/**
 * Đảm bảo chống lỗ hổng IDOR trên đơn hàng:
 * Chỉ có Quản trị viên (SysAdmin) HOẶC chính chủ sở hữu đơn hàng mới có thể truy cập/hủy đơn.
 */
@Component("orderSecurity")
public class OrderSecurity {

    @Autowired
    private OrderRepository orderRepository;

    public boolean isOwner(Integer orderId, Object principal) {
        if (!(principal instanceof UserPrincipal userPrincipal) || orderId == null) {
            return false;
        }

        if (isAdmin(userPrincipal)) {
            return true;
        }

        return orderRepository.findById(orderId)
                .map(order -> order.getUser() != null && userPrincipal.getId().equals(order.getUser().getId()))
                .orElse(false);
    }

    public boolean isUserOwner(Integer userId, Object principal) {
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

