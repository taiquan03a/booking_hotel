package com.hotel.booking.controller;

import com.hotel.booking.dto.serviceHotel.CreateServiceHotel;
import com.hotel.booking.dto.serviceHotel.UpdateServiceHotel;
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
    @PostMapping("active/{id}")
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

}
