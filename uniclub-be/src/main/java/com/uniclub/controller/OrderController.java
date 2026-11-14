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

import com.uniclub.dto.request.Order.CreateOrderRequest;
import com.uniclub.dto.response.Order.OrderResponse;
import com.uniclub.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@Validated
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // CREATE (Khách hàng đã đăng nhập tạo đơn hàng)
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse created = orderService.createOrder(request);
        return ResponseEntity
                .created(URI.create("/api/orders/" + created.getId()))
                .body(created);
    }

    // UPDATE
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SysAdmin') or @orderSecurity.isOwner(#id, principal)")
    public ResponseEntity<OrderResponse> update(@PathVariable Integer id,
                                                @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse updated = orderService.updateOrder(id, request);
        return ResponseEntity.ok(updated);
    }

    // GET ALL (Chỉ SysAdmin mới được xem toàn bộ đơn hàng trong hệ thống)
    @GetMapping
    @PreAuthorize("hasRole('SysAdmin')")
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
    
    // GET BY USER ID (Chống IDOR: Chỉ SysAdmin hoặc chính User đó mới được xem đơn hàng của mình)
    @GetMapping("/user/{userId}")
    @PreAuthorize("@orderSecurity.isUserOwner(#userId, principal)")
    public ResponseEntity<List<OrderResponse>> getByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    // GET BY ID (Chống IDOR: Chỉ SysAdmin hoặc chính chủ sở hữu đơn hàng)
    @GetMapping("/{id}")
    @PreAuthorize("@orderSecurity.isOwner(#id, principal)")
    public ResponseEntity<OrderResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // UPDATE ORDER STATUS (Chỉ SysAdmin mới được duyệt/đổi trạng thái đơn hàng)
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SysAdmin')")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Integer id,
                                                    @RequestBody CreateOrderRequest request) {
        try {
            OrderResponse updated = orderService.updateOrderStatus(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // CANCEL ORDER (Chống IDOR: Chỉ chủ sở hữu đơn hàng hoặc SysAdmin mới được hủy)
    @PutMapping("/{id}/cancel")
    @PreAuthorize("@orderSecurity.isOwner(#id, principal)")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer id) {
        try {
            OrderResponse cancelled = orderService.cancelOrder(id);
            return ResponseEntity.ok(cancelled);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Không thể hủy đơn hàng");
        }
    }

    // RETRY PAYMENT (Chống IDOR: Chỉ chủ sở hữu đơn hàng hoặc SysAdmin)
    @PostMapping("/{id}/retry-payment")
    @PreAuthorize("@orderSecurity.isOwner(#id, principal)")
    public ResponseEntity<?> retryPayment(@PathVariable Integer id) {
        try {
            String paymentUrl = orderService.retryVNPayPayment(id);
            return ResponseEntity.ok(paymentUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Không thể tạo lại thanh toán");
        }
    }

    // DELETE (Chỉ SysAdmin mới có quyền xóa đơn hàng)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SysAdmin')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
