package com.pharmcy.service;

import java.time.LocalDateTime;
import java.util.Map;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pharmcy.model.PaymentDto;
import com.pharmcy.model.repo.PaymentRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private RazorpayClient client;

    @Autowired
    private PaymentRepo paymentRepo;

    @PostConstruct
    public void init() throws RazorpayException {
        this.client = new RazorpayClient(keyId, keySecret);
    }

    /**
     * 1) Create a Razorpay order stub.
     *    Expects req map with keys "total" and "clientOrderRef".
     */
    public PaymentDto createOrder(Map<String, String> req) throws RazorpayException {
        // parse & validate
        String totalStr = req.get("total");
        if (totalStr == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing total");
        }
        double totalRu = Double.parseDouble(totalStr);
        if (totalRu < 1.0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum amount is ₹1");
        }

        String refStr = req.get("clientOrderRef");
        if (refStr == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing clientOrderRef");
        }
        long clientRef = Long.parseLong(refStr);

        // build Razorpay order options
        JSONObject opts = new JSONObject()
            .put("amount", (int) Math.round(totalRu * 100))
            .put("currency", "INR")
            .put("receipt", "ref_" + clientRef)
            .put("payment_capture", 1);

        // create Razorpay order
        Order rzOrder = client.orders.create(opts);

        // persist stub in our DB
        PaymentDto dto = new PaymentDto();
        dto.setRazorpayOrderId(rzOrder.get("id"));
        dto.setClientOrderRef(clientRef);
        dto.setTotal(totalRu);
        dto.setOrderStatus(rzOrder.get("status")); // usually "created"
        paymentRepo.save(dto);

        return dto;
    }

    /**
     * 2) On Razorpay success callback
     */
    public void confirmPayment(Map<String, String> resp) {
        String rzOrderId = resp.get("razorpay_order_id");
        String rzPaymentId = resp.get("razorpay_payment_id");

        // Step 1: Fetch existing payment
        PaymentDto payment = paymentRepo.findByRazorpayOrderId(rzOrderId);

        // Step 2: Validation
        if (payment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment record not found for order ID: " + rzOrderId);
        }

        // Step 3: Update only if not already confirmed
        if (!"PAYMENT_DONE".equals(payment.getOrderStatus())) {
            payment.setOrderStatus("PAYMENT_DONE");
            payment.setRazorpayPaymentId(rzPaymentId);
//            payment.setUpdatedAt(LocalDateTime.now()); // Optional: for audit
            paymentRepo.save(payment);
        } else {
            // Optional: log or ignore if already confirmed
            log.info("Payment already confirmed for order ID: {}", rzOrderId);
        }
    }


    /**
     * 3) On Razorpay failure or user dismiss
     */
    public void failPayment(Map<String, String> resp) {
        String rzOrderId = resp.get("razorpay_order_id");
        PaymentDto dto = paymentRepo.findByRazorpayOrderId(rzOrderId);
        if (dto == null) {
            log.warn("Unknown RazorpayOrderId in failure callback: {}", rzOrderId);
            return;
        }
        dto.setOrderStatus("FAILED");
        paymentRepo.save(dto);
    }

	public ResponseEntity<String> updateId(Map<String, String> data) {
		
		String rzOrderId = data.get("razorpay_order_id");
		String order_id = data.get("order_id");
		
		PaymentDto payment = paymentRepo.findByRazorpayOrderId(rzOrderId);
		
		if (payment == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record not found");
		}
		payment.setOrder_id(Long.parseLong(order_id));
		paymentRepo.save(payment);
		return ResponseEntity.ok("Order Id Attached");
	}
}
