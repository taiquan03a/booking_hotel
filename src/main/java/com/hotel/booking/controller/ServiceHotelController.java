package com.hotel.booking.controller;

import com.hotel.booking.dto.serviceHotel.BookingServiceHotel;
import com.hotel.booking.dto.serviceHotel.BookingServiceRoom;
import com.hotel.booking.dto.serviceHotel.CreateServiceHotel;
import com.hotel.booking.dto.serviceHotel.UpdateServiceHotel;
import com.hotel.booking.model.Booking;
import com.hotel.booking.service.ServiceHotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/service_hotel")
@RequiredArgsConstructor
public class ServiceHotelController {
    final private ServiceHotelService serviceHotelService;

    @GetMapping
    public ResponseEntity<?> getServiceHotel() {
        return serviceHotelService.getAllServiceHotels();
    }
    @PostMapping("create")
    public ResponseEntity<?> addServiceHotel(@ModelAttribute CreateServiceHotel serviceHotel, Principal principal) {
        return serviceHotelService.addServiceHotel(serviceHotel,principal);
    }
    @PutMapping("edit")
    public ResponseEntity<?> updateServiceHotel(@ModelAttribute UpdateServiceHotel serviceHotel, Principal principal) {
        return serviceHotelService.updateServiceHotel(serviceHotel,principal);
    }
    @GetMapping("active/{id}")
    public ResponseEntity<?> active(@PathVariable("id") Integer id){
        return serviceHotelService.deleteServiceHotel(id);
    }
    @GetMapping("category/getAll")
    public ResponseEntity<?> getAllServiceHotelCategory(){
        return serviceHotelService.getAllCategory();
    }
    @GetMapping("category/get-service")
    private ResponseEntity<?> getServiceHotelCategory(){
        return serviceHotelService.getByCategory();
    }

    @PostMapping("booking")
    public ResponseEntity<?> addBooking(@RequestBody BookingServiceHotel bookingServiceHotel, Principal principal) throws Exception {
        return serviceHotelService.bookingServiceHotel(bookingServiceHotel,principal);
    }
    @GetMapping("check_status_booking")
    public ResponseEntity<?> checkBookingStatus(@RequestParam Long bookingHotelId,@RequestParam String transId) throws Exception {
        return serviceHotelService.checkBookingStatus(bookingHotelId,transId);
    }
    @GetMapping("service_room_detail")
    public ResponseEntity<?> serviceRoomDetail(@RequestParam int serviceRoomId ,Principal principal){
        return serviceHotelService.serviceRoomDetail(serviceRoomId,principal);
    }
    @PostMapping("booking_service_room")
    public ResponseEntity<?> bookingServiceRoom(@RequestBody BookingServiceRoom bookingServiceRoom, Principal principal) throws Exception {
        return serviceHotelService.bookingServiceRoom(bookingServiceRoom,principal);
    }
    @GetMapping("check_status_room")
    public ResponseEntity<?> checkStatusRoom(@RequestParam int paymentId ,String transId) throws Exception {
        return serviceHotelService.checkBookingRoomStatus(paymentId,transId);
    }
}
