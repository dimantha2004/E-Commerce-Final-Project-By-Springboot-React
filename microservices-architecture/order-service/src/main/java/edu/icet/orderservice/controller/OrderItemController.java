package edu.icet.orderservice.controller;

import edu.icet.orderservice.dto.OrderItemDto;
import edu.icet.orderservice.dto.Response;
import edu.icet.orderservice.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PostMapping("/place-order")
    public ResponseEntity<Response> placeOrder(@RequestBody OrderItemDto orderItemDto) {
        Response response = orderItemService.placeOrder(orderItemDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-status/{orderItemId}")
    public ResponseEntity<Response> updateOrderItemStatus(@PathVariable Long orderItemId, @RequestParam String status) {
        Response response = orderItemService.updateOrderItemStatus(orderItemId, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/{status}")
    public ResponseEntity<Response> filterOrderItems(@PathVariable String status) {
        Response response = orderItemService.filterOrderItems(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Response> getAllOrderItems() {
        Response response = orderItemService.getAllOrderItems();
        return ResponseEntity.ok(response);
    }
}
