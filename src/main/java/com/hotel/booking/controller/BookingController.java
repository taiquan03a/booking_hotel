package com.hotel.booking.controller;

import com.hotel.booking.dto.booking.CreateCart;
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
    @PostMapping("place")
    public Map<String, Object> place(Principal principal){
        return null;
    }
}
