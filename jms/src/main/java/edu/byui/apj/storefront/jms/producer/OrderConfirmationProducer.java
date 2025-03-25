package edu.byui.apj.storefront.jms.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmationProducer {
    private static final Logger log = LoggerFactory.getLogger(OrderConfirmationProducer.class);
    private final JmsTemplate jmsTemplate;

    public OrderConfirmationProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendOrderConfirmation(String orderId) {
        try {
            jmsTemplate.convertAndSend("orderQueue", orderId);
            log.info("Order confirmation message sent for order ID: {}", orderId);
        } catch (Exception e) {
            log.error("Error sending order confirmation message", e);
            throw e;
        }
    }
}
