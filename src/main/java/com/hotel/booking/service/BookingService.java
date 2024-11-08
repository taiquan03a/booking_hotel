package com.hotel.booking.service;

import com.hotel.booking.dto.booking.CreateCart;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface BookingService {
    ResponseEntity<?> addToCart(CreateCart createCart, Principal principal);
    ResponseEntity<?> booking(Principal principal);
}
