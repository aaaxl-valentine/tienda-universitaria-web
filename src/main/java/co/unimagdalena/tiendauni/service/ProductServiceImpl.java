package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.ProductDTOs.CreateProductRequest;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.ProductResponse;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.UpdateProductRequest;
import co.unimagdalena.tiendauni.NotFoundException.ConflictException;
import co.unimagdalena.tiendauni.NotFoundException.ResourceNotFoundException;
import co.unimagdalena.tiendauni.entity.Product;
import co.unimagdalena.tiendauni.entity.Category;
import co.unimagdalena.tiendauni.repository.ProductRepository;
import co.unimagdalena.tiendauni.repository.CategoryRepository;
import co.unimagdalena.tiendauni.repository.OrderItemRepository;
import co.unimagdalena.tiendauni.service.mappers.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        // Validación: SKU único
        if (productRepository.existsBySku(request.sku())) {
            throw new ConflictException("Ya existe un producto con el SKU '" + request.sku() + "'");
        }

        // Validación: precio mayor que cero
        if (request.price() == null || request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }

        // Validación: categoría existente
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("La categoría con ID " + request.categoryId() + " no existe"));

        Product product = ProductMapper.toEntity(request, category);
        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse createProductWithInventory(CreateProductRequest request, Integer initialStock, Integer minimumStock) {
        // Crear el producto primero
        ProductResponse product = createProduct(request);

        // Crear inventario inicial
        inventoryService.createInventoryForProduct(product.id(), initialStock, minimumStock);

        // Recargar el producto con el inventario
        return productRepository.findById(product.id())
                .map(ProductMapper::toResponse)
                .orElse(product);
    }

    @Override
    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(ProductMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product " + id + " no existe"));
    }

    @Override
    public ProductResponse findBySku(String sku) {
        return productRepository.findBySku(sku)
                .map(ProductMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product " + sku + " no existe"));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto con ID " + productId + " no encontrado"));

        // Validación: precio mayor que cero
        if (request.price() != null && request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }

        // Validación: categoría existente (si se proporciona)
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("La categoría con ID " + request.categoryId() + " no existe"));
            product.setCategory(category);
        }

        // Actualizar campos no nulos
        if (request.name() != null) product.setName(request.name());
        if (request.description() != null) product.setDescription(request.description());
        if (request.price() != null) product.setPrice(request.price());
        if (request.active() != null) product.setActive(request.active());

        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse setProductActive(Long productId, boolean active) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto con ID " + productId + " no encontrado"));

        // Si se está desactivando, verificar que no tenga pedidos activos
        if (!active) {
            boolean hasActiveOrders = orderItemRepository.findByProductId(productId)
                    .stream()
                    .anyMatch(orderItem -> {
                        String status = orderItem.getOrder().getStatus().name();
                        return "CREATED".equals(status) || "PAID".equals(status) || "SHIPPED".equals(status);
                    });

            if (hasActiveOrders) {
                throw new ConflictException("No se puede desactivar un producto con pedidos activos");
            }
        }

        product.setActive(active);
        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Override
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> findByCategory(Long categoryId) {
        // Validar que la categoría existe
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("La categoría con ID " + categoryId + " no existe");
        }

        return productRepository.findByCategoryId(categoryId).stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> findActiveProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product " + productId + " no existe");
        }
        productRepository.deleteById(productId);
    }
}
