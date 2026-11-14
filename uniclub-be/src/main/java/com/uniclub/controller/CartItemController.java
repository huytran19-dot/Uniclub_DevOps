package com.uniclub.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uniclub.dto.request.CartItem.CreateCartItemRequest;
import com.uniclub.dto.request.CartItem.UpdateCartItemRequest;
import com.uniclub.dto.response.CartItem.CartItemResponse;
import com.uniclub.service.CartItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart-items")
@Validated
@CrossOrigin(origins = "*")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    // CREATE (Thêm sản phẩm vào giỏ hàng)
    @PostMapping
    public ResponseEntity<CartItemResponse> create(@Valid @RequestBody CreateCartItemRequest request) {
        CartItemResponse created = cartItemService.createCartItem(request);
        return ResponseEntity
                .created(URI.create("/api/cart-items/" + created.getId()))
                .body(created);
    }

    // UPDATE (Chống IDOR: Chỉ chủ sở hữu giỏ hàng chứa item đó hoặc SysAdmin)
    @PutMapping("/{id}")
    @PreAuthorize("@cartSecurity.isCartItemOwner(#id, principal)")
    public ResponseEntity<CartItemResponse> update(@PathVariable Integer id,
                                                @Valid @RequestBody UpdateCartItemRequest request) {
        CartItemResponse updated = cartItemService.updateCartItem(id, request);
        return ResponseEntity.ok(updated);
    }

    // GET ALL (Chỉ SysAdmin mới được xem toàn bộ cart items trong hệ thống)
    @GetMapping
    @PreAuthorize("hasRole('SysAdmin')")
    public ResponseEntity<List<CartItemResponse>> getAll() {
        return ResponseEntity.ok(cartItemService.getAllCartItems());
    }

    // GET BY ID (Chống IDOR: Chỉ chủ sở hữu item hoặc SysAdmin)
    @GetMapping("/{id}")
    @PreAuthorize("@cartSecurity.isCartItemOwner(#id, principal)")
    public ResponseEntity<CartItemResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(cartItemService.getCartItemById(id));
    }

    // GET BY CART ID (Chống IDOR: Chỉ chủ sở hữu giỏ hàng hoặc SysAdmin)
    @GetMapping("/cart/{cartId}")
    @PreAuthorize("@cartSecurity.isCartOwner(#cartId, principal)")
    public ResponseEntity<List<CartItemResponse>> getByCartId(@PathVariable Integer cartId) {
        return ResponseEntity.ok(cartItemService.getCartItemsByCartId(cartId));
    }

    // DELETE (Chống IDOR: Chỉ chủ sở hữu item hoặc SysAdmin)
    @DeleteMapping("/{id}")
    @PreAuthorize("@cartSecurity.isCartItemOwner(#id, principal)")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        cartItemService.deleteCartItem(id);
        return ResponseEntity.noContent().build();
    }
}
