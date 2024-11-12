package com.hotel.booking.controller;

import com.hotel.booking.dto.auth.OtpRequest;
import com.hotel.booking.dto.auth.ResetPassword;
import com.hotel.booking.dto.booking.CreateCartUser;
import com.hotel.booking.dto.placeRoom.PlaceRoomRequest;
import com.hotel.booking.service.BookingService;
import com.hotel.booking.service.RoomService;
import com.hotel.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {
    final private RoomService roomService;
    final private BookingService bookingService;
    final private UserService userService;

    @PostMapping("place")
    public ResponseEntity<?> place(@RequestBody PlaceRoomRequest placeRoomRequest, Principal principal) {
        return roomService.placeRoom(placeRoomRequest, principal);
    }
    @PostMapping("add_cart")
    public ResponseEntity<?> selectRoom(@RequestBody CreateCartUser createCartUser, Principal principal) {
        return bookingService.userSelect(createCartUser, principal);
    }
    @GetMapping("payment")
    public Map<String, Object> payment(Principal principal) throws Exception {
        return bookingService.userPayment(principal);
    }
    @GetMapping("history")
    public ResponseEntity<?> history(Principal principal) {
        return bookingService.userHistoryBooking(principal);
    }
    @GetMapping("check_email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        return userService.checkEmail(email);
    }
    @GetMapping("otp")
    public ResponseEntity<?> otp(@RequestBody OtpRequest otp) {
        return userService.checkOtp(otp);
    }
    @PostMapping("reset_password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPassword resetPassword){
        return userService.resetPassword(resetPassword);
    }
}
