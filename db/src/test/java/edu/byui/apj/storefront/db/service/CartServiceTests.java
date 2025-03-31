package edu.byui.apj.storefront.db.service;

import edu.byui.apj.storefront.db.model.Cart;
import edu.byui.apj.storefront.db.model.Item;
import edu.byui.apj.storefront.db.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private Cart testCart;
    private Item testItem;

    @BeforeEach
    void setUp() {
        testCart = new Cart();
        testCart.setId("testCart123");
        testCart.setPersonId("user123");
        testCart.setItems(new ArrayList<>()); // Initialize the items list

        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Test Product");
        testItem.setPrice(9.99);
        testItem.setQuantity(1);
    }

    @Test
    void addItemToCart_shouldAddItemToExistingCart() {
        // Arrange
        when(cartRepository.findById("testCart123")).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Cart result = cartService.addItemToCart("testCart123", testItem);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(testItem, result.getItems().get(0));
        assertEquals(testCart, testItem.getCart());

        verify(cartRepository).findById("testCart123");
        verify(cartRepository).save(testCart);
    }

    @Test
    void removeItemFromCart_shouldRemoveItem() {
        // Arrange
        testCart.getItems().add(testItem); // Add item first
        when(cartRepository.findById("testCart123")).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Cart result = cartService.removeItemFromCart("testCart123", 1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        verify(cartRepository).findById("testCart123");
        verify(cartRepository).save(testCart);
    }

    @Test
    void removeItemFromCart_shouldHandleNonExistentItem() {
        // Arrange
        when(cartRepository.findById("testCart123")).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Cart result = cartService.removeItemFromCart("testCart123", 99L);

        // Assert
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void updateCartItem_shouldUpdateExistingItem() {
        // Arrange
        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setName("Old Name");
        existingItem.setPrice(5.99);
        testCart.getItems().add(existingItem);

        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setName("New Name");
        updatedItem.setPrice(7.99);

        when(cartRepository.findById("testCart123")).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Cart result = cartService.updateCartItem("testCart123", updatedItem);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        Item item = result.getItems().get(0);
        assertEquals("New Name", item.getName());
        assertEquals(7.99, item.getPrice());
        assertEquals(testCart, item.getCart());
    }

    // ... rest of your test methods remain the same ...
}