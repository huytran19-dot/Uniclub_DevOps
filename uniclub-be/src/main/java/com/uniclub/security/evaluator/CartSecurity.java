package com.uniclub.security.evaluator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uniclub.repository.CartItemRepository;
import com.uniclub.repository.CartRepository;
import com.uniclub.security.UserPrincipal;

/**
 * Đảm bảo chống lỗ hổng IDOR trên giỏ hàng và các mục trong giỏ hàng:
 * Chỉ có chính chủ sở hữu hoặc SysAdmin mới có quyền thao tác trên giỏ hàng đó.
 */
@Component("cartSecurity")
public class CartSecurity {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public boolean isCartOwner(Integer cartId, Object principal) {
        if (!(principal instanceof UserPrincipal userPrincipal) || cartId == null) {
            return false;
        }

        if (isAdmin(userPrincipal)) {
            return true;
        }

        return cartRepository.findById(cartId)
                .map(cart -> cart.getUser() != null && userPrincipal.getId().equals(cart.getUser().getId()))
                .orElse(false);
    }

    public boolean isCartUserOwner(Integer userId, Object principal) {
        if (!(principal instanceof UserPrincipal userPrincipal) || userId == null) {
            return false;
        }

        if (isAdmin(userPrincipal)) {
            return true;
        }

        return userPrincipal.getId().equals(userId);
    }

    public boolean isCartItemOwner(Integer cartItemId, Object principal) {
        if (!(principal instanceof UserPrincipal userPrincipal) || cartItemId == null) {
            return false;
        }

        if (isAdmin(userPrincipal)) {
            return true;
        }

        return cartItemRepository.findById(cartItemId)
                .map(item -> item.getCart() != null && item.getCart().getUser() != null 
                        && userPrincipal.getId().equals(item.getCart().getUser().getId()))
                .orElse(false);
    }

    private boolean isAdmin(UserPrincipal principal) {
        return principal.getAuthorities().stream()
                .anyMatch(a -> "ROLE_SysAdmin".equalsIgnoreCase(a.getAuthority()));
    }
}

