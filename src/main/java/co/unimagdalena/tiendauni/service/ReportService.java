package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.entity.Inventory;
import co.unimagdalena.tiendauni.entity.Order;
import co.unimagdalena.tiendauni.entity.Product;
import co.unimagdalena.tiendauni.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {

    List<Inventory> getLowStockProducts();

    List<Inventory> getProductsWithInsufficientStock();

    List<Order> getOrdersByFilters(Long customerId,
                                   OrderStatus status,
                                   LocalDateTime startDate,
                                   LocalDateTime endDate,
                                   BigDecimal minTotal,
                                   BigDecimal maxTotal);

    List<Product> getTopSellingProductsByPeriod(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getMonthlyRevenue(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getTopCustomersByRevenue(LocalDateTime startDate, LocalDateTime endDate);
}