package edu.icet.orderservice.controller;

import edu.icet.orderservice.dto.Response;
import edu.icet.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<Response> createOrder(@PathVariable Long userId) {
        Response response = orderService.createOrder(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Response> getAllOrders() {
        Response response = orderService.getAllOrders();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-order-by-id/{orderId}")
    public ResponseEntity<Response> getOrderById(@PathVariable Long orderId) {
        Response response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-order-history/{userId}")
    public ResponseEntity<Response> getUserOrderHistory(@PathVariable Long userId) {
        Response response = orderService.getUserOrderHistory(userId);
        return ResponseEntity.ok(response);
    }
}
