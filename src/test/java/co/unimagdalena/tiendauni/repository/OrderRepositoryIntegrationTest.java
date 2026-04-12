package co.unimagdalena.tiendauni.repository;

import co.unimagdalena.tiendauni.entity.Address;
import co.unimagdalena.tiendauni.entity.Customer;
import co.unimagdalena.tiendauni.entity.Order;
import co.unimagdalena.tiendauni.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryIntegrationTest extends AbstractRepositoryIT {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void buscarPedidosPorFiltrosCompuestos() {
        Customer clienteUno = customerRepository.save(Customer.builder()
                .firstName("Juan")
                .lastName("Lopez")
                .email("juan.lopez@test.com")
                .build());
        Customer clienteDos = customerRepository.save(Customer.builder()
                .firstName("Sara")
                .lastName("Ruiz")
                .email("sara.ruiz@test.com")
                .build());

        Address direccionUno = addressRepository.save(Address.builder()
                .street("Calle 10")
                .city("Santa Marta")
                .department("Magdalena")
                .postalCode("470001")
                .customer(clienteUno)
                .build());
        Address direccionDos = addressRepository.save(Address.builder()
                .street("Carrera 5")
                .city("Santa Marta")
                .department("Magdalena")
                .postalCode("470001")
                .customer(clienteDos)
                .build());

        orderRepository.save(Order.builder()
                .customer(clienteUno)
                .address(direccionUno)
                .status(OrderStatus.PAID)
                .total(new BigDecimal("100.00"))
                .build());

        Order pedidoEsperado = orderRepository.save(Order.builder()
                .customer(clienteUno)
                .address(direccionUno)
                .status(OrderStatus.SHIPPED)
                .total(new BigDecimal("250.00"))
                .build());
        actualizarFechaPedido(pedidoEsperado.getId(), LocalDateTime.of(2026, 2, 10, 10, 0));

        orderRepository.save(Order.builder()
                .customer(clienteUno)
                .address(direccionUno)
                .status(OrderStatus.CANCELLED)
                .total(new BigDecimal("300.00"))
                .build());

        Order pedidoOtroCliente = orderRepository.save(Order.builder()
                .customer(clienteDos)
                .address(direccionDos)
                .status(OrderStatus.SHIPPED)
                .total(new BigDecimal("260.00"))
                .build());
        actualizarFechaPedido(pedidoOtroCliente.getId(), LocalDateTime.of(2026, 2, 10, 10, 0));

        List<Order> encontrados = orderRepository.findOrdersByFilters(
                clienteUno.getId(),
                OrderStatus.SHIPPED,
                LocalDateTime.of(2026, 2, 1, 0, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59),
                new BigDecimal("200.00"),
                new BigDecimal("300.00"));

        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).getId()).isEqualTo(pedidoEsperado.getId());
    }

    @Test
    void calcularIngresosMensuales() {
        Customer cliente = customerRepository.save(Customer.builder()
                .firstName("Mario")
                .lastName("Diaz")
                .email("mario.diaz@test.com")
                .build());
        Address direccion = addressRepository.save(Address.builder()
                .street("Avenida 1")
                .city("Santa Marta")
                .department("Magdalena")
                .postalCode("470001")
                .customer(cliente)
                .build());

        Order enero = orderRepository.save(Order.builder()
                .customer(cliente)
                .address(direccion)
                .status(OrderStatus.PAID)
                .total(new BigDecimal("100.00"))
                .build());
        actualizarFechaPedido(enero.getId(), LocalDateTime.of(2026, 1, 15, 10, 0));

        Order febreroUno = orderRepository.save(Order.builder()
                .customer(cliente)
                .address(direccion)
                .status(OrderStatus.SHIPPED)
                .total(new BigDecimal("200.00"))
                .build());
        actualizarFechaPedido(febreroUno.getId(), LocalDateTime.of(2026, 2, 10, 10, 0));

        Order febreroDos = orderRepository.save(Order.builder()
                .customer(cliente)
                .address(direccion)
                .status(OrderStatus.DELIVERED)
                .total(new BigDecimal("50.00"))
                .build());
        actualizarFechaPedido(febreroDos.getId(), LocalDateTime.of(2026, 2, 18, 10, 0));

        Order cancelado = orderRepository.save(Order.builder()
                .customer(cliente)
                .address(direccion)
                .status(OrderStatus.CANCELLED)
                .total(new BigDecimal("500.00"))
                .build());
        actualizarFechaPedido(cancelado.getId(), LocalDateTime.of(2026, 1, 20, 10, 0));

        List<Object[]> ingresos = orderRepository.findMonthlyRevenue(
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 3, 31, 23, 59));

        assertThat(ingresos).hasSize(2);
        assertThat(ingresos.get(0)[0]).isEqualTo(2026);
        assertThat(ingresos.get(0)[1]).isEqualTo(1);
        assertThat((BigDecimal) ingresos.get(0)[2]).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(ingresos.get(1)[0]).isEqualTo(2026);
        assertThat(ingresos.get(1)[1]).isEqualTo(2);
        assertThat((BigDecimal) ingresos.get(1)[2]).isEqualByComparingTo(new BigDecimal("250.00"));
    }

    @Test
    void obtenerClientesConMayorFacturacion() {
        Customer clienteTop = customerRepository.save(Customer.builder()
                .firstName("Camila")
                .lastName("Rojas")
                .email("camila.rojas@test.com")
                .build());
        Customer clienteSegundo = customerRepository.save(Customer.builder()
                .firstName("Nicolas")
                .lastName("Mora")
                .email("nicolas.mora@test.com")
                .build());

        Address direccionTop = addressRepository.save(Address.builder()
                .street("Calle 100")
                .city("Santa Marta")
                .department("Magdalena")
                .postalCode("470001")
                .customer(clienteTop)
                .build());
        Address direccionSegundo = addressRepository.save(Address.builder()
                .street("Calle 101")
                .city("Santa Marta")
                .department("Magdalena")
                .postalCode("470001")
                .customer(clienteSegundo)
                .build());

        Order topUno = orderRepository.save(Order.builder()
                .customer(clienteTop)
                .address(direccionTop)
                .status(OrderStatus.PAID)
                .total(new BigDecimal("300.00"))
                .build());
        actualizarFechaPedido(topUno.getId(), LocalDateTime.of(2026, 3, 1, 10, 0));

        Order topDos = orderRepository.save(Order.builder()
                .customer(clienteTop)
                .address(direccionTop)
                .status(OrderStatus.DELIVERED)
                .total(new BigDecimal("50.00"))
                .build());
        actualizarFechaPedido(topDos.getId(), LocalDateTime.of(2026, 3, 2, 10, 0));

        Order segundo = orderRepository.save(Order.builder()
                .customer(clienteSegundo)
                .address(direccionSegundo)
                .status(OrderStatus.SHIPPED)
                .total(new BigDecimal("200.00"))
                .build());
        actualizarFechaPedido(segundo.getId(), LocalDateTime.of(2026, 3, 3, 10, 0));

        Order ignorado = orderRepository.save(Order.builder()
                .customer(clienteSegundo)
                .address(direccionSegundo)
                .status(OrderStatus.CANCELLED)
                .total(new BigDecimal("900.00"))
                .build());
        actualizarFechaPedido(ignorado.getId(), LocalDateTime.of(2026, 3, 4, 10, 0));

        List<Object[]> topClientes = orderRepository.findTopCustomersByRevenue(
                LocalDateTime.of(2026, 3, 1, 0, 0),
                LocalDateTime.of(2026, 3, 31, 23, 59));

        assertThat(topClientes).hasSize(2);
        assertThat(((Customer) topClientes.get(0)[0]).getId()).isEqualTo(clienteTop.getId());
        assertThat((BigDecimal) topClientes.get(0)[1]).isEqualByComparingTo(new BigDecimal("350.00"));
        assertThat(((Customer) topClientes.get(1)[0]).getId()).isEqualTo(clienteSegundo.getId());
        assertThat((BigDecimal) topClientes.get(1)[1]).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    private void actualizarFechaPedido(Long orderId, LocalDateTime createdAt) {
        entityManager.createNativeQuery("UPDATE orders SET created_at = :createdAt WHERE id = :orderId")
                .setParameter("createdAt", Timestamp.valueOf(createdAt))
                .setParameter("orderId", orderId)
                .executeUpdate();
        entityManager.clear();
    }
}
