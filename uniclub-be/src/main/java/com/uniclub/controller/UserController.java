package com.uniclub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uniclub.dto.request.User.CreateUserRequest;
import com.uniclub.dto.request.User.UpdateUserRequest;
import com.uniclub.dto.response.User.UserResponse;
import com.uniclub.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // CREATE (Chỉ SysAdmin mới được tạo tài khoản trực tiếp qua endpoint này)
    @PostMapping
    @PreAuthorize("hasRole('SysAdmin')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserResponse created = userService.createUser(request);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // UPDATE (Chống IDOR: Chỉ SysAdmin HOẶC chính chủ sở hữu tài khoản mới được cập nhật)
    @PutMapping("/{id}")
    @PreAuthorize("@userSecurity.isCurrentUser(#id, principal)")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @Valid @RequestBody UpdateUserRequest request) {
        try {
            UserResponse updated = userService.updateUser(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // GET ALL (Chỉ SysAdmin mới được lấy danh sách toàn bộ người dùng)
    @GetMapping
    @PreAuthorize("hasRole('SysAdmin')")
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET BY ID (Chống IDOR: Chỉ SysAdmin HOẶC chính chủ sở hữu tài khoản)
    @GetMapping("/{id}")
    @PreAuthorize("@userSecurity.isCurrentUser(#id, principal)")
    public ResponseEntity<UserResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // DELETE (Chỉ SysAdmin mới có quyền xóa người dùng)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SysAdmin')")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi xóa người dùng: " + e.getMessage());
        }
    }
}
