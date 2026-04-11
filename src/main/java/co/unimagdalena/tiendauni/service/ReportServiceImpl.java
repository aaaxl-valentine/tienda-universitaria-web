package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.entity.Inventory;
import co.unimagdalena.tiendauni.entity.Order;
import co.unimagdalena.tiendauni.entity.Product;
import co.unimagdalena.tiendauni.enums.OrderStatus;
import co.unimagdalena.tiendauni.repository.InventoryRepository;
import co.unimagdalena.tiendauni.repository.OrderRepository;
import co.unimagdalena.tiendauni.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Inventory> getLowStockProducts() {
        return inventoryRepository.findLowStockInventories();
    }

    @Override
    public List<Inventory> getProductsWithInsufficientStock() {
        return inventoryRepository.findProductsWithInsufficientStock();
    }

    @Override
    public List<Order> getOrdersByFilters(Long customerId,
                                          OrderStatus status,
                                          LocalDateTime startDate,
                                          LocalDateTime endDate,
                                          BigDecimal minTotal,
                                          BigDecimal maxTotal) {
        return orderRepository.findOrdersByFilters(customerId, status, startDate, endDate, minTotal, maxTotal);
    }

    @Override
    public List<Product> getTopSellingProductsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return productRepository.findTopSellingProductsByPeriod(startDate, endDate);
    }

    @Override
    public List<Object[]> getMonthlyRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findMonthlyRevenue(startDate, endDate);
    }

    @Override
    public List<Object[]> getTopCustomersByRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findTopCustomersByRevenue(startDate, endDate);
    }
}