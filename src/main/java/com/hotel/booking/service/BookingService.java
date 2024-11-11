package com.hotel.booking.service;

import com.hotel.booking.dto.booking.CreateCart;
import com.hotel.booking.dto.booking.CreateCartUser;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Map;

public interface BookingService {
    ResponseEntity<?> addToCart(CreateCart createCart, Principal principal);
    ResponseEntity<?> booking(Principal principal);
    ResponseEntity<?> removeFromCart(Principal principal,int bookingRoomId);
    ResponseEntity<?> checkout(Principal principal);
    ResponseEntity<?> editCart(Principal principal, int adult, int child,int infant,String serviceId,int bookingRoomId);
    Map<String, Object> payment(Principal principal, Long customerId) throws Exception;
    ResponseEntity<?> checkBill(String transId,int paymentId) throws Exception;
    ResponseEntity<?> historyBooking();
    ResponseEntity<?> userSelect(CreateCartUser createCartUser, Principal principal);
    ResponseEntity<?> dashBoard(Principal principal);
}
