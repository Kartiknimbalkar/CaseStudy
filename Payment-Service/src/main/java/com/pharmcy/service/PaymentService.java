package com.pharmcy.service;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pharmcy.model.OrderDto;
import com.pharmcy.model.PaymentDto;
import com.pharmcy.model.repo.OrderClient;
import com.pharmcy.model.repo.PaymentRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.annotation.PostConstruct;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private OrderClient orderClient;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private RazorpayClient razorpayClient;

    @PostConstruct
    public void init() throws RazorpayException {
        this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
    }

    public PaymentDto makePayment(Long orderId) throws RazorpayException {
        // 1) Retrieve order details
        OrderDto orderData = orderClient.getOrderById(orderId);

        // 2) Validate minimum amount
        double rupees = orderData.getTotalPrice();
        if (rupees < 1.0) {
            throw new IllegalArgumentException("Order amount must be at least ₹1.00");
        }

        // 3) Convert to paise safely
        double paise = rupees * 100;

        // 4) Create Razorpay order
        JSONObject options = new JSONObject();
        options.put("amount", (int) paise);
        options.put("currency", "INR");
        options.put("receipt", orderData.getDoctorEmail());  // optional

        Order razorpayOrder = razorpayClient.orders.create(options);

        // 5) Persist and return
        PaymentDto payment = new PaymentDto();
        payment.setOrderId(orderId);
        payment.setTotal(rupees);
        payment.setRazorpayOrderId(razorpayOrder.get("id"));
        payment.setOrderStatus(razorpayOrder.get("status"));

        return paymentRepo.save(payment);
    }

    public PaymentDto updateStatus(Map<String, String> params) {
        String rzOrderId = params.get("razorpay_order_id");
        if (rzOrderId == null) {
            throw new IllegalArgumentException("Missing Razorpay Order ID");
        }
        PaymentDto payment = paymentRepo.findByRazorpayOrderId(rzOrderId);
        if (payment == null) {
            throw new IllegalStateException("Payment record not found");
        }
        payment.setOrderStatus("PAYMENT_DONE");
        return paymentRepo.save(payment);
    }
}
