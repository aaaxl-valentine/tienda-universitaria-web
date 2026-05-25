package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.CustomerDTOs.TopCustomerResponse;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.MonthlyIncomeResponse;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.OrderResponse;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.BestSellingProductResponse;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.LowStockProductResponse;
import co.unimagdalena.tiendauni.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {


    List<LowStockProductResponse> getLowStockProducts();

    default List<LowStockProductResponse> getProductsWithInsufficientStock() {
        return getLowStockProducts();
    }


    List<OrderResponse> getOrdersByFilters(Long customerId,
                                           OrderStatus status,
                                           LocalDateTime startDate,
                                           LocalDateTime endDate,
                                           BigDecimal minTotal,
                                           BigDecimal maxTotal);

    List<BestSellingProductResponse> getTopSellingProductsByPeriod(LocalDateTime startDate, LocalDateTime endDate);

    List<MonthlyIncomeResponse> getMonthlyRevenue(LocalDateTime startDate, LocalDateTime endDate);

    List<TopCustomerResponse> getTopCustomersByRevenue(LocalDateTime startDate, LocalDateTime endDate);
}
