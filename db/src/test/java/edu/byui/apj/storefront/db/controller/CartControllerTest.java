package edu.byui.apj.storefront.db.controller;

import edu.byui.apj.storefront.db.model.Cart;
import edu.byui.apj.storefront.db.model.Item;
import edu.byui.apj.storefront.db.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private Cart testCart;
    private Item testItem;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();

        testCart = new Cart();
        testCart.setId("testCart123");
        testCart.setPersonId("user123");

        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Test Product");
        testItem.setPrice(9.99);
        testItem.setQuantity(1);
    }

    @Test
    void getCartNoOrder_shouldReturnListOfCarts() throws Exception {
        // Arrange
        List<Cart> carts = Arrays.asList(testCart);
        when(cartService.getCartsWithoutOrders()).thenReturn(carts);

        // Act & Assert
        mockMvc.perform(get("/cart/noorder"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("testCart123"))
                .andExpect(jsonPath("$[0].personId").value("user123"));

        verify(cartService).getCartsWithoutOrders();
    }

    @Test
    void getCart_shouldReturnCart() throws Exception {
        // Arrange
        when(cartService.getCart("testCart123")).thenReturn(testCart);

        // Act & Assert
        mockMvc.perform(get("/cart/testCart123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("testCart123"))
                .andExpect(jsonPath("$.personId").value("user123"));

        verify(cartService).getCart("testCart123");
    }

    @Test
    void getCart_shouldReturn404WhenNotFound() throws Exception {
        // Arrange
        when(cartService.getCart("nonexistent")).thenThrow(new RuntimeException("Cart not found"));

        // Act & Assert
        mockMvc.perform(get("/cart/nonexistent"))
                .andExpect(status().isNotFound());

        verify(cartService).getCart("nonexistent");
    }

    @Test
    void saveCart_shouldCreateNewCart() throws Exception {
        // Arrange
        when(cartService.saveCart(any(Cart.class))).thenReturn(testCart);

        // Act & Assert
        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCart)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("testCart123"));

        verify(cartService).saveCart(any(Cart.class));
    }

    @Test
    void addItemToCart_shouldAddItem() throws Exception {
        // Arrange
        when(cartService.addItemToCart(eq("testCart123"), any(Item.class))).thenReturn(testCart);

        // Act & Assert
        mockMvc.perform(post("/cart/testCart123/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("testCart123"));

        verify(cartService).addItemToCart(eq("testCart123"), any(Item.class));
    }

    @Test
    void removeCart_shouldDeleteCart() throws Exception {
        // Arrange
        doNothing().when(cartService).removeCart("testCart123");

        // Act & Assert
        mockMvc.perform(delete("/cart/testCart123"))
                .andExpect(status().isOk());

        verify(cartService).removeCart("testCart123");
    }

    @Test
    void removeItemFromCart_shouldRemoveItem() throws Exception {
        // Arrange
        when(cartService.removeItemFromCart("testCart123", 1L)).thenReturn(testCart);

        // Act & Assert
        mockMvc.perform(delete("/cart/testCart123/item/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("testCart123"));

        verify(cartService).removeItemFromCart("testCart123", 1L);
    }

    @Test
    void updateItemInCart_shouldUpdateItem() throws Exception {
        // Arrange
        when(cartService.updateCartItem(eq("testCart123"), any(Item.class))).thenReturn(testCart);

        // Act & Assert
        mockMvc.perform(put("/cart/testCart123/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("testCart123"));

        verify(cartService).updateCartItem(eq("testCart123"), any(Item.class));
    }

    @Test
    void allEndpoints_shouldHaveCorsEnabled() throws Exception {
        // Test CORS headers for one endpoint (they'll be the same for all)
        mockMvc.perform(get("/cart/noorder")
                        .header("Origin", "http://localhost:8080"))
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:8080"));
    }
}