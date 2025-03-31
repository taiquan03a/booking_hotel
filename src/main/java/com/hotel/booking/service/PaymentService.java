package com.hotel.booking.service;

import com.hotel.booking.model.Bill;
import com.hotel.booking.model.Booking;
import com.hotel.booking.model.BookingRoom;

import java.util.concurrent.CompletableFuture;

public interface PaymentService {
    public void checkPaymentAsync(BookingRoom bookingRoom, Bill bill,String newServiceId,int servicePrice,String note);
}
