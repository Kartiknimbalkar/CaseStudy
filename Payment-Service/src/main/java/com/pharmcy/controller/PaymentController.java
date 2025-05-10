package com.pharmcy.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import com.pharmcy.model.PaymentDto;
import com.pharmcy.service.PaymentService;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "http://localhost:5173")  // adjust for your React dev server
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // 1) Initiate payment for a given order ID
    @PostMapping("/makePayment/{orderId}")
    public PaymentDto makePayment(@PathVariable Long orderId) throws RazorpayException {
        return paymentService.makePayment(orderId);
    }

    // 2) Razorpay callback to update status
    @PostMapping("/paymentCallback")
    public void paymentCallback(@RequestBody Map<String, String> response) {
        paymentService.updateStatus(response);
//        return new RedirectView("http://localhost:5173/success");
    }
}
