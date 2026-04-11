package com.shopwise.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.shopwise.entity.Payment;
import com.shopwise.entity.PaymentStatus;
import com.shopwise.event.InventoryReservedEvent;
import com.shopwise.event.PaymentEvent;
import com.shopwise.event.PaymentFailedEvent;
import com.shopwise.event.PaymentRequestEvent;
import com.shopwise.kafka.PaymentEventProducer;
import com.shopwise.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;
    private final RazorpayClient razorpayClient;

    @Value("${razorpay.currency}")
    private String currency;

    @Value("${razorpay.key-secret}")
    private String keySecret;


    @Transactional
    public void processPayment(PaymentRequestEvent event){
        if(paymentRepository.findByOrderId(event.getOrderId()).isPresent()){
            log.info("Payment already processed for OrderId: {}",event.getOrderId());
            return;
        }

        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .amount(event.getTotalAmount())
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        try {

//            boolean paymentSuccess = processWithPaymentGateway(event);
//
//            if(paymentSuccess){
//                payment.setPaymentStatus(PaymentStatus.SUCCESS);
//                payment.setTransactionId(UUID.randomUUID().toString());
//                payment.setUpdatedAt(LocalDateTime.now());
//                paymentRepository.save(payment);
//
//                paymentEventProducer.sendPaymentEvent(
//                        PaymentEvent.builder()
//                                .eventType("PAYMENT_SUCCESS")
//                                .orderId(event.getOrderId())
//                                .userId(event.getUserId())
//                                .amount(event.getTotalAmount())
//                                .transactionId(payment.getTransactionId())
//                                .occurredAt(LocalDateTime.now())
//                                .build()
//                );
//                log.info("Payment Success for orderId: {}",event.getOrderId());
//            } else {
//                handlePaymentFailure(payment,event,"Payment Declined By Gateway");
//            }
            String razorpayOrderId = createRazorpayOrder(event);
            payment.setRazorpayOrderId(razorpayOrderId);
            paymentRepository.save(payment);

            log.info("Razorpay order created: {} for orderId: {}",
                    razorpayOrderId, event.getOrderId());

//            paymentEventProducer.sendPaymentEvent(
//                    PaymentEvent.builder()
//                            .eventType("PAYMENT_INITIATED")
//                            .orderId(event.getOrderId())
//                            .userId(event.getUserId())
//                            .amount(event.getTotalAmount())
//                            .razorpayOrderId(razorpayOrderId)
//                            .occurredAt(LocalDateTime.now())
//                            .build()
//            );
            
        }catch (RazorpayException e){
            log.error("Razorpay order creation failed for orderId: {}", event.getOrderId(), e);
            handlePaymentFailure(payment,e.getMessage());
        }
    }

    private String createRazorpayOrder(PaymentRequestEvent event) throws RazorpayException{
        JSONObject orderRequest = new JSONObject();

        orderRequest.put("amount",
                event.getTotalAmount().multiply(BigDecimal.valueOf(100)).intValue());
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "order_"+event.getOrderId());

        JSONObject notes = new JSONObject();
        notes.put("orderId", event.getOrderId().toString());
        notes.put("userId", event.getUserId().toString());
        orderRequest.put("notes", notes);

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        return razorpayOrder.get("id");
    }

    @Transactional
    public void handlePaymentSuccess(String razorpayOrderId,
                                     String razorpayPaymentId,
                                     String razorpaySignature) {
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException(
                        "Payment not found for razorpayOrderId: " + razorpayOrderId));

        try {
            verifyPaymentSignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);

            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(razorpayPaymentId);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            paymentEventProducer.sendPaymentEvent(
                    PaymentEvent.builder()
                            .eventType("PAYMENT_SUCCESS")
                            .orderId(payment.getOrderId())
                            .userId(payment.getUserId())
                            .amount(payment.getAmount())
                            .transactionId(razorpayPaymentId)
                            .occurredAt(LocalDateTime.now())
                            .build()
            );
            log.info("Payment SUCCESS for orderId: {}", payment.getOrderId());

        } catch (RazorpayException e) {
            log.error("Signature verification failed for razorpayOrderId: {}", razorpayOrderId);
            handlePaymentFailure(payment,"Signature verification failed");
        }
    }

    private void verifyPaymentSignature(String orderId,
                                        String paymentId,
                                        String signature) throws RazorpayException {
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", orderId);
        options.put("razorpay_payment_id", paymentId);
        options.put("razorpay_signature", signature);
        Utils.verifyPaymentSignature(options, keySecret); // Throws if invalid
    }

    private void handlePaymentFailure(Payment payment,
                                      String reason) {
        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        paymentEventProducer.sendPaymentEvent(
                PaymentEvent.builder()
                        .eventType("PAYMENT_FAILED")
                        .orderId(payment.getOrderId())
                        .userId(payment.getUserId())
                        .amount(payment.getAmount())
                        .razorpayOrderId(payment.getRazorpayOrderId())
                        .failureReason(reason)
                        .occurredAt(LocalDateTime.now())
                        .build()
        );
        log.info("Payment FAILED for orderId: {} Reason: {}",payment.getOrderId(), reason);
    }

    @Transactional
    public void handleWebhookPaymentFailure(String razorpayOrderId, String errorDescription) {
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for razorpayOrderId: " + razorpayOrderId));

        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setFailureReason(errorDescription);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        paymentEventProducer.sendPaymentEvent(
                PaymentEvent.builder()
                        .eventType("PAYMENT_FAILED")
                        .orderId(payment.getOrderId())
                        .userId(payment.getUserId())
                        .amount(payment.getAmount())
                        .razorpayOrderId(payment.getRazorpayOrderId())
                        .failureReason(errorDescription)
                        .occurredAt(LocalDateTime.now())
                        .build()
        );

        log.info("Webhook Payment FAILED for razorpayOrderId: {} | Reason: {}",
                razorpayOrderId, errorDescription);
    }
}
