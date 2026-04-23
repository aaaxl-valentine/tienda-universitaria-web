package co.unimagdalena.tiendauni.controller;

import co.unimagdalena.tiendauni.DTOs.OrderDTOs.CancelOrderRequest;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.CreateOrderRequest;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.OrderResponse;
import co.unimagdalena.tiendauni.entity.OrderStatusHistory;
import co.unimagdalena.tiendauni.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest req,
                                                UriComponentsBuilder uriBuilder) {
        var orderCreated = orderService.createOrder(req);
        var location = uriBuilder.path("/api/orders/{id}").buildAndExpand(orderCreated.id()).toUri();
        return ResponseEntity.created(location).body(orderCreated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(@PathVariable long customerId) {
        return ResponseEntity.ok(orderService.findByCustomerId(customerId));
    }

    @GetMapping("/{id}/total")
    public ResponseEntity<BigDecimal> calculateTotal(@PathVariable long id) {
        return ResponseEntity.ok(orderService.calculateOrderTotal(id));
    }

    @PostMapping("/{id}/process-payment")
    public ResponseEntity<OrderResponse> processPayment(@PathVariable long id) {
        return ResponseEntity.ok(orderService.processPayment(id));
    }

    @PostMapping("/{id}/ship")
    public ResponseEntity<OrderResponse> shipOrder(@PathVariable long id) {
        return ResponseEntity.ok(orderService.shipOrder(id));
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<OrderResponse> deliverOrder(@PathVariable long id) {
        return ResponseEntity.ok(orderService.deliverOrder(id));
    }

    @PostMapping("/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@Valid @RequestBody CancelOrderRequest req) {
        return ResponseEntity.ok(orderService.cancelOrder(req));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<OrderStatusHistory>> getOrderHistory(@PathVariable long id) {
        return ResponseEntity.ok(orderService.getOrderHistory(id));
    }
}
