package edu.icet.controller;

import edu.icet.dto.ProductDto;
import edu.icet.dto.StatusUpdateDto;
import edu.icet.dto.UserDto;
import edu.icet.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/send-status-email")
    public ResponseEntity<?> sendStatusUpdateEmail(@RequestBody StatusUpdateDto request) {
        try {

            String orderItemId = request.getOrderItemId();
            String status = request.getStatus();
            UserDto user = request.getUser();
            ProductDto product = request.getProduct();

            // Send email to the user
            emailService.sendOrderStatusUpdateEmail(
                    user.getEmail(),
                    user.getName(),
                    orderItemId,
                    status,
                    product.getName(),
                    product.getPrice().doubleValue()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Email notification sent successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Failed to send email notification: " + e.getMessage());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}