package co.unimagdalena.tiendauni.controlador;

import co.unimagdalena.tiendauni.DTOs.ProductDTOs.LowStockProductResponse;
import co.unimagdalena.tiendauni.NotFoundException.GlobalExceptionHandler;
import co.unimagdalena.tiendauni.controller.ReportController;
import co.unimagdalena.tiendauni.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @Test
    void lowStockReportReturnsOk() throws Exception {
        when(reportService.getLowStockProducts())
                .thenReturn(List.of(new LowStockProductResponse(1L, "SKU-1", "Producto 1", 3, 5)));

        mockMvc.perform(get("/api/reports/low-stock-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1L));
    }

    @Test
    void bestSellingAliasReturnsOk() throws Exception {
        when(reportService.getTopSellingProductsByPeriod(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/reports/best-selling-products")
                        .param("startDate", "2026-01-01T00:00:00")
                        .param("endDate", "2026-01-31T23:59:59"))
                .andExpect(status().isOk());
    }
}
