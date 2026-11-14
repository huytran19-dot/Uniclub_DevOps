package com.uniclub.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.uniclub.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {

    private Integer id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean enabled;

    public static UserPrincipal create(User user) {
        String roleName = (user.getRole() != null && user.getRole().getName() != null)
                ? user.getRole().getName()
                : "Buyer";

        List<GrantedAuthority> authorities = new ArrayList<>();
        // 1. Phân quyền cấp Role (tiền tố ROLE_ chuẩn Spring Security)
        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));

        // 2. Phân quyền chi tiết (Permission-Based Access Control)
        if ("SysAdmin".equalsIgnoreCase(roleName)) {
            authorities.add(new SimpleGrantedAuthority("USER_MANAGE"));
            authorities.add(new SimpleGrantedAuthority("ROLE_MANAGE"));
            authorities.add(new SimpleGrantedAuthority("PRODUCT_MANAGE"));
            authorities.add(new SimpleGrantedAuthority("GRN_MANAGE"));
            authorities.add(new SimpleGrantedAuthority("SUPPLIER_MANAGE"));
            authorities.add(new SimpleGrantedAuthority("ORDER_MANAGE_ALL"));
            authorities.add(new SimpleGrantedAuthority("UPLOAD_FILE"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ORDER_CREATE"));
            authorities.add(new SimpleGrantedAuthority("ORDER_VIEW_OWN"));
            authorities.add(new SimpleGrantedAuthority("CART_MANAGE"));
            authorities.add(new SimpleGrantedAuthority("PROFILE_MANAGE"));
        }

        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .enabled(user.getStatus() != null && user.getStatus() == 1)
                .build();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

