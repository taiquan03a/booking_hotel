package com.hotel.booking.controller;

import com.hotel.booking.dto.booking.CreateCart;
import com.hotel.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
}
