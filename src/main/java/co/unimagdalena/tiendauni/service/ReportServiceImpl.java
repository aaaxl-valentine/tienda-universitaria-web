package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.CustomerDTOs.TopCustomerResponse;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.MonthlyIncomeResponse;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.OrderResponse;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.BestSellingProductResponse;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.LowStockProductResponse;
import co.unimagdalena.tiendauni.entity.Customer;
import co.unimagdalena.tiendauni.entity.Inventory;
import co.unimagdalena.tiendauni.entity.Product;
import co.unimagdalena.tiendauni.enums.OrderStatus;
import co.unimagdalena.tiendauni.repository.InventoryRepository;
import co.unimagdalena.tiendauni.repository.OrderItemRepository;
import co.unimagdalena.tiendauni.repository.OrderRepository;
import co.unimagdalena.tiendauni.service.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public List<LowStockProductResponse> getLowStockProducts() {
        return inventoryRepository.findLowStockInventories().stream()
                .map(this::toLowStockProductResponse)
                .toList();
    }

    @Override
    public List<LowStockProductResponse> getProductsWithInsufficientStock() {
        return inventoryRepository.findProductsWithInsufficientStock().stream()
                .map(this::toLowStockProductResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> getOrdersByFilters(Long customerId,
                                                  OrderStatus status,
                                                  LocalDateTime startDate,
                                                  LocalDateTime endDate,
                                                  BigDecimal minTotal,
                                                  BigDecimal maxTotal) {
        return orderRepository.findOrdersByFilters(customerId, status, startDate, endDate, minTotal, maxTotal)
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Override
    public List<BestSellingProductResponse> getTopSellingProductsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return orderItemRepository.findTopSellingProductsWithQuantities(startDate, endDate).stream()
                .map(row -> {
                    Product product = (Product) row[0];
                    Long totalUnitsSold = ((Number) row[1]).longValue();
                    return new BestSellingProductResponse(
                            product.getId(),
                            product.getSku(),
                            product.getName(),
                            totalUnitsSold
                    );
                })
                .toList();
    }

    @Override
    public List<MonthlyIncomeResponse> getMonthlyRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findMonthlyRevenue(startDate, endDate).stream()
                .map(row -> new MonthlyIncomeResponse(
                        YearMonth.of(((Number) row[0]).intValue(), ((Number) row[1]).intValue()),
                        (BigDecimal) row[2]
                ))
                .toList();
    }

    @Override
    public List<TopCustomerResponse> getTopCustomersByRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findTopCustomersByRevenue(startDate, endDate).stream()
                .map(row -> {
                    Customer customer = (Customer) row[0];
                    BigDecimal totalRevenue = (BigDecimal) row[1];
                    String fullName = (customer.getFirstName() + " " + customer.getLastName()).trim();
                    return new TopCustomerResponse(
                            customer.getId(),
                            fullName,
                            customer.getEmail(),
                            totalRevenue
                    );
                })
                .toList();
    }

    private LowStockProductResponse toLowStockProductResponse(Inventory inventory) {
        Product product = inventory.getProduct();
        return new LowStockProductResponse(
                product != null ? product.getId() : null,
                product != null ? product.getSku() : null,
                product != null ? product.getName() : null,
                inventory.getAvailableStock(),
                inventory.getMinimumStock()
        );
    }
}