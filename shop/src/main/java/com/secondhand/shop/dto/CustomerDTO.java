package com.secondhand.shop.dto;

import com.secondhand.shop.model.Customer;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {

    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String district;
    private String ward;
    private Integer totalOrders;
    private Double totalSpent;  // ✅ ĐỔI

    public static CustomerDTO fromEntity(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .userId(customer.getUser().getId())
                .username(customer.getUser().getUsername())
                .fullName(customer.getUser().getFullName())
                .email(customer.getUser().getEmail())
                .phone(customer.getUser().getPhone())
                .address(customer.getAddress())
                .city(customer.getCity())
                .district(customer.getDistrict())
                .ward(customer.getWard())
                .totalOrders(customer.getTotalOrders())
                .totalSpent(customer.getTotalSpent() != null ? customer.getTotalSpent().doubleValue() : 0.0)
                .build();
    }
}