package edu.byui.apj.storefront.db.service;

import edu.byui.apj.storefront.db.model.CardOrder;
import edu.byui.apj.storefront.db.model.Cart;
import edu.byui.apj.storefront.db.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderService orderService;

    private Cart testCart;
    private CardOrder testOrder;

    @BeforeEach
    void setUp() {
        testCart = new Cart();
        testCart.setId("testCart123");

        testOrder = new CardOrder();
        testOrder.setId(1L);
        testOrder.setCart(testCart);
    }

    @Test
    void saveOrder_shouldSaveOrderWithValidCart() {
        // Arrange
        when(cartService.getCart("testCart123")).thenReturn(testCart);
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        // Act
        CardOrder result = orderService.saveOrder(testOrder);

        // Assert
        assertNotNull(result);
        assertEquals(testCart, result.getCart());
        verify(cartService).getCart("testCart123");
        verify(orderRepository).save(testOrder);
    }

    @Test
    void saveOrder_shouldThrowExceptionWhenCartNotFound() {
        // Arrange
        when(cartService.getCart("invalidCart")).thenThrow(new RuntimeException("Cart not found"));

        CardOrder invalidOrder = new CardOrder();
        invalidOrder.setCart(new Cart());
        invalidOrder.getCart().setId("invalidCart");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.saveOrder(invalidOrder));
        verify(cartService).getCart("invalidCart");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void saveOrder_shouldHandleNullCart() {
        // Arrange
        CardOrder orderWithoutCart = new CardOrder();
        orderWithoutCart.setId(2L);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> orderService.saveOrder(orderWithoutCart));
        verify(cartService, never()).getCart(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrder_shouldReturnOrderWhenExists() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        Optional<CardOrder> result = orderService.getOrder(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testOrder, result.get());
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrder_shouldReturnEmptyWhenOrderNotFound() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<CardOrder> result = orderService.getOrder(99L);

        // Assert
        assertTrue(result.isEmpty());
        verify(orderRepository).findById(99L);
    }

    @Test
    void saveOrder_shouldUpdateCartReference() {
        // Arrange
        Cart newCart = new Cart();
        newCart.setId("newCart123");

        CardOrder orderWithNewCart = new CardOrder();
        orderWithNewCart.setId(3L);
        orderWithNewCart.setCart(testCart); // Original cart

        when(cartService.getCart("newCart123")).thenReturn(newCart);
        when(orderRepository.save(any(CardOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Change the cart reference
        orderWithNewCart.setCart(newCart);

        // Act
        CardOrder result = orderService.saveOrder(orderWithNewCart);

        // Assert
        assertNotNull(result);
        assertEquals(newCart, result.getCart());
        verify(cartService).getCart("newCart123");
        verify(orderRepository).save(orderWithNewCart);
    }
}