package com.hotel.booking.service;

import com.hotel.booking.dto.booking.CreateCart;
import com.hotel.booking.dto.booking.CreateCartUser;
import com.hotel.booking.dto.booking.CreateFeedback;
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
    ResponseEntity<?> userHistoryBooking(Principal principal);
    ResponseEntity<?> userSelect(CreateCartUser createCartUser, Principal principal);
    Map<String, Object> userPayment(Principal principal) throws Exception;
    ResponseEntity<?> dashBoard(String type,Principal connectedUser);
    ResponseEntity<?> sendFeedback(CreateFeedback createFeedback, Principal principal);

    //status booking
    ResponseEntity<?> checkIn(Integer bookingId,Principal principal) throws Exception;
    Map<String,Object> checkOut(Integer bookingId,Principal principal) throws Exception;
    ResponseEntity<?> checkBillCheckOut(String transId,int paymentId) throws Exception;
    ResponseEntity<?> cancelBooking(Integer bookingId,Principal principal);
}
