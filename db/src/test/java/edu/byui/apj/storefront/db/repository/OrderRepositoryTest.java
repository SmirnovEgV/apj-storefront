package edu.byui.apj.storefront.db.repository;

import edu.byui.apj.storefront.db.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private CardOrder createTestOrder(String cartId) {
        // Create related entities first
        Customer customer = new Customer();
        customer.setFirstName("Test");
        customer.setLastName("Customer");
        customer.setEmail("test@example.com");
        entityManager.persist(customer);

        Address address = new Address();
        address.setAddressLine1("123 Main St");
        address.setCity("Rexburg");
        address.setState("ID");
        address.setZipCode("83440");
        address.setCountry("USA");
        entityManager.persist(address);

        // Initialize Cart with unique ID
        Cart cart = new Cart();
        cart.setId(cartId); // Use parameterized ID
        entityManager.persist(cart);

        // Create the order
        CardOrder order = new CardOrder();
        order.setCustomer(customer);
        order.setShippingAddress(address);
        order.setCart(cart);
        order.setOrderDate(new Date());
        order.setConfirmationSent(false);
        order.setShipMethod("Standard");
        order.setOrderNotes("Test order");
        order.setSubtotal(100.0);
        order.setTotal(110.0);
        order.setTax(10.0);

        return order;
    }

    @Test
    public void whenFindById_thenReturnOrderWithAllFields() {
        // given
        CardOrder order = createTestOrder("Id0942");
        entityManager.persist(order);
        entityManager.flush();

        // when
        Optional<CardOrder> found = orderRepository.findById(order.getId());

        // then
        assertThat(found).isPresent();
        CardOrder retrieved = found.get();
        assertThat(retrieved.getCustomer()).isNotNull();
        assertThat(retrieved.getShippingAddress()).isNotNull();
        assertThat(retrieved.getCart()).isNotNull();
        assertThat(retrieved.getOrderDate()).isNotNull();
        assertThat(retrieved.isConfirmationSent()).isFalse();
        assertThat(retrieved.getShipMethod()).isEqualTo("Standard");
        assertThat(retrieved.getOrderNotes()).isEqualTo("Test order");
        assertThat(retrieved.getSubtotal()).isEqualTo(100.0);
        assertThat(retrieved.getTotal()).isEqualTo(110.0);
        assertThat(retrieved.getTax()).isEqualTo(10.0);
    }

    @Test
    public void whenFindAll_thenReturnAllOrders() {
        // given
        CardOrder order1 = createTestOrder("cart1");
        CardOrder order2 = createTestOrder("cart2");
        order2.setOrderNotes("Second order");

        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.flush();

        // when
        List<CardOrder> orders = orderRepository.findAll();

        // then
        assertThat(orders).hasSize(2);
        assertThat(orders).extracting(CardOrder::getOrderNotes)
                .containsExactlyInAnyOrder("Test order", "Second order");
    }

    @Test
    public void whenSaveOrder_thenAllFieldsArePersisted() {
        // given
        CardOrder order = createTestOrder("Id753dws");

        // when
        CardOrder saved = orderRepository.save(order);

        // then
        CardOrder found = entityManager.find(CardOrder.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getCustomer()).isNotNull();
        assertThat(found.getShippingAddress()).isNotNull();
        assertThat(found.getCart()).isNotNull();
        assertThat(found.getOrderDate()).isNotNull();
    }

    @Test
    public void whenDeleteOrder_thenOrderIsRemoved() {
        // given
        CardOrder order = createTestOrder("Id5643aa");
        entityManager.persist(order);
        entityManager.flush();

        // when
        orderRepository.deleteById(order.getId());

        // then
        assertThat(entityManager.find(CardOrder.class, order.getId())).isNull();
    }

    @Test
    public void whenUpdateOrder_thenChangesArePersisted() {
        // given
        CardOrder order = createTestOrder("Id5221mm");
        entityManager.persist(order);
        entityManager.flush();

        // when
        order.setConfirmationSent(true);
        order.setShipMethod("Express");
        order.setOrderNotes("Updated notes");
        order.setSubtotal(200.0);
        order.setTotal(220.0);
        order.setTax(20.0);
        orderRepository.save(order);

        // then
        CardOrder updated = entityManager.find(CardOrder.class, order.getId());
        assertThat(updated.isConfirmationSent()).isTrue();
        assertThat(updated.getShipMethod()).isEqualTo("Express");
        assertThat(updated.getOrderNotes()).isEqualTo("Updated notes");
        assertThat(updated.getSubtotal()).isEqualTo(200.0);
        assertThat(updated.getTotal()).isEqualTo(220.0);
        assertThat(updated.getTax()).isEqualTo(20.0);
    }

    @Test
    public void whenOrderHasNoNotes_thenNotesFieldIsNull() {
        // given
        CardOrder order = createTestOrder("Id0001129");
        order.setOrderNotes(null);
        entityManager.persist(order);
        entityManager.flush();

        // when
        CardOrder found = orderRepository.findById(order.getId()).orElseThrow();

        // then
        assertThat(found.getOrderNotes()).isNull();
    }
}