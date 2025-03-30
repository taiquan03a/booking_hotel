package com.hotel.booking.controller;

import com.hotel.booking.dto.placeRoom.PlaceRoomRequest;
import com.hotel.booking.dto.roomService.AddRoomService;
import com.hotel.booking.dto.roomService.ServiceRoomRequest;
import com.hotel.booking.dto.roomService.UpdateRoomService;
import com.hotel.booking.dto.user.CreateCustomer;
import com.hotel.booking.dto.user.CreateUserRequest;
import com.hotel.booking.dto.user.EditCustomer;
import com.hotel.booking.dto.user.EditUserRequest;
import com.hotel.booking.service.BookingService;
import com.hotel.booking.service.RoomService;
import com.hotel.booking.service.ServiceRoom;
import com.hotel.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    final private UserService userService;
    final private RoomService roomService;
    final private BookingService bookingService;
    final private ServiceRoom serviceRoom;

    @GetMapping("role")
    public ResponseEntity<?> getRole() {
        return userService.getRoles();
    }

    @GetMapping("user")
    public ResponseEntity<?> getUser() {
        return userService.getUsers();
    }

    @GetMapping("customer")
    public ResponseEntity<?> getCustomer() {
        return userService.getCustomers();
    }

    @PostMapping("user/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PostMapping("customer/create")
    public ResponseEntity<?> createCustomer(@RequestBody CreateCustomer request) {
        return userService.createCustomer(request);
    }

    @PutMapping("user/edit")
    public ResponseEntity<?> editUser(@RequestBody EditUserRequest request) {
        return userService.editUser(request);
    }

    @PutMapping("customer/edit")
    public ResponseEntity<?> editCustomer(@RequestBody EditCustomer request) {
        return userService.editCustomer(request);
    }

    @GetMapping("active/{id}")
    public ResponseEntity<?> getActiveUser(@PathVariable long id) {
        return userService.activeUser(id);
    }

    @GetMapping("search_room")
    public ResponseEntity<?> getSearchRoom(
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate,
            @RequestParam int adults,
            @RequestParam int children,
            @RequestParam int rankId
    ) {
        return roomService.searchRoomAdmin(checkInDate, checkOutDate, adults, children, rankId);
    }
    @PostMapping("place")
    public ResponseEntity<?> place(@RequestBody PlaceRoomRequest placeRoomRequest, Principal principal) {
        return roomService.placeRoom(placeRoomRequest,principal);
    }
    @GetMapping("dashboard")
    public ResponseEntity<?> getDashboard(@RequestParam(defaultValue = "day") String type,Principal principal) {
        return bookingService.dashBoard(type,principal);
    }
    @GetMapping("service_room")
    public ResponseEntity<?> serviceRoom(){
        return serviceRoom.view();
    }
    @GetMapping("service_room/{id}")
    public ResponseEntity<?> getServiceRoom(@PathVariable int id){
        return serviceRoom.viewDetail(id);
    }
    @PostMapping("service_room")
    public ResponseEntity<?> addServiceRoom(@ModelAttribute AddRoomService addRoomService) throws IOException {
        return serviceRoom.add(addRoomService);
    }
    @PutMapping("service_room")
    public ResponseEntity<?> updateServiceRoom(@ModelAttribute UpdateRoomService updateRoomService) throws IOException {
        return serviceRoom.update(updateRoomService);
    }
    @GetMapping("service_room/active/{id}")
    public ResponseEntity<?> addServiceRoom(@PathVariable int id){
        return serviceRoom.delete(id);
    }
}
