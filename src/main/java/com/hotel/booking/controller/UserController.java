package com.hotel.booking.controller;

import com.hotel.booking.dto.booking.CreateCartUser;
import com.hotel.booking.dto.placeRoom.PlaceRoomRequest;
import com.hotel.booking.service.BookingService;
import com.hotel.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {
    final private RoomService roomService;
    final private BookingService bookingService;

    @PostMapping("place")
    public ResponseEntity<?> place(@RequestBody PlaceRoomRequest placeRoomRequest, Principal principal) {
        return roomService.placeRoom(placeRoomRequest, principal);
    }
    @PostMapping("add_cart")
    public ResponseEntity<?> selectRoom(@RequestBody CreateCartUser createCartUser, Principal principal) {
        return bookingService.userSelect(createCartUser, principal);
    }
    @PostMapping("payment")
    public Map<String, Object> payment(Principal principal) throws Exception {
        return bookingService.userPayment(principal);
    }
    @GetMapping("history")
    public ResponseEntity<?> history(Principal principal) {
        return bookingService.userHistoryBooking(principal);
    }
}
