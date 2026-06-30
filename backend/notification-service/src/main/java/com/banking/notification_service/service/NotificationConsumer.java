package com.banking.notification_service.service;

import com.banking.notification_service.dto.PaymentCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j // This gives us the 'log' object to print to the console
public class NotificationConsumer {

    @KafkaListener(
            topics = "payment-completed-topic", 
            groupId = "notification-group"
    )
    public void consumePaymentEvent(PaymentCompletedEvent event) {
        log.info("======================================================");
        log.info("🔔 NEW KAFKA EVENT RECEIVED!");
        log.info("Processing Email/SMS Notification...");
        log.info("Reference Number: {}", event.getReferenceNumber());
        log.info("Amount Transferred: ₹{}", event.getAmount());
        log.info("From Account: {}", event.getFromAccount());
        log.info("To Account: {}", event.getToAccount());
        log.info("✅ Notification Sent Successfully!");
        log.info("======================================================");
        
        // In a real production app, this is where you would use JavaMailSender 
        // or an SMS API like Twilio to actually send the message!
    }
}