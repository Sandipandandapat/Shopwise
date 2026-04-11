package com.shopwise.controller;

import com.razorpay.Utils;
import com.shopwise.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class RazorpayWebhookController {

    private final PaymentService paymentService;

    @Value("${razorpay.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/razorpay")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("X-Razorpay-Signature") String signature){

        try {
            Utils.verifyWebhookSignature(payload, signature, webhookSecret);

            JSONObject event = new JSONObject(payload);
            String eventType = event.getString("event");

            log.info("Received Razorpay webhook: {}", eventType);

            if ("payment.captured".equals(eventType)) {
                JSONObject paymentEntity = event
                        .getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity");
                String razorpayOrderId = paymentEntity.getString("order_id");
                String razorpayPaymentId = paymentEntity.getString("id");
                paymentService.handlePaymentSuccess(
                        razorpayOrderId, razorpayPaymentId, signature
                );
            } else if ("payment.failed".equals(eventType)) {
                JSONObject paymentEntity = event
                        .getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity");

                String razorpayOrderId = paymentEntity.getString("order_id");
                String errorCode = paymentEntity.optString("error_code", "UNKNOWN");
                String errorDescription = paymentEntity.optString("error_description", "Payment failed");

                paymentService.handleWebhookPaymentFailure(razorpayOrderId, errorDescription);
            }

            return ResponseEntity.ok("Webhook processed");

        } catch (Exception e){
            log.error("Invalid webhook signature", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }
    }

}
