package co.unimagdalena.tiendauni.controlador;

import co.unimagdalena.tiendauni.DTOs.CustomerDTOs.TopCustomerResponse;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.MonthlyIncomeResponse;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.OrderResponse;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.BestSellingProductResponse;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.LowStockProductResponse;
import co.unimagdalena.tiendauni.controller.ReportController;
import co.unimagdalena.tiendauni.entity.enums.OrderStatus;
import co.unimagdalena.tiendauni.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    @Test
    void getLowStockProducts_ShouldReturnOk() {
        var response = new LowStockProductResponse(1L, "SKU1", "Product 1", 5, 10);
        when(reportService.getLowStockProducts()).thenReturn(List.of(response));

        var result = reportController.getLowStockProducts();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
        assertThat(result.getBody().getFirst().productId()).isEqualTo(1L);
    }

    @Test
    void getProductsWithInsufficientStock_ShouldReturnOk() {
        var response = new LowStockProductResponse(2L, "SKU2", "Product 2", 2, 5);
        when(reportService.getProductsWithInsufficientStock()).thenReturn(List.of(response));

        var result = reportController.getProductsWithInsufficientStock();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert result.getBody() != null;
        assertThat(result.getBody().getFirst().productId()).isEqualTo(2L);
    }

    @Test
    void getOrdersByFilters_ShouldReturnOk() {
        var response = new OrderResponse(1L, OrderStatus.PAID, new BigDecimal("100.00"), LocalDateTime.now(), LocalDateTime.now(), 1L, 1L);
        when(reportService.getOrdersByFilters(any(), any(), any(), any(), any(), any())).thenReturn(List.of(response));

        var result = reportController.getOrdersByFilters(1L, OrderStatus.PAID, null, null, null, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert result.getBody() != null;
        assertThat(result.getBody().getFirst().id()).isEqualTo(1L);
    }

    @Test
    void getTopSellingProducts_ShouldReturnOk() {
        var response = new BestSellingProductResponse(1L, "SKU1", "Product 1", 100L);
        when(reportService.getTopSellingProductsByPeriod(any(), any())).thenReturn(List.of(response));

        var result = reportController.getTopSellingProducts(LocalDateTime.now(), LocalDateTime.now());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert result.getBody() != null;
        assertThat(result.getBody().getFirst().productId()).isEqualTo(1L);
    }

    @Test
    void getMonthlyRevenue_ShouldReturnOk() {
        var response = new MonthlyIncomeResponse(YearMonth.of(2023, 1), new BigDecimal("5000.00"));
        when(reportService.getMonthlyRevenue(any(), any())).thenReturn(List.of(response));

        var result = reportController.getMonthlyRevenue(LocalDateTime.now(), LocalDateTime.now());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert result.getBody() != null;
        assertThat(result.getBody().getFirst().totalIncome()).isEqualTo(new BigDecimal("5000.00"));
    }

    @Test
    void getTopCustomers_ShouldReturnOk() {
        var response = new TopCustomerResponse(1L, "John Doe", "john@example.com", new BigDecimal("1000.00"));
        when(reportService.getTopCustomersByRevenue(any(), any())).thenReturn(List.of(response));

        var result = reportController.getTopCustomers(LocalDateTime.now(), LocalDateTime.now());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert result.getBody() != null;
        assertThat(result.getBody().getFirst().customerId()).isEqualTo(1L);
    }
}
