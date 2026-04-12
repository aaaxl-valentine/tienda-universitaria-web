package co.unimagdalena.tiendauni.service.mapper;

import co.unimagdalena.tiendauni.DTOs.OrderDTOs.CreateOrderRequest;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.OrderResponse;
import co.unimagdalena.tiendauni.entity.Address;
import co.unimagdalena.tiendauni.entity.Customer;
import co.unimagdalena.tiendauni.entity.Order;
import co.unimagdalena.tiendauni.enums.OrderStatus;

import java.math.BigDecimal;

public class OrderMapper {

    public static Order toEntity(CreateOrderRequest request, Customer customer, Address address) {
        return Order.builder()
                .status(request.status() != null ? request.status() : OrderStatus.CREATED)
                .total(BigDecimal.ZERO)
                .customer(customer)
                .address(address)
                .build();
    }

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotal(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getCustomer() != null ? order.getCustomer().getId() : null,
                order.getAddress() != null ? order.getAddress().getId() : null
        );
    }
}
