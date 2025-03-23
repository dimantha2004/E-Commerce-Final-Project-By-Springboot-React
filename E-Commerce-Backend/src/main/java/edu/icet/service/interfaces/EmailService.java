package edu.icet.service.interfaces;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp);

    void sendOrderConfirmation(String toEmail, String subject, String content);

    void sendOrderStatusUpdateEmail(String toEmail, String userName, String orderItemId,
                                    String status, String productName, double price);
}
