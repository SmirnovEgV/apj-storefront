package edu.byui.apj.storefront.db.controller;

import edu.byui.apj.storefront.db.model.CardOrder;
import edu.byui.apj.storefront.db.model.Cart;
import edu.byui.apj.storefront.db.service.OrderService;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private CardOrder testOrder;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

        testCart = new Cart();
        testCart.setId("testCart123");

        testOrder = new CardOrder();
        testOrder.setId(1L);
        testOrder.setCart(testCart);
    }

    @Test
    void saveOrder_shouldCreateNewOrder() throws Exception {
        // Arrange
        when(orderService.saveOrder(any(CardOrder.class))).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cart.id").value("testCart123"));

        verify(orderService).saveOrder(any(CardOrder.class));
    }

//    @Test
//    void saveOrder_shouldReturnBadRequestWhenInvalidInput() throws Exception {
//        // Act & Assert (empty body)
//        mockMvc.perform(post("/order")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void getOrder_shouldReturnOrderWhenExists() throws Exception {
        // Arrange
        when(orderService.getOrder(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        mockMvc.perform(get("/order/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cart.id").value("testCart123"));

        verify(orderService).getOrder(1L);
    }

    @Test
    void getOrder_shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        // Arrange
        when(orderService.getOrder(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/order/99"))
                .andExpect(status().isNotFound());

        verify(orderService).getOrder(99L);
    }

    @Test
    void allEndpoints_shouldHaveCorsEnabled() throws Exception {
        // Test CORS headers for one endpoint (they'll be the same for all)
        mockMvc.perform(post("/order")
                        .header("Origin", "http://localhost:8080")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:8080"));
    }

    @Test
    void endpoints_shouldRejectNonAllowedOrigins() throws Exception {
        // Test with non-allowed origin
        mockMvc.perform(post("/order")
                        .header("Origin", "http://unallowed-origin.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}