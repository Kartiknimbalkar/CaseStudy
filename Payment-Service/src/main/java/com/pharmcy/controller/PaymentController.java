package com.pharmcy.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD

=======
>>>>>>> c9c4f00 (Updated code for the Payment service with failure handling)
import com.pharmcy.model.PaymentDto;
import com.pharmcy.service.PaymentService;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    @Autowired private PaymentService service;

    /** 1) Client calls this first, passing total₹ and a clientOrderRef */
    @PostMapping("/createOrder")
    public ResponseEntity<PaymentDto> createOrder(@RequestBody Map<String,String> req)
            throws RazorpayException {
        return ResponseEntity.ok(service.createOrder(req));
    }

    /** 2) Razorpay success callback from the browser JS handler */
    @PostMapping("/paymentCallback")
    public ResponseEntity<Void> callback(@RequestBody Map<String,String> resp) {
        service.confirmPayment(resp);
        return ResponseEntity.ok().build();
    }
<<<<<<< HEAD
    
    @PostMapping("/paymentFailureCallback")
    public String failureCallback(@RequestBody Map<String, String> response) {
    	paymentService.handleFailure(response);
    	return "Payment Failed and items restocked";
    }
=======

    /** 3) Razorpay failure or user dismiss */
    @PostMapping("/paymentFailureCallback")
    public ResponseEntity<Void> failure(@RequestBody Map<String,String> resp) {
        service.failPayment(resp);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/update")
    public ResponseEntity<String> updateId(@RequestBody Map<String, String> data) {
    	return service.updateId(data);
    }
    
>>>>>>> c9c4f00 (Updated code for the Payment service with failure handling)
}
