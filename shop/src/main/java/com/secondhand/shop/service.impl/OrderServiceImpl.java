package com.secondhand.shop.service.impl;

import com.secondhand.shop.dto.OrderDTO;
import com.secondhand.shop.dto.OrderItemDTO;
import com.secondhand.shop.model.*;
import com.secondhand.shop.repository.*;
import com.secondhand.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return OrderDTO.fromEntity(order);
    }

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        // Validate customer
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + orderDTO.getCustomerId()));

        // Generate order code
        String orderCode = generateOrderCode();

        // Create order
        Order order = Order.builder()
                .orderCode(orderCode)
                .customer(customer)
                .totalAmount(orderDTO.getTotalAmount())
                .discountAmount(orderDTO.getDiscountAmount())
                .finalAmount(orderDTO.getFinalAmount())
                .status(Order.OrderStatus.PENDING)
                .shippingAddress(orderDTO.getShippingAddress())
                .shippingPhone(orderDTO.getShippingPhone())
                .note(orderDTO.getNote())
                .build();

        Order savedOrder = orderRepository.save(order);

        // Create order items
        if (orderDTO.getOrderItems() != null && !orderDTO.getOrderItems().isEmpty()) {
            for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
                Product product = productRepository.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemDTO.getProductId()));

                OrderItem orderItem = OrderItem.builder()
                        .order(savedOrder)
                        .product(product)
                        .productName(product.getName())
                        .price(itemDTO.getPrice())
                        .quantity(itemDTO.getQuantity())
                        .subtotal(itemDTO.getSubtotal())
                        .build();

                orderItemRepository.save(orderItem);

                // Update product quantity
                if (product.getQuantity() >= itemDTO.getQuantity()) {
                    product.setQuantity(product.getQuantity() - itemDTO.getQuantity());
                    if (product.getQuantity() == 0) {
                        product.setStatus(Product.ProductStatus.SOLD);
                    }
                    productRepository.save(product);
                }
            }
        }

        // Update customer stats
        customer.setTotalOrders(customer.getTotalOrders() + 1);
        customerRepository.save(customer);

        return OrderDTO.fromEntity(orderRepository.findById(savedOrder.getId()).get());
    }

    @Override
    public OrderDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        order.setStatus(Order.OrderStatus.valueOf(status));

        // ✅ SỬA: Dùng phép cộng Double thay vì BigDecimal
        if (status.equals("DELIVERED")) {
            Customer customer = order.getCustomer();
            customer.setTotalSpent(customer.getTotalSpent() + order.getFinalAmount());
            customerRepository.save(customer);
        }

        Order updatedOrder = orderRepository.save(order);
        return OrderDTO.fromEntity(updatedOrder);
    }

    @Override
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        // Only allow cancel if order is PENDING or CONFIRMED
        if (order.getStatus() == Order.OrderStatus.PENDING ||
                order.getStatus() == Order.OrderStatus.CONFIRMED) {

            order.setStatus(Order.OrderStatus.CANCELLED);

            // Return products to inventory
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                product.setStatus(Product.ProductStatus.AVAILABLE);
                productRepository.save(product);
            }

            orderRepository.save(order);
        } else {
            throw new RuntimeException("Cannot cancel order with status: " + order.getStatus());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(Order.OrderStatus.valueOf(status)).stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Generate unique order code
    private String generateOrderCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long count = orderRepository.count() + 1;
        return "ORD" + timestamp + String.format("%03d", count);
    }
}