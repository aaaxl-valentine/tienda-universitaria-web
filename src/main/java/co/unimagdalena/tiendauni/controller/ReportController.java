package co.unimagdalena.tiendauni.controller;

import co.unimagdalena.tiendauni.DTOs.CustomerDTOs.TopCustomerResponse;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.MonthlyIncomeResponse;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.OrderResponse;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.BestSellingProductResponse;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.LowStockProductResponse;
import co.unimagdalena.tiendauni.entity.enums.OrderStatus;
import co.unimagdalena.tiendauni.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Validated
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/low-stock-products")
    public ResponseEntity<List<LowStockProductResponse>> getLowStockProducts() {
        return ResponseEntity.ok(reportService.getLowStockProducts());
    }

    @GetMapping("/products-with-insufficient-stock")
    public ResponseEntity<List<LowStockProductResponse>> getProductsWithInsufficientStock() {
        return ResponseEntity.ok(reportService.getProductsWithInsufficientStock());
    }


    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getOrdersByFilters(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) BigDecimal minTotal,
            @RequestParam(required = false) BigDecimal maxTotal) {
        return ResponseEntity.ok(reportService.getOrdersByFilters(customerId, status, startDate, endDate, minTotal, maxTotal));
    }

    @GetMapping("/top-selling-products")
    public ResponseEntity<List<BestSellingProductResponse>> getTopSellingProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportService.getTopSellingProductsByPeriod(startDate, endDate));
    }

    @GetMapping("/best-selling-products")
    public ResponseEntity<List<BestSellingProductResponse>> getBestSellingProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportService.getTopSellingProductsByPeriod(startDate, endDate));
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<List<MonthlyIncomeResponse>> getMonthlyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportService.getMonthlyRevenue(startDate, endDate));
    }

    @GetMapping("/monthly-income")
    public ResponseEntity<List<MonthlyIncomeResponse>> getMonthlyIncome(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportService.getMonthlyRevenue(startDate, endDate));
    }

    @GetMapping("/top-customers")
    public ResponseEntity<List<TopCustomerResponse>> getTopCustomers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportService.getTopCustomersByRevenue(startDate, endDate));
    }
}
