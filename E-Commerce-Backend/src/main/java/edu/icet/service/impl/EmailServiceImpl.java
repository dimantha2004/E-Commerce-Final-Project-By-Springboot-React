package edu.icet.service.impl;

import edu.icet.service.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Password Reset OTP");
            message.setText("Your OTP is: " + otp + "\nValid for 5 minutes");
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }

    @Override
    public void sendOrderConfirmation(String toEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("noreply@yourstore.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true); // true indicates HTML

            log.info("Attempting to send email to: {}", toEmail);
            mailSender.send(message);
            log.info("Email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}. Error: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendOrderStatusUpdateEmail(String toEmail, String userName, String orderItemId,
                                           String status, String productName, double price) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("noreply@yourstore.com");
            helper.setTo(toEmail);
            helper.setSubject("Order Status Update: " + status);

            String emailContent = buildOrderStatusUpdateEmailContent(
                    userName, orderItemId, status, productName, price
            );

            helper.setText(emailContent, true);

            log.info("Attempting to send status update email to: {}", toEmail);
            mailSender.send(message);
            log.info("Status update email sent successfully to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send status update email to: {}. Error: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send status update email", e);
        }
    }

    private String buildOrderStatusUpdateEmailContent(String userName, String orderItemId,
                                                      String status, String productName, double price) {

        return "<html>"
                + "<body style='font-family: Arial, sans-serif;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e1e1e1;'>"
                + "<h2 style='color: #333;'>Order Status Update</h2>"
                + "<p>Hello " + userName + ",</p>"
                + "<p>Your order <strong>#" + orderItemId + "</strong> status has been updated to <strong>" + status + "</strong>.</p>"
                + "<div style='background-color: #f9f9f9; padding: 15px; margin: 20px 0;'>"
                + "<h3 style='margin-top: 0;'>Order Details</h3>"
                + "<p><strong>Product:</strong> " + productName + "</p>"
                + "<p><strong>Price:</strong> $" + String.format("%.2f", price) + "</p>"
                + "<p><strong>Order ID:</strong> " + orderItemId + "</p>"
                + "<p><strong>Status:</strong> " + status + "</p>"
                + "</div>"
                + getStatusSpecificMessage(status)
                + "<p>Thank you for shopping with us!</p>"
                + "<p>Best regards,<br>The Support Team</p>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

    private String getStatusSpecificMessage(String status) {
        switch (status) {
            case "CONFIRMED":
                return "<p>Your order has been confirmed and is being processed. We'll notify you once it's shipped.</p>";
            case "SHIPPED":
                return "<p>Great news! Your order is on its way. You can track your package using your order number.</p>";
            case "DELIVERED":
                return "<p>Your order has been delivered. We hope you enjoy your purchase!</p>";
            case "CANCELLED":
                return "<p>Your order has been cancelled as requested. If you have any questions, please contact our support team.</p>";
            case "RETURNED":
                return "<p>Your return has been processed. Any refund will be issued according to our return policy.</p>";
            default:
                return "";
        }
    }
}

