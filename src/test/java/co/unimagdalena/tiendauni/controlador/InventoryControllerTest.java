package co.unimagdalena.tiendauni.controlador;

import co.unimagdalena.tiendauni.DTOs.InventoryDTOs.InventoryResponse;
import co.unimagdalena.tiendauni.NotFoundException.ResourceNotFoundException;
import co.unimagdalena.tiendauni.controller.InventoryController;
import co.unimagdalena.tiendauni.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private InventoryResponse testInventory;

    @BeforeEach
    void setUp() {
        testInventory = new InventoryResponse(
                1L,
                50,      // availableStock
                10,      // minimumStock
                LocalDateTime.now(),
                1L       // productId
        );
    }

    @Test
    void testGetInventoryByProductId_Success() {
        // Arrange
        when(inventoryService.findByProductId(1L))
                .thenReturn(Optional.of(testInventory));

        // Act
        ResponseEntity<InventoryResponse> response = inventoryController.getInventoryByProductId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testInventory.id(), response.getBody().id());
        assertEquals(50, response.getBody().availableStock());
        verify(inventoryService, times(1)).findByProductId(1L);
    }

    @Test
    void testGetInventoryByProductId_NotFound() {
        // Arrange
        when(inventoryService.findByProductId(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                inventoryController.getInventoryByProductId(999L)
        );
        verify(inventoryService, times(1)).findByProductId(999L);
    }

    @Test
    void testGetInventoryBySku_Success() {
        // Arrange
        when(inventoryService.findByProductSku("LAPTOP-001"))
                .thenReturn(Optional.of(testInventory));

        // Act
        ResponseEntity<InventoryResponse> response = inventoryController.getInventoryBySku("LAPTOP-001");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testInventory.id(), response.getBody().id());
        verify(inventoryService, times(1)).findByProductSku("LAPTOP-001");
    }

    @Test
    void testGetInventoryBySku_NotFound() {
        // Arrange
        when(inventoryService.findByProductSku("INVALID-SKU"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                inventoryController.getInventoryBySku("INVALID-SKU")
        );
    }

    @Test
    void testGetLowStockInventories_Success() {
        // Arrange
        InventoryResponse lowStock = new InventoryResponse(
                2L, 5, 10, LocalDateTime.now(), 2L
        );
        when(inventoryService.getLowStockInventories())
                .thenReturn(List.of(lowStock));

        // Act
        ResponseEntity<List<InventoryResponse>> response = inventoryController.getLowStockInventories();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(5, response.getBody().get(0).availableStock());
        verify(inventoryService, times(1)).getLowStockInventories();
    }

    @Test
    void testGetLowStockInventories_Empty() {
        // Arrange
        when(inventoryService.getLowStockInventories())
                .thenReturn(List.of());

        // Act
        ResponseEntity<List<InventoryResponse>> response = inventoryController.getLowStockInventories();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testCreateInventory_Success() {
        // Arrange
        when(inventoryService.createInventoryForProduct(1L, 50, 10))
                .thenReturn(testInventory);

        // Act
        ResponseEntity<InventoryResponse> response = inventoryController.createInventory(1L, 50, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(50, response.getBody().availableStock());
        verify(inventoryService, times(1)).createInventoryForProduct(1L, 50, 10);
    }

    @Test
    void testUpdateStock_Success() {
        // Arrange
        InventoryResponse updatedInventory = new InventoryResponse(
                1L, 100, 10, LocalDateTime.now(), 1L
        );
        when(inventoryService.updateAvailableStock(1L, 100))
                .thenReturn(updatedInventory);

        // Act
        ResponseEntity<InventoryResponse> response = inventoryController.updateStock(1L, 100);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100, response.getBody().availableStock());
        verify(inventoryService, times(1)).updateAvailableStock(1L, 100);
    }

    @Test
    void testUpdateMinimumStock_Success() {
        // Arrange
        InventoryResponse updatedInventory = new InventoryResponse(
                1L, 50, 20, LocalDateTime.now(), 1L
        );
        when(inventoryService.updateMinimumStock(1L, 20))
                .thenReturn(updatedInventory);

        // Act
        ResponseEntity<InventoryResponse> response = inventoryController.updateMinimumStock(1L, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(20, response.getBody().minimumStock());
        verify(inventoryService, times(1)).updateMinimumStock(1L, 20);
    }

    @Test
    void testIncrementStock_Success() {
        // Arrange
        InventoryResponse incrementedInventory = new InventoryResponse(
                1L, 70, 10, LocalDateTime.now(), 1L
        );
        when(inventoryService.incrementStock(1L, 20))
                .thenReturn(incrementedInventory);

        // Act
        ResponseEntity<InventoryResponse> response = inventoryController.incrementStock(1L, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(70, response.getBody().availableStock());
        verify(inventoryService, times(1)).incrementStock(1L, 20);
    }

    @Test
    void testDecrementStock_Success() {
        // Arrange
        InventoryResponse decrementedInventory = new InventoryResponse(
                1L, 30, 10, LocalDateTime.now(), 1L
        );
        when(inventoryService.decrementStock(1L, 20))
                .thenReturn(decrementedInventory);

        // Act
        ResponseEntity<InventoryResponse> response = inventoryController.decrementStock(1L, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(30, response.getBody().availableStock());
        verify(inventoryService, times(1)).decrementStock(1L, 20);
    }

    @Test
    void testIsLowStock_True() {
        // Arrange
        when(inventoryService.isLowStock(1L))
                .thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = inventoryController.isLowStock(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(inventoryService, times(1)).isLowStock(1L);
    }

    @Test
    void testIsLowStock_False() {
        // Arrange
        when(inventoryService.isLowStock(1L))
                .thenReturn(false);

        // Act
        ResponseEntity<Boolean> response = inventoryController.isLowStock(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
        verify(inventoryService, times(1)).isLowStock(1L);
    }

    @Test
    void testGetAvailableStock_Success() {
        // Arrange
        when(inventoryService.getAvailableStock(1L))
                .thenReturn(50);

        // Act
        ResponseEntity<Integer> response = inventoryController.getAvailableStock(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(50, response.getBody());
        verify(inventoryService, times(1)).getAvailableStock(1L);
    }
}
