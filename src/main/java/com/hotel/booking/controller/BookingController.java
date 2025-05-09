package com.hotel.booking.controller;

import com.hotel.booking.dto.booking.CheckBillRequest;
import com.hotel.booking.dto.booking.CreateCart;
import com.hotel.booking.dto.booking.CreateFeedback;
import com.hotel.booking.model.Feedback;
import com.hotel.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/book")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("add_cart")
    public ResponseEntity<?> addCart(@RequestBody CreateCart cart, Principal principal) {
        return bookingService.addToCart(cart,principal);
    }
    @GetMapping("get_cart")
    public ResponseEntity<?> getCart(Principal principal) {
        return bookingService.booking(principal);
    }
    @GetMapping("remove_cart/{bookingRoomId}")
    public ResponseEntity<?> removeCart(@PathVariable int bookingRoomId,Principal principal) {
        return bookingService.removeFromCart(principal,bookingRoomId);
    }
    @GetMapping("checkout")
    public ResponseEntity<?> checkout(Principal principal) {
        return bookingService.checkout(principal);
    }
    @PostMapping("edit_cart")
    public ResponseEntity<?> editCart(
            @RequestParam(defaultValue = "-1") int adult,
            @RequestParam(defaultValue = "-1") int child,
            @RequestParam(defaultValue = "-1") int infant,
            @RequestParam(defaultValue = "-1") String serviceId,
            @RequestParam int bookingRoomId,
            Principal principal
    ) {
        return bookingService.editCart(principal,adult,child,infant,serviceId,bookingRoomId);
    }
    @GetMapping("payment/{customerId}")
    public Map<String, Object> place(@PathVariable Long customerId,Principal principal) throws Exception {
        return bookingService.payment(principal,customerId);
    }
    @PostMapping("check_bill")
    public ResponseEntity<?> checkBill(@RequestBody CheckBillRequest check) throws Exception {
            return bookingService.checkBill(check.getTransId(),check.getPaymentId());
    }
    @GetMapping("history")
    public ResponseEntity<?> history() {
        return bookingService.historyBooking();
    }
    @PostMapping("feedback")
    public ResponseEntity<?> feedback(@RequestBody CreateFeedback feedback, Principal principal) {
        return bookingService.sendFeedback(feedback,principal);
    }

    @GetMapping("checkin/{bookingId}")
    public Map<String, Object> checkin(@PathVariable int bookingId, Principal principal) throws Exception {
        return bookingService.checkIn(bookingId,principal);
    }

    @GetMapping("checkout/{bookingId}")
    public ResponseEntity<?> checkout(@PathVariable int bookingId, Principal principal){
        return bookingService.checkOut(bookingId,principal);
    }
}
