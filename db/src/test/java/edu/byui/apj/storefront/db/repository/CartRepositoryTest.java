package edu.byui.apj.storefront.db.repository;

import edu.byui.apj.storefront.db.model.Cart;
import edu.byui.apj.storefront.db.model.CardOrder;
import edu.byui.apj.storefront.db.model.Item;
import edu.byui.apj.storefront.db.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartRepository cartRepository;

    @Test
    void findCartsWithoutOrders_shouldReturnOnlyCartsNotInOrders() {
        // Create and persist test data
        Cart cart1 = new Cart();
        cart1.setId("cart1");
        cart1.setPersonId("user1");
        entityManager.persist(cart1);

        Cart cart2 = new Cart();
        cart2.setId("cart2");
        cart2.setPersonId("user2");
        entityManager.persist(cart2);

        // Create an order that uses cart1
        CardOrder order = new CardOrder();
        order.setCart(cart1);
        entityManager.persist(order);

        entityManager.flush();

        // Execute the query
        List<Cart> cartsWithoutOrders = cartRepository.findCartsWithoutOrders();

        // Verify results
        assertThat(cartsWithoutOrders)
                .hasSize(1)
                .extracting(Cart::getId)
                .containsExactly("cart2");
    }

    @Test
    void findCartsWithoutOrders_shouldReturnEmptyListWhenAllCartsHaveOrders() {
        // Create test carts
        Cart cart1 = new Cart();
        cart1.setId("cart1");
        entityManager.persist(cart1);

        Cart cart2 = new Cart();
        cart2.setId("cart2");
        entityManager.persist(cart2);

        // Create orders for both carts
        CardOrder order1 = new CardOrder();
        order1.setCart(cart1);
        entityManager.persist(order1);

        CardOrder order2 = new CardOrder();
        order2.setCart(cart2);
        entityManager.persist(order2);

        entityManager.flush();

        // Execute query
        List<Cart> result = cartRepository.findCartsWithoutOrders();

        // Verify
        assertThat(result).isEmpty();
    }

    @Test
    void findCartsWithoutOrders_shouldReturnAllCartsWhenNoOrdersExist() {
        // Create test carts
        Cart cart1 = new Cart();
        cart1.setId("cart1");
        cart1.setPersonId("user1");
        entityManager.persist(cart1);

        Cart cart2 = new Cart();
        cart2.setId("cart2");
        cart2.setPersonId("user2");
        entityManager.persist(cart2);

        // Add some items to test that they don't affect the query
        Item item1 = new Item();
        item1.setId(1L);
        item1.setCart(cart1);
        item1.setName("Product 1");
        entityManager.persist(item1);

        entityManager.flush();

        // Execute query
        List<Cart> result = cartRepository.findCartsWithoutOrders();

        // Verify
        assertThat(result)
                .hasSize(2)
                .extracting(Cart::getId)
                .containsExactlyInAnyOrder("cart1", "cart2");
    }

    @Test
    void findCartsWithoutOrders_shouldWorkWithCartsThatHaveItems() {
        // Create a cart with items
        Cart cartWithItems = new Cart();
        cartWithItems.setId("cartWithItems");
        entityManager.persist(cartWithItems);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setCart(cartWithItems);
        item1.setName("Test Item");
        item1.setPrice(9.99);
        entityManager.persist(item1);

        // Create a cart without items
        Cart cartWithoutItems = new Cart();
        cartWithoutItems.setId("cartWithoutItems");
        entityManager.persist(cartWithoutItems);

        // Create an order for cartWithoutItems
        CardOrder order = new CardOrder();
        order.setCart(cartWithoutItems);
        entityManager.persist(order);

        entityManager.flush();

        // Execute query
        List<Cart> result = cartRepository.findCartsWithoutOrders();

        // Verify only cartWithItems is returned (since cartWithoutItems has an order)
        assertThat(result)
                .hasSize(1)
                .extracting(Cart::getId)
                .containsExactly("cartWithItems");
    }
}