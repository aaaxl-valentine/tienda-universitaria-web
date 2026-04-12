package co.unimagdalena.tiendauni.Servicios;

import co.unimagdalena.tiendauni.DTOs.InventoryDTOs.InventoryResponse;
import co.unimagdalena.tiendauni.entity.Inventory;
import co.unimagdalena.tiendauni.entity.Product;
import co.unimagdalena.tiendauni.repository.InventoryRepository;
import co.unimagdalena.tiendauni.repository.ProductRepository;
import co.unimagdalena.tiendauni.service.InventoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InventoryServiceImpl service;

    @Test
    void shouldCreateInventoryForProduct() {
        Product product = Product.builder().id(10L).sku("SKU-10").name("Lapicero").build();
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(10L)).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> {
            Inventory inventory = invocation.getArgument(0);
            inventory.setId(99L);
            return inventory;
        });

        InventoryResponse response = service.createInventoryForProduct(10L, 15, 5);

        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.availableStock()).isEqualTo(15);
        assertThat(response.minimumStock()).isEqualTo(5);
        assertThat(response.productId()).isEqualTo(10L);
    }

    @Test
    void shouldRejectDecrementWhenStockIsInsufficient() {
        Inventory inventory = Inventory.builder()
                .id(1L)
                .availableStock(3)
                .minimumStock(1)
                .build();
        when(inventoryRepository.findByProductId(20L)).thenReturn(Optional.of(inventory));

        assertThatThrownBy(() -> service.decrementStock(20L, 4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient stock");

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void shouldDecrementStockSuccessfully() {
        Inventory inventory = Inventory.builder()
                .id(1L)
                .availableStock(10)
                .minimumStock(2)
                .build();
        Product product = Product.builder().id(20L).build();
        inventory.setProduct(product);

        when(inventoryRepository.findByProductId(20L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryResponse response = service.decrementStock(20L, 4);

        assertThat(response.availableStock()).isEqualTo(6);
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void shouldRejectNegativeStockValidation() {
        assertThatThrownBy(() -> service.validateStockValues(-1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Available stock cannot be negative");
    }
}
