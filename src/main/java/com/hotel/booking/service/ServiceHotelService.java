package com.hotel.booking.service;

import com.hotel.booking.dto.serviceHotel.BookingServiceHotel;
import com.hotel.booking.dto.serviceHotel.BookingServiceRoom;
import com.hotel.booking.dto.serviceHotel.CreateServiceHotel;
import com.hotel.booking.dto.serviceHotel.UpdateServiceHotel;
import com.hotel.booking.model.Bill;
import com.hotel.booking.model.Booking;
import com.hotel.booking.model.BookingRoom;
import com.hotel.booking.model.ServiceHotel;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface ServiceHotelService {
    ResponseEntity<?> addServiceHotel(CreateServiceHotel serviceHotel, Principal principal);
    ResponseEntity<?> updateServiceHotel(UpdateServiceHotel serviceHotel, Principal principal);
    ResponseEntity<?> deleteServiceHotel(Integer id);
    ResponseEntity<?> getServiceHotelById(Long id);
    ResponseEntity<?> getAllServiceHotels();
    ResponseEntity<?> getAllCategory();
    ResponseEntity<?> getByCategory();
    ResponseEntity<?> serviceHotelById(Long id);
    ResponseEntity<?> bookingServiceHotel(BookingServiceHotel bookingServiceHotel, Principal principal) throws Exception;
    ResponseEntity<?> checkBookingStatus(Long bookingId,String transId) throws Exception;
    ResponseEntity<?> serviceRoomDetail(int serviceRoomId,Principal principal);
    ResponseEntity<?> bookingServiceRoom(BookingServiceRoom bookingServiceRoom, Principal principal) throws Exception;
    ResponseEntity<?> checkBookingRoomStatus(int paymentId,String transId) throws Exception;
    void checkPaymentsAsync(Booking booking, BookingRoom bookingRoom, Bill bill);
}
