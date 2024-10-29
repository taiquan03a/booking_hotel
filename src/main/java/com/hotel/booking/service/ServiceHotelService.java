package com.hotel.booking.service;

import com.hotel.booking.dto.serviceHotel.CreateServiceHotel;
import com.hotel.booking.dto.serviceHotel.UpdateServiceHotel;
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
}
