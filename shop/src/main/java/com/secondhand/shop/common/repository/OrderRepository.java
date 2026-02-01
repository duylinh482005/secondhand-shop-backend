package com.secondhand.shop.common.repository;

import com.secondhand.shop.common.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByStatus(Order.OrderStatus status);

    List<Order> findByCustomerIdAndStatus(Long customerId, Order.OrderStatus status);

    // Đếm số đơn hàng theo customer
    long countByCustomerId(Long customerId);

    @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.customer.id = :customerId AND o.status = 'DELIVERED'")
    Double calculateTotalSpentByCustomer(@Param("customerId") Long customerId);
}