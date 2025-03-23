package edu.icet.service.impl;

import edu.icet.dto.OrderItemDto;
import edu.icet.dto.OrderRequest;
import edu.icet.dto.Response;
import edu.icet.entity.Order;
import edu.icet.entity.OrderItem;
import edu.icet.entity.Product;
import edu.icet.entity.User;
import edu.icet.enums.OrderStatus;
import edu.icet.exception.InvalidOperationException;
import edu.icet.exception.NotFoundException;
import edu.icet.mapper.EntityDtoMapper;
import edu.icet.repository.OrderItemRepository;
import edu.icet.repository.OrderRepository;
import edu.icet.repository.ProductRepository;
import edu.icet.service.interfaces.EmailService;
import edu.icet.service.interfaces.OrderItemService;
import edu.icet.service.interfaces.UserService;
import edu.icet.specification.OrderItemSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final EntityDtoMapper entityDtoMapper;
    private final EmailService emailService;

    @Override
    public Response placeOrder(OrderRequest orderRequest) {
        User user = userService.getLoginUser();

        List<OrderItem> orderItems = orderRequest.getItems().stream().map(orderItemRequest -> {
            Product product = productRepository.findById(orderItemRequest.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product Not Found"));

            if (product.getQuantity() < orderItemRequest.getQuantity()) {
                throw new InvalidOperationException("Not enough stock for product: " + product.getName());
            }

            product.setQuantity(product.getQuantity() - orderItemRequest.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(orderItemRequest.getQuantity());
            orderItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(orderItemRequest.getQuantity())));
            orderItem.setStatus(OrderStatus.PENDING);
            orderItem.setUser(user);
            return orderItem;
        }).collect(Collectors.toList());

        BigDecimal totalPrice = orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setOrderItemList(orderItems);
        order.setTotalprice(totalPrice);
        orderItems.forEach(orderItem -> orderItem.setOrder(order));
        orderRepository.save(order);
        try {
            String emailContent = buildEmailContent(orderItems, totalPrice, user);
            emailService.sendOrderConfirmation(
                    user.getEmail(),
                    "Order Confirmation #" + order.getId(),
                    emailContent
            );
        } catch (Exception e) {
            log.error("Failed to send confirmation email for order {}", order.getId(), e);
        }

        return Response.builder()
                .status(200)
                .message("Order was successfully placed")
                .build();
    }

    private String buildEmailContent(List<OrderItem> items, BigDecimal total, User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h2>Thank you for your order, ").append(user.getName()).append("!</h2>");
        sb.append("<h3>Order Details:</h3>");
        sb.append("<table border='1' cellpadding='5'>");
        sb.append("<tr><th>Product</th><th>Quantity</th><th>Price</th></tr>");

        items.forEach(item -> {
            sb.append("<tr>")
                    .append("<td>").append(item.getProduct().getName()).append("</td>")
                    .append("<td>").append(item.getQuantity()).append("</td>")
                    .append("<td>$").append(item.getPrice()).append("</td>")
                    .append("</tr>");
        });

        sb.append("</table>");
        sb.append("<h4>Total: $").append(total).append("</h4>");
        sb.append("<p>Your order status: ").append(OrderStatus.PENDING).append("</p>");
        sb.append("<p>We'll notify you when your items ship.</p>");
        sb.append("</body></html>");

        return sb.toString();
    }

    @Override
    public Response updateOrderItemStatus(Long orderItemId, String status) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(()-> new NotFoundException("Order Item not found"));

        orderItem.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        orderItemRepository.save(orderItem);
        return Response.builder()
                .status(200)
                .message("Order status updated successfully")
                .build();
    }

    @Override
    public Response filterOrderItems(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate, Long itemId, Pageable pageable) {
        Specification<OrderItem> spec = Specification.where(OrderItemSpecification.hasStatus(status))
                .and(OrderItemSpecification.createdBetween(startDate, endDate))
                .and(OrderItemSpecification.hasItemId(itemId));

        Page<OrderItem> orderItemPage = orderItemRepository.findAll(spec, pageable);

        if (orderItemPage.isEmpty()){
            throw new NotFoundException("No Order Found");
        }
        List<OrderItemDto> orderItemDtos = orderItemPage.getContent().stream()
                .map(entityDtoMapper::mapOrderItemToDtoPlusProductAndUser)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .orderItemList(orderItemDtos)
                .totalPage(orderItemPage.getTotalPages())
                .totalElement(orderItemPage.getTotalElements())
                .build();
    }

}
